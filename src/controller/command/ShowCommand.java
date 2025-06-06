package controller.command;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import model.ICalendar;

public class ShowCommand implements Command {

  private String command;

  public ShowCommand(String command) {
    this.command = command;
  }
  @Override
  public void execute(ICalendar model) {
    if (command.contains("show status on ")) {
      String dateTimeString = Command.getWordAfter("on", command);
      // …(parsing, model.getScheduleInRange, printing “Busy”/“Available”)…

      LocalDateTime dt = LocalDateTime.parse(dateTimeString);
      // 4) Convert to LocalDate
      LocalDate d = dt.toLocalDate();

      List<?> events = model.getScheduleInRange(d, d);
      // 6) Print result
      if (events != null && !events.isEmpty()) {
        System.out.println("Busy");
      } else {
        System.out.println("Available");
      }
      return;
    }

    throw new IllegalArgumentException("Malformed show command: \"" + command + "\"");

  }
}
