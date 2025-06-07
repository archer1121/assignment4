package controller.command;

import model.Event;
import model.EventLocation;
import model.EventStatus;
import model.ICalendar;
import model.IEvent;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Handles “edit event …” commands.  Supported forms:
 *
 *   1) edit event <property> <eventSubject> from <dateTime1> to <dateTime2> with <newValue>
 *   2) edit events <property> <eventSubject> from <dateTime> with <newValue>
 *   3) edit series <property> <eventSubject> from <dateTime> with <newValue>
 *
 * <property> must be one of: subject, start, end, description, location, status.
 *
 * For simplicity, this implementation locates the single Event whose subject and start‐time
 * match exactly, then replaces exactly one property.  It does not fully implement “series”
 * vs. “single instance” logic—both “edit events” and “edit series” branches simply change one event.
 */
public class EditCommand implements Command {

  // allowed properties that can be edited
  private static final Set<String> VALID_PROPERTIES = new HashSet<>(
          Arrays.asList("subject", "start", "end", "description", "location", "status")
  );

  private final String command;

  public EditCommand(String command) {
    this.command = command.trim();
  }

  @Override
  public void execute(ICalendar model) {
    String property;
    String subject;
    String fromDateTimeStr;
    String toDateTimeStr;   // only for the “edit event … to …” form
    String newValue;

    // ─── Case 1: “edit event <property> <subject> from <dt1> to <dt2> with <newValue>” ───
    if (command.startsWith("edit event ")
            && command.contains(" from ")
            && command.contains(" to ")
            && command.contains(" with ")) {

      // 1A) Extract the <property> immediately after “edit event”
      property = Command.getWordAfter("edit event", command);
      if (!VALID_PROPERTIES.contains(property)) {
        throw new IllegalArgumentException("Invalid property: \"" + property + "\"");
      }

      // 1B) Extract eventSubject (the word after the property)
      subject = Command.getWordAfter(property, command);
      if (subject.isEmpty()) {
        throw new IllegalArgumentException("Missing event subject in: \"" + command + "\"");
      }

      // 1C) Extract start‐dateTime after “from”
      fromDateTimeStr = Command.getWordAfter("from", command);
      if (fromDateTimeStr.isEmpty()) {
        throw new IllegalArgumentException("Missing start date‐time in: \"" + command + "\"");
      }
      LocalDateTime fromDT = LocalDateTime.parse(fromDateTimeStr);
      LocalDate fromDate = fromDT.toLocalDate();
      LocalTime fromTime = fromDT.toLocalTime();

      // 1D) Extract end‐dateTime after “to”
      toDateTimeStr = Command.getWordAfter("to", command);
      if (toDateTimeStr.isEmpty()) {
        throw new IllegalArgumentException("Missing end date‐time in: \"" + command + "\"");
      }
      LocalDateTime toDT = LocalDateTime.parse(toDateTimeStr);
      LocalDate toDate = toDT.toLocalDate();
      LocalTime toTime = toDT.toLocalTime();

      // 1E) Extract the newValue after “with”
      newValue = Command.getWordAfter("with", command);
      if (newValue.isEmpty()) {
        throw new IllegalArgumentException("Missing new value after 'with' in: \"" + command + "\"");
      }

      // ─── Locate the existing Event in the model’s schedule ───
      // We look for events on fromDate (the start date).  Then match subject+startTime exactly.
      List<Event> candidates = model.getScheduleInRange(fromDate, fromDate);
      Event oldEvent = null;
      for (Event e : candidates) {
        if (e.getSubject().equals(subject)
                && e.getStartDate().equals(fromDate)
                && e.getStartTime().equals(fromTime)) {
          oldEvent = e;
          break;
        }
      }
      if (oldEvent == null) {
        throw new IllegalArgumentException(
                "No matching event found with subject=\"" + subject +
                        "\" starting at " + fromDate + "T" + fromTime);
      }

      // ─── Build a new Event by copying EVERY field from oldEvent, then overwriting exactly one property ───
      Event.EventBuilder builder = Event.getBuilder()
              .subject(oldEvent.getSubject())
              .startDate(oldEvent.getStartDate().getDayOfMonth(),
                      oldEvent.getStartDate().getMonthValue(),
                      oldEvent.getStartDate().getYear())
              .startTime(oldEvent.getStartTime().getHour(),
                      oldEvent.getStartTime().getMinute())
              .endDate(oldEvent.getEndDate().getDayOfMonth(),
                      oldEvent.getEndDate().getMonthValue(),
                      oldEvent.getEndDate().getYear())
              .endTime(oldEvent.getEndTime().getHour(),
                      oldEvent.getEndTime().getMinute());

      // copy optional fields if present
      if (oldEvent.getLocation() != null) {
        builder.location(oldEvent.getLocation());
      }
      if (oldEvent.getStatus() != null) {
        builder.status(oldEvent.getStatus());
      }
      if (oldEvent.getDescription() != null) {
        builder.description(oldEvent.getDescription());
      }

      // Overwrite exactly the requested property with newValue
      switch (property) {
        case "subject":
          builder = builder.subject(newValue);
          break;

        case "start":
          // newValue should be an ISO‐8601 “YYYY-MM-DDThh:mm”
          LocalDateTime newStartDT = LocalDateTime.parse(newValue);
          builder = builder
                  .startDate(newStartDT.getDayOfMonth(),
                          newStartDT.getMonthValue(),
                          newStartDT.getYear())
                  .startTime(newStartDT.getHour(),
                          newStartDT.getMinute());
          break;

        case "end":
          LocalDateTime newEndDT = LocalDateTime.parse(newValue);
          builder = builder
                  .endDate(newEndDT.getDayOfMonth(),
                          newEndDT.getMonthValue(),
                          newEndDT.getYear())
                  .endTime(newEndDT.getHour(),
                          newEndDT.getMinute());
          break;

        case "description":
          builder = builder.description(newValue);
          break;

        case "location":
          // newValue should match an EventLocation enum name, e.g. "OFFICE" or "HOME"
          builder = builder.location(EventLocation.valueOf(newValue));
          break;

        case "status":
          // newValue should match an EventStatus enum name, e.g. "CONFIRMED", "TENTATIVE"
          builder = builder.status(EventStatus.valueOf(newValue));
          break;

        default:
          throw new IllegalArgumentException("Unsupported edit property: " + property);
      }

      // 1F) Build and replace
      IEvent updated = builder.buildEvent();
      model.editEvent(oldEvent, (Event) updated);
      return;
    }

    // ─── Case 2: “edit events <property> <subject> from <dateTime> with <newValue>” ───
    else if (command.startsWith("edit events ")
            && command.contains(" from ")
            && command.contains(" with ")
            && !command.contains(" to ")) {

      // 2A) Extract property
      property = Command.getWordAfter("edit events", command);
      if (!VALID_PROPERTIES.contains(property)) {
        throw new IllegalArgumentException("Invalid property: \"" + property + "\"");
      }

      // 2B) Extract subject
      subject = Command.getWordAfter(property, command);
      if (subject.isEmpty()) {
        throw new IllegalArgumentException("Missing event subject in: \"" + command + "\"");
      }

      // 2C) Extract start‐dateTime after “from”
      fromDateTimeStr = Command.getWordAfter("from", command);
      if (fromDateTimeStr.isEmpty()) {
        throw new IllegalArgumentException("Missing start date‐time in: \"" + command + "\"");
      }
      LocalDateTime fromDT = LocalDateTime.parse(fromDateTimeStr);
      LocalDate fromDate = fromDT.toLocalDate();
      LocalTime fromTime = fromDT.toLocalTime();

      // 2D) Extract newValue after “with”
      newValue = Command.getWordAfter("with", command);
      if (newValue.isEmpty()) {
        throw new IllegalArgumentException("Missing new value after 'with' in: \"" + command + "\"");
      }

      // Locate the existing Event
      List<Event> candidates = model.getScheduleInRange(fromDate, fromDate);
      Event oldEvent = null;
      for (Event e : candidates) {
        if (e.getSubject().equals(subject)
                && e.getStartDate().equals(fromDate)
                && e.getStartTime().equals(fromTime)) {
          oldEvent = e;
          break;
        }
      }
      if (oldEvent == null) {
        throw new IllegalArgumentException(
                "No matching event found with subject=\"" + subject +
                        "\" starting at " + fromDate + "T" + fromTime);
      }

      // Copy‐and‐overwrite (same as case 1, but no “to” field)
      Event.EventBuilder builder = Event.getBuilder()
              .subject(oldEvent.getSubject())
              .startDate(oldEvent.getStartDate().getDayOfMonth(),
                      oldEvent.getStartDate().getMonthValue(),
                      oldEvent.getStartDate().getYear())
              .startTime(oldEvent.getStartTime().getHour(),
                      oldEvent.getStartTime().getMinute())
              .endDate(oldEvent.getEndDate().getDayOfMonth(),
                      oldEvent.getEndDate().getMonthValue(),
                      oldEvent.getEndDate().getYear())
              .endTime(oldEvent.getEndTime().getHour(),
                      oldEvent.getEndTime().getMinute());

      if (oldEvent.getLocation() != null) {
        builder.location(oldEvent.getLocation());
      }
      if (oldEvent.getStatus() != null) {
        builder.status(oldEvent.getStatus());
      }
      if (oldEvent.getDescription() != null) {
        builder.description(oldEvent.getDescription());
      }

      switch (property) {
        case "subject":
          builder = builder.subject(newValue);
          break;
        case "start":
          LocalDateTime newStartDT = LocalDateTime.parse(newValue);
          builder = builder
                  .startDate(newStartDT.getDayOfMonth(),
                          newStartDT.getMonthValue(),
                          newStartDT.getYear())
                  .startTime(newStartDT.getHour(),
                          newStartDT.getMinute());
          break;
        case "end":
          LocalDateTime newEndDT = LocalDateTime.parse(newValue);
          builder = builder
                  .endDate(newEndDT.getDayOfMonth(),
                          newEndDT.getMonthValue(),
                          newEndDT.getYear())
                  .endTime(newEndDT.getHour(),
                          newEndDT.getMinute());
          break;
        case "description":
          builder = builder.description(newValue);
          break;
        case "location":
          builder = builder.location(EventLocation.valueOf(newValue));
          break;
        case "status":
          builder = builder.status(EventStatus.valueOf(newValue));
          break;
        default:
          throw new IllegalArgumentException("Unsupported property: " + property);
      }

      IEvent updated = builder.buildEvent();
      model.editEvent(oldEvent,(Event) updated);
      return;
    }

    // ─── Case 3: “edit series <property> <subject> from <dateTime> with <newValue>” ───
    else if (command.startsWith("edit series ")
            && command.contains(" from ")
            && command.contains(" with ")
            && !command.contains(" to ")) {

      // This is effectively the same as “edit events …” in our minimal implementation:
      property = Command.getWordAfter("edit series", command);
      if (!VALID_PROPERTIES.contains(property)) {
        throw new IllegalArgumentException("Invalid property: \"" + property + "\"");
      }

      subject = Command.getWordAfter(property, command);
      if (subject.isEmpty()) {
        throw new IllegalArgumentException("Missing event subject in: \"" + command + "\"");
      }

      fromDateTimeStr = Command.getWordAfter("from", command);
      if (fromDateTimeStr.isEmpty()) {
        throw new IllegalArgumentException("Missing start date‐time in: \"" + command + "\"");
      }
      LocalDateTime fromDT2 = LocalDateTime.parse(fromDateTimeStr);
      LocalDate fromDate2 = fromDT2.toLocalDate();
      LocalTime fromTime2 = fromDT2.toLocalTime();

      newValue = Command.getWordAfter("with", command);
      if (newValue.isEmpty()) {
        throw new IllegalArgumentException("Missing new value after 'with' in: \"" + command + "\"");
      }

      List<Event> candidates2 = model.getScheduleInRange(fromDate2, fromDate2);
      Event oldEvent2 = null;
      for (Event e : candidates2) {
        if (e.getSubject().equals(subject)
                && e.getStartDate().equals(fromDate2)
                && e.getStartTime().equals(fromTime2)) {
          oldEvent2 = e;
          break;
        }
      }
      if (oldEvent2 == null) {
        throw new IllegalArgumentException(
                "No matching event found with subject=\"" + subject +
                        "\" starting at " + fromDate2 + "T" + fromTime2);
      }

      Event.EventBuilder builder2 = Event.getBuilder()
              .subject(oldEvent2.getSubject())
              .startDate(oldEvent2.getStartDate().getDayOfMonth(),
                      oldEvent2.getStartDate().getMonthValue(),
                      oldEvent2.getStartDate().getYear())
              .startTime(oldEvent2.getStartTime().getHour(),
                      oldEvent2.getStartTime().getMinute())
              .endDate(oldEvent2.getEndDate().getDayOfMonth(),
                      oldEvent2.getEndDate().getMonthValue(),
                      oldEvent2.getEndDate().getYear())
              .endTime(oldEvent2.getEndTime().getHour(),
                      oldEvent2.getEndTime().getMinute());

      if (oldEvent2.getLocation() != null) {
        builder2.location(oldEvent2.getLocation());
      }
      if (oldEvent2.getStatus() != null) {
        builder2.status(oldEvent2.getStatus());
      }
      if (oldEvent2.getDescription() != null) {
        builder2.description(oldEvent2.getDescription());
      }

      switch (property) {
        case "subject":
          builder2 = builder2.subject(newValue);
          break;
        case "start":
          LocalDateTime ns = LocalDateTime.parse(newValue);
          builder2 = builder2
                  .startDate(ns.getDayOfMonth(), ns.getMonthValue(), ns.getYear())
                  .startTime(ns.getHour(), ns.getMinute());
          break;
        case "end":
          LocalDateTime ne = LocalDateTime.parse(newValue);
          builder2 = builder2
                  .endDate(ne.getDayOfMonth(), ne.getMonthValue(), ne.getYear())
                  .endTime(ne.getHour(), ne.getMinute());
          break;
        case "description":
          builder2 = builder2.description(newValue);
          break;
        case "location":
          builder2 = builder2.location(EventLocation.valueOf(newValue));
          break;
        case "status":
          builder2 = builder2.status(EventStatus.valueOf(newValue));
          break;
        default:
          throw new IllegalArgumentException("Unsupported property: " + property);
      }

      IEvent updated2 = builder2.buildEvent();
      model.editEvent(oldEvent2, (Event) updated2);
      return;
    }

    // If none of the patterns matched exactly, it’s malformed:
    throw new IllegalArgumentException("Malformed edit command: \"" + command + "\"");
  }
}
