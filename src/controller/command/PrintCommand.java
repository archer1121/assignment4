package controller.command;

import model.ICalendar;
import model.IEvent;
import view.ITextView;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class PrintCommand implements Command {

  private final String command;

  public PrintCommand(String command) {
    this.command = command.trim();
  }

  @Override
  public void execute(ICalendar model, ITextView view) {
    // “print events on <date>”
    if (command.startsWith("print events on ")
            && !command.contains(" to ")) {

      String dateStr = Command.getWordAfter("on", command);
      LocalDate d = LocalDate.parse(dateStr);
      List<IEvent> events = model.getScheduleInRange(d,d);
      for (IEvent e : events) {
        view.takeMessage(formatLine(e));
      }
      return;
    }

    // “print events from <dt1> to <dt2>”
    if (command.startsWith("print events from ")
            && command.contains(" to ")) {

      LocalDateTime dt1 = LocalDateTime.parse(Command.getWordAfter("from", command));
      LocalDateTime dt2 = LocalDateTime.parse(Command.getWordAfter("to",   command));
      List<IEvent> events = model.getScheduleInRange(dt1.toLocalDate(), dt2.toLocalDate());
      for (IEvent e : events) {
        view.takeMessage(formatLine(e));
      }
      return;
    }

    throw new IllegalArgumentException("Malformed print command: \"" + command + "\"");
  }

  private String formatLine(IEvent e) {
    StringBuilder sb = new StringBuilder("• ");
    sb.append(e.getSubject());
    if (e.isAllDayEvent()) {
      sb.append(" (All day)");
    } else {
      LocalTime st = e.getStartTime();
      LocalTime et = e.getEndTime();
      sb.append(String.format(" %02d:%02d–%02d:%02d",
              st.getHour(), st.getMinute(), et.getHour(), et.getMinute()));
    }
    if (e.getLocation() != null) {
      sb.append(" @ ").append(e.getLocation());
    }
    return sb.toString();
  }
}
