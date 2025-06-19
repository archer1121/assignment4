package controller.command;

import model.Event;
import model.ICalendar;
import model.IEvent;
import view.ITextView;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Creates an event within calendar.
 */
public class CreateCommand implements Command {
  private final String cmd;

  /**
   * Creates an event.
   *
   * @param command string
   */
  public CreateCommand(String command) {
    this.cmd = command.trim();
  }

  @Override
  public void execute(ICalendar model, ITextView view) {
    if (!cmd.startsWith("create event ")) {
      throw new IllegalArgumentException("Expected 'create event ...' but got: \"" + cmd + "\"");
    }

    // ─── Case 1: single timed event ───────────────────────────────────
    if (cmd.contains(" from ")
            && cmd.contains(" to ")
            && !cmd.contains(" on ")
            && !cmd.contains(" repeats ")) {

      String subject = Command.getWordAfter("event", cmd);
      String fromS = Command.getWordAfter("from", cmd);
      String toS = Command.getWordAfter("to", cmd);

      LocalDateTime fromDT = LocalDateTime.parse(fromS);
      LocalDateTime toDT = LocalDateTime.parse(toS);

      IEvent e = Event.getBuilder()
              .subject(subject)
              .startDate(fromDT.getDayOfMonth(), fromDT.getMonthValue(), fromDT.getYear())
              .startTime(fromDT.getHour(), fromDT.getMinute())
              .endDate(toDT.getDayOfMonth(), toDT.getMonthValue(), toDT.getYear())
              .endTime(toDT.getHour(), toDT.getMinute())
              .buildEvent();

      model.addEvent(e);
      return;
    }

    // ─── Case 2: timed series for N times ─────────────────────────────
    if (cmd.contains(" from ")
            && cmd.contains(" to ")
            && cmd.contains(" repeats ")
            && cmd.contains(" for ")
            && cmd.contains(" times")) {

      String subject = Command.getWordAfter("event", cmd);
      String fromS = Command.getWordAfter("from", cmd);
      String toS = Command.getWordAfter("to", cmd);
      String days = Command.getWordAfter("repeats", cmd);
      String countS = Command.getWordAfter("for", cmd);

      int count;
      try {
        count = Integer.parseInt(countS);
      } catch (NumberFormatException ex) {
        throw new IllegalArgumentException("Invalid repeat count: " + countS);
      }

      LocalDateTime fromDT = LocalDateTime.parse(fromS);
      LocalTime startT = fromDT.toLocalTime();
      LocalTime endT = LocalDateTime.parse(toS).toLocalTime();
      LocalDate cursor = fromDT.toLocalDate();

      int created = 0;
      while (created < count) {
        DayOfWeek dow = cursor.getDayOfWeek();
        if (days.indexOf(firstCharOf(dow)) >= 0) {
          IEvent e = Event.getBuilder()
                  .subject(subject)
                  .startDate(cursor.getDayOfMonth(), cursor.getMonthValue(), cursor.getYear())
                  .startTime(startT.getHour(), startT.getMinute())
                  .endDate(cursor.getDayOfMonth(), cursor.getMonthValue(), cursor.getYear())
                  .endTime(endT.getHour(), endT.getMinute())
                  .buildEvent();
          model.addEvent(e);
          created++;
        }
        cursor = cursor.plusDays(1);
      }
      return;
    }

    // ─── Case 3: timed series until <date> ────────────────────────────
    if (cmd.contains(" from ")
            && cmd.contains(" to ")
            && cmd.contains(" repeats ")
            && cmd.contains(" until ")) {

      String subject = Command.getWordAfter("event", cmd);
      String fromS = Command.getWordAfter("from", cmd);
      String toS = Command.getWordAfter("to", cmd);
      String days = Command.getWordAfter("repeats", cmd);
      String untilS = Command.getWordAfter("until", cmd);

      LocalDateTime fromDT = LocalDateTime.parse(fromS);
      LocalTime startT = fromDT.toLocalTime();
      LocalTime endT = LocalDateTime.parse(toS).toLocalTime();
      LocalDate cursor = fromDT.toLocalDate();
      LocalDate endDate = LocalDate.parse(untilS);

      while (!cursor.isAfter(endDate)) {
        DayOfWeek dow = cursor.getDayOfWeek();
        if (days.indexOf(firstCharOf(dow)) >= 0) {
          IEvent e = Event.getBuilder()
                  .subject(subject)
                  .startDate(cursor.getDayOfMonth(), cursor.getMonthValue(), cursor.getYear())
                  .startTime(startT.getHour(), startT.getMinute())
                  .endDate(cursor.getDayOfMonth(), cursor.getMonthValue(), cursor.getYear())
                  .endTime(endT.getHour(), endT.getMinute())
                  .buildEvent();
          model.addEvent(e);
        }
        cursor = cursor.plusDays(1);
      }
      return;
    }

    // ─── Case 4: single all‐day event ────────────────────────────────
    if (cmd.contains(" on ")
            && !cmd.contains(" repeats ")) {

      String subject = Command.getWordAfter("event", cmd);
      String onS = Command.getWordAfter("on", cmd);
      LocalDate d = LocalDate.parse(onS);

      IEvent e = Event.getBuilder()
              .subject(subject)
              .startDate(d.getDayOfMonth(), d.getMonthValue(), d.getYear())
              .startTime(8, 0)
              .endDate(d.getDayOfMonth(), d.getMonthValue(), d.getYear())
              .endTime(17, 0)
              .buildEvent();

      model.addEvent(e);
      return;
    }

    // ─── Case 5: all‐day series for N times ───────────────────────────
    if (cmd.contains(" on ")
            && cmd.contains(" repeats ")
            && cmd.contains(" for ")
            && cmd.contains(" times")) {

      String subject = Command.getWordAfter("event", cmd);
      String onS = Command.getWordAfter("on", cmd);
      String days = Command.getWordAfter("repeats", cmd);
      String countS = Command.getWordAfter("for", cmd);

      int count;
      try {
        count = Integer.parseInt(countS);
      } catch (NumberFormatException ex) {
        throw new IllegalArgumentException("Invalid repeat count: " + countS);
      }

      LocalDate cursor = LocalDate.parse(onS);
      int created = 0;
      while (created < count) {
        DayOfWeek dow = cursor.getDayOfWeek();
        if (days.indexOf(firstCharOf(dow)) >= 0) {
          IEvent e = Event.getBuilder()
                  .subject(subject)
                  .startDate(cursor.getDayOfMonth(), cursor.getMonthValue(), cursor.getYear())
                  .startTime(8, 0)
                  .endDate(cursor.getDayOfMonth(), cursor.getMonthValue(), cursor.getYear())
                  .endTime(17, 0)
                  .buildEvent();
          model.addEvent(e);
          created++;
        }
        cursor = cursor.plusDays(1);
      }
      return;
    }

    // ─── Case 6: all‐day series until <date> ──────────────────────────
    if (cmd.contains(" on ")
            && cmd.contains(" repeats ")
            && cmd.contains(" until ")) {

      String subject = Command.getWordAfter("event", cmd);
      String onS = Command.getWordAfter("on", cmd);
      String days = Command.getWordAfter("repeats", cmd);
      String untilS = Command.getWordAfter("until", cmd);

      LocalDate cursor = LocalDate.parse(onS);
      LocalDate end = LocalDate.parse(untilS);
      while (!cursor.isAfter(end)) {
        DayOfWeek dow = cursor.getDayOfWeek();
        if (days.indexOf(firstCharOf(dow)) >= 0) {
          IEvent e = Event.getBuilder()
                  .subject(subject)
                  .startDate(cursor.getDayOfMonth(), cursor.getMonthValue(), cursor.getYear())
                  .startTime(8, 0)
                  .endDate(cursor.getDayOfMonth(), cursor.getMonthValue(), cursor.getYear())
                  .endTime(17, 0)
                  .buildEvent();
          model.addEvent(e);
        }
        cursor = cursor.plusDays(1);
      }
      return;
    }

    throw new IllegalArgumentException("Malformed create command: \"" + cmd + "\"");
  }

  private static char firstCharOf(DayOfWeek d) {
    switch (d) {
      case MONDAY:
        return 'M';
      case TUESDAY:
        return 'T';
      case WEDNESDAY:
        return 'W';
      case THURSDAY:
        return 'R';
      case FRIDAY:
        return 'F';
      case SATURDAY:
        return 'S';
      case SUNDAY:
        return 'U';
      default:
        throw new IllegalArgumentException();
    }
  }
}
