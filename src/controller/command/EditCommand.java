package controller.command;

import model.Event;
import model.EventLocation;
import model.EventSeries;
import model.EventStatus;
import model.ICalendar;
import model.IEvent;
import model.IEventSeries;
import model.SeriesEditor;
import view.ITextView;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Handles “edit event …”, “edit events …” and “edit series …” commands.
 * Single‐event edits, tail‐of‐series edits, and whole‐series edits are
 * supported via SeriesEditor and the Calendar’s replaceSeries API.
 */
public class EditCommand implements Command {

  private static final Set<String> VALID_PROPERTIES = new HashSet<>(
          Arrays.asList("subject", "start", "end", "description", "location", "status")
  );

  private final String command;

  /**
   * Constructs command.
   * @param command string
   */
  public EditCommand(String command) {
    this.command = command.trim();
  }

  @Override
  public void execute(ICalendar model, ITextView view) {
    String property = null;
    String subject = null;
    LocalDateTime fromDT = null;
    LocalDateTime toDT = null;
    String newValue = null;

    // 1) Determine which form we're in, and extract parts
    if (command.startsWith("edit event ")
            && command.contains(" from ")
            && command.contains(" to ")
            && command.contains(" with ")) {

      // single‐instance edit
      property = Command.getWordAfter("edit event", command);
      subject = Command.getWordAfter(property, command);
      fromDT = LocalDateTime.parse(Command.getWordAfter("from", command));
      toDT = LocalDateTime.parse(Command.getWordAfter("to", command));
      newValue = Command.getWordAfter("with", command);

    } else if (command.startsWith("edit events ")
            && command.contains(" from ")
            && command.contains(" with ")
            && !command.contains(" to ")) {

      // tail‐of‐series edit
      property = Command.getWordAfter("edit events", command);
      subject = Command.getWordAfter(property, command);
      fromDT = LocalDateTime.parse(Command.getWordAfter("from", command));
      newValue = Command.getWordAfter("with", command);

    } else if (command.startsWith("edit series ")
            && command.contains(" from ")
            && command.contains(" with ")
            && !command.contains(" to ")) {

      // whole‐series edit
      property = Command.getWordAfter("edit series", command);
      subject = Command.getWordAfter(property, command);
      fromDT = LocalDateTime.parse(Command.getWordAfter("from", command));
      newValue = Command.getWordAfter("with", command);

    } else {
      throw new IllegalArgumentException("Malformed edit command: \"" + command + "\"");
    }

    // Validate property
    if (!VALID_PROPERTIES.contains(property)) {
      throw new IllegalArgumentException("Invalid property: \"" + property + "\"");
    }

    // Break out date/time pieces
    LocalDate fromDate = fromDT.toLocalDate();
    LocalTime fromTime = fromDT.toLocalTime();
    LocalDate toDate = (toDT != null) ? toDT.toLocalDate() : null;
    LocalTime toTime = (toDT != null) ? toDT.toLocalTime() : null;

    // Locate oldEvent in model
    List<IEvent> candidates = model.getScheduleInRange(fromDate, fromDate);
    IEvent oldEvent = null;
    for (IEvent e : candidates) {
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

    // Build a fresh EventBuilder copying all fields from oldEvent
    Event.EventBuilder builder = Event.getBuilder()
            .subject(oldEvent.getSubject())
            .startDate(
                    oldEvent.getStartDate().getDayOfMonth(),
                    oldEvent.getStartDate().getMonthValue(),
                    oldEvent.getStartDate().getYear())
            .startTime(
                    oldEvent.getStartTime().getHour(),
                    oldEvent.getStartTime().getMinute())
            .endDate(
                    oldEvent.getEndDate().getDayOfMonth(),
                    oldEvent.getEndDate().getMonthValue(),
                    oldEvent.getEndDate().getYear())
            .endTime(
                    oldEvent.getEndTime().getHour(),
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

    // Overwrite exactly the requested property
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
        throw new IllegalArgumentException("Unsupported edit property: " + property);
    }

    // Build the updated event
    IEvent updated = builder.buildEvent();

    // Series‐aware replacement
    IEventSeries series = model.getSeriesFor(oldEvent);

    if (command.startsWith("edit event ")) {
      // single-instance only
      model.replaceEvent(oldEvent, updated);

    } else if (command.startsWith("edit events ")) {
      // tail‐of‐series (the event + all after it)
      if (series == null) {
        model.replaceEvent(oldEvent, updated);
      } else {
        EventSeries newSeries = new SeriesEditor((EventSeries) series)
                .replaceRange(oldEvent, updated)
                .getSeries();
        model.replaceSeries(series, newSeries);
      }

    } else {  // edit series …
      // entire series
      if (series == null) {
        model.replaceEvent(oldEvent, updated);
      } else {
        EventSeries newSeries = new SeriesEditor((EventSeries) series)
                .replaceAll(updated)
                .getSeries();
        model.replaceSeries(series, newSeries);
      }
    }
  }
}
