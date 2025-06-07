package controller.command;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import model.Event;
import model.ICalendar;

/**
 * "show status on <dateTime>"
 *
 * Prints "Busy" if there is any event on that date, otherwise "Available".
 */
public class ShowCommand implements Command {

  private final String command;

  public ShowCommand(String command) {
    this.command = command.trim();
  }

  @Override
  public void execute(ICalendar model) {
    final String prefix = "show status on ";
    if (!command.startsWith(prefix)) {
      throw new IllegalArgumentException("Malformed show command: \"" + command + "\"");
    }
    String dtStr = Command.getWordAfter("on", command);
    if (dtStr.isEmpty()) {
      throw new IllegalArgumentException("Malformed show command: \"" + command + "\"");
    }
    LocalDateTime dt = LocalDateTime.parse(dtStr);
    LocalDate date = dt.toLocalDate();
    List<Event> events = model.getScheduleInRange(date, date);
    if (events != null && !events.isEmpty()) {
      System.out.println("Busy");
    } else {
      System.out.println("Available");
    }
  }
}
