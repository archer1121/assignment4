package controller.command;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import model.Event;
import model.ICalendar;

public class PrintCommand implements Command{

  private String command;

  public PrintCommand(String command) {
    this.command = command;
  }
  @Override
  public void execute(ICalendar model) {

      // case 1: print events on <date>
      if (command.startsWith("print events on ") && !command.contains(" to ")) {
        String dateStr = Command.getWordAfter("on", command);
        if (dateStr.isEmpty()) {
          throw new IllegalArgumentException("Malformed print command: \"" + command + "\"");
        }
        LocalDate date = LocalDate.parse(dateStr);
        List<Event> events = model.getScheduleInRange(date, date);
        for (Event ev : events) {
          printEventLine(ev);
        }
        return;
      }

      // case 2: print events from <dateTime> to <dateTime>
      if (command.startsWith("print events from ") && command.contains(" to ")) {
        String fromStr = Command.getWordAfter("from", command);
        String toStr = Command.getWordAfter("to", command);
        if (fromStr.isEmpty() || toStr.isEmpty()) {
          throw new IllegalArgumentException("Malformed print command: \"" + command + "\"");
        }
        LocalDateTime fromDT = LocalDateTime.parse(fromStr);
        LocalDateTime toDT = LocalDateTime.parse(toStr);
        LocalDate startDate = fromDT.toLocalDate();
        LocalDate endDate = toDT.toLocalDate();
        List<Event> events = model.getScheduleInRange(startDate, endDate);
        for (Event ev : events) {
          printEventLine(ev);
        }
        return;
      }

      // otherwise malformed
      throw new IllegalArgumentException("Malformed print command: \"" + command + "\"");
    }

  private void printEventLine(Event ev) {
    StringBuilder line = new StringBuilder("• ");
    line.append(ev.getSubject());
    if (ev.isAllDayEvent()) {
      line.append(" (All day)");
    } else {
      LocalTime st = ev.getStartTime();
      LocalTime et = ev.getEndTime();
      line.append(' ')
              .append(String.format("%02d:%02d", st.getHour(), st.getMinute()))
              .append('–')
              .append(String.format("%02d:%02d", et.getHour(), et.getMinute()));
    }
    if (ev.getLocation() != null) {
      line.append(" @ ").append(ev.getLocation());
    }
    System.out.println(line.toString());
  }

}
