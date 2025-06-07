package controller.command;

import model.Calendar;
import model.Event;
import model.ICalendar;
import model.EventStatus;
import model.EventLocation;
import model.IEvent;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles “create event …” commands by parsing them and then building and
 * storing Event objects via the ICalendar model.
 */
public class CreateCommand implements Command {

  private final String command;

  public CreateCommand(String command) {
    this.command = command.trim();
  }

  @Override
  public void execute(ICalendar model) {
    // 1) Basic guard
    if (!command.startsWith("create event ")) {
      throw new IllegalArgumentException("Expected 'create event ...' but got: \"" + command + "\"");
    }

    // ─── Case 1: “create event <subject> from <dt1> to <dt2>” (no repeats) ───
    if (command.contains(" from ") &&
            command.contains(" to ") &&
            !command.contains(" repeats ") &&
            !command.contains(" for ") &&
            !command.contains(" until ")) {

      // a) Extract subject
      String subject = Command.getWordAfter("event", command);
      if (subject.isEmpty()) {
        throw new IllegalArgumentException("Missing event subject");
      }

      // b) Extract start‐dateTime and parse
      String startString = Command.getWordAfter("from", command);
      if (startString.isEmpty()) {
        throw new IllegalArgumentException("Missing start date‐time");
      }
      LocalDateTime startDT = LocalDateTime.parse(startString);
      LocalDate startDate = startDT.toLocalDate();
      LocalTime startTime = startDT.toLocalTime();

      // c) Extract end‐dateTime and parse
      String endString = Command.getWordAfter("to", command);
      if (endString.isEmpty()) {
        throw new IllegalArgumentException("Missing end date‐time");
      }
      LocalDateTime endDT = LocalDateTime.parse(endString);
      LocalDate endDate = endDT.toLocalDate();
      LocalTime endTime = endDT.toLocalTime();

      // d) Use the EventBuilder to create an Event
      IEvent e = Event.getBuilder()
              .subject(subject)
              .startDate(startDate.getDayOfMonth(),
                      startDate.getMonthValue(),
                      startDate.getYear())
              .startTime(startTime.getHour(), startTime.getMinute())
              .endDate(endDate.getDayOfMonth(),
                      endDate.getMonthValue(),
                      endDate.getYear())
              .endTime(endTime.getHour(), endTime.getMinute())
              // .location(...)      // if you want to add location
              // .status(EventStatus.CONFIRMED)  // example
              // .description("Optional description")
              .buildEvent();

      // e) Finally, add it to the model
      model.createEvent((Event) e);

      return;
    }

    // ─── Case 2: “create event <subject> from <dt1> to <dt2> repeats <weekdays> for <N> times” ───
    else if (command.contains(" from ")
            && command.contains(" to ")
            && command.contains(" repeats ")
            && command.contains(" for ")
            && command.contains(" times")) {

      String subject = Command.getWordAfter("event", command);
      String startString = Command.getWordAfter("from", command);
      String endString   = Command.getWordAfter("to", command);
      String weekdays    = Command.getWordAfter("repeats", command);
      String countStr    = Command.getWordAfter("for", command);

      // parse count
      int count;
      try {
        count = Integer.parseInt(countStr);
      } catch (NumberFormatException ex) {
        throw new IllegalArgumentException("Invalid repeat‐count: " + countStr);
      }

      // parse datetimes
      LocalDateTime startDT = LocalDateTime.parse(startString);
      LocalDate startDate = startDT.toLocalDate();
      LocalTime startTime = startDT.toLocalTime();

      LocalDateTime endDT = LocalDateTime.parse(endString);
      LocalDate endDate = endDT.toLocalDate();
      LocalTime endTime = endDT.toLocalTime();

      // build N events according to the weekday string (e.g. "MWF")
      // Here’s a quick example for a weekly series:
      List<Event> series = new ArrayList<>();
      LocalDate currentDate = startDate;
      int created = 0;
      while (created < count) {
        char dayCode = currentDate.getDayOfWeek().toString().charAt(0);
        // Simplified: check if dayCode matches any char in `weekdays`
        if (weekdays.indexOf(dayCode) >= 0) {
          IEvent e = Event.getBuilder()
                  .subject(subject)
                  .startDate(currentDate.getDayOfMonth(),
                          currentDate.getMonthValue(),
                          currentDate.getYear())
                  .startTime(startTime.getHour(), startTime.getMinute())
                  .endDate(currentDate.getDayOfMonth(),
                          currentDate.getMonthValue(),
                          currentDate.getYear())
                  .endTime(endTime.getHour(), endTime.getMinute())
                  .buildEvent();
          series.add((Event) e);
          created++;
        }
        currentDate = currentDate.plusDays(1);
      }

      // store the entire list at once
      model.createEventSeries(series);
      return;
    }

    // ─── Case 3: “create event <subject> from <dt1> to <dt2> repeats <weekdays> until <date>” ───
    else if (command.contains(" from ")
            && command.contains(" to ")
            && command.contains(" repeats ")
            && command.contains(" until ")) {

      String subject    = Command.getWordAfter("event", command);
      String startString= Command.getWordAfter("from", command);
      String endString  = Command.getWordAfter("to", command);
      String weekdays   = Command.getWordAfter("repeats", command);
      String untilDateS = Command.getWordAfter("until", command);

      // parse the “until” LocalDate (no time)
      LocalDate untilDate = LocalDate.parse(untilDateS);

      // parse start and end LocalDateTime
      LocalDateTime startDT = LocalDateTime.parse(startString);
      LocalDate startDate = startDT.toLocalDate();
      LocalTime startTime = startDT.toLocalTime();

      LocalDateTime endDT = LocalDateTime.parse(endString);
      LocalDate endDate = endDT.toLocalDate();
      LocalTime endTime = endDT.toLocalTime();

      // generate series until `untilDate` (inclusive)
      List<Event> series = new ArrayList<>();
      LocalDate currentDate = startDate;
      while (!currentDate.isAfter(untilDate)) {
        char dayCode = currentDate.getDayOfWeek().toString().charAt(0);
        if (weekdays.indexOf(dayCode) >= 0) {
          IEvent e = Event.getBuilder()
                  .subject(subject)
                  .startDate(currentDate.getDayOfMonth(),
                          currentDate.getMonthValue(),
                          currentDate.getYear())
                  .startTime(startTime.getHour(), startTime.getMinute())
                  .endDate(currentDate.getDayOfMonth(),
                          currentDate.getMonthValue(),
                          currentDate.getYear())
                  .endTime(endTime.getHour(), endTime.getMinute())
                  .buildEvent();
          series.add((Event) e);
        }
        currentDate = currentDate.plusDays(1);
      }

      model.createEventSeries(series);
      return;
    }

    // ─── Case 4: “create event <subject> on <date>” ───
    else if (command.contains(" on ")
            && !command.contains(" repeats ")
            && !command.contains(" for ")
            && !command.contains(" until ")) {

      String subject   = Command.getWordAfter("event", command);
      String dateOnly  = Command.getWordAfter("on", command);
      LocalDate d      = LocalDate.parse(dateOnly);

      // Build an “all‐day” event: startTime=08:00, endTime=17:00
      IEvent e = Event.getBuilder()
              .subject(subject)
              .startDate(d.getDayOfMonth(), d.getMonthValue(), d.getYear())
              .startTime(8, 0)
              .endDate(d.getDayOfMonth(), d.getMonthValue(), d.getYear())
              .endTime(17, 0)
              .buildEvent();

      model.createEvent((Event) e);
      return;
    }

    // ─── Case 5: “create event <subject> on <date> repeats <weekdays> for <N> times” ───
    else if (command.contains(" on ")
            && command.contains(" repeats ")
            && command.contains(" for ")
            && command.contains(" times")) {

      String subject   = Command.getWordAfter("event", command);
      String dateOnly  = Command.getWordAfter("on", command);
      String weekdays  = Command.getWordAfter("repeats", command);
      String countStr  = Command.getWordAfter("for", command);

      int count;
      try {
        count = Integer.parseInt(countStr);
      } catch (NumberFormatException ex) {
        throw new IllegalArgumentException("Invalid repeat count: " + countStr);
      }

      LocalDate startDate = LocalDate.parse(dateOnly);
      LocalTime startTime = LocalTime.of(8, 0);
      LocalTime endTime   = LocalTime.of(17, 0);

      List<Event> series = new ArrayList<>();
      LocalDate currentDate = startDate;
      int created = 0;
      while (created < count) {
        char dayCode = currentDate.getDayOfWeek().toString().charAt(0);
        if (weekdays.indexOf(dayCode) >= 0) {
          IEvent e = Event.getBuilder()
                  .subject(subject)
                  .startDate(currentDate.getDayOfMonth(),
                          currentDate.getMonthValue(),
                          currentDate.getYear())
                  .startTime(startTime.getHour(), startTime.getMinute())
                  .endDate(currentDate.getDayOfMonth(),
                          currentDate.getMonthValue(),
                          currentDate.getYear())
                  .endTime(endTime.getHour(), endTime.getMinute())
                  .buildEvent();
          series.add((Event) e);
          created++;
        }
        currentDate = currentDate.plusDays(1);
      }

      model.createEventSeries(series);
      return;
    }

    // ─── Case 6: “create event <subject> on <date> repeats <weekdays> until <date>” ───
    else if (command.contains(" on ")
            && command.contains(" repeats ")
            && command.contains(" until ")) {

      String subject   = Command.getWordAfter("event", command);
      String dateOnly  = Command.getWordAfter("on", command);
      String weekdays  = Command.getWordAfter("repeats", command);
      String untilDateS = Command.getWordAfter("until", command);

      LocalDate startDate = LocalDate.parse(dateOnly);
      LocalDate untilDate = LocalDate.parse(untilDateS);
      LocalTime startTime = LocalTime.of(8, 0);
      LocalTime endTime   = LocalTime.of(17, 0);

      List<Event> series = new ArrayList<>();
      LocalDate currentDate = startDate;
      while (!currentDate.isAfter(untilDate)) {
        char dayCode = currentDate.getDayOfWeek().toString().charAt(0);
        if (weekdays.indexOf(dayCode) >= 0) {
          IEvent e = Event.getBuilder()
                  .subject(subject)
                  .startDate(currentDate.getDayOfMonth(),
                          currentDate.getMonthValue(),
                          currentDate.getYear())
                  .startTime(startTime.getHour(), startTime.getMinute())
                  .endDate(currentDate.getDayOfMonth(),
                          currentDate.getMonthValue(),
                          currentDate.getYear())
                  .endTime(endTime.getHour(), endTime.getMinute())
                  .buildEvent();
          series.add((Event) e);
        }
        currentDate = currentDate.plusDays(1);
      }

      model.createEventSeries(series);
      return;
    }

    // If none of the patterns matched, it’s malformed:
    throw new IllegalArgumentException("Malformed create command: \"" + command + "\"");
  }
}
