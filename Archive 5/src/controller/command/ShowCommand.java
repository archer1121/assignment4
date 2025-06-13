package controller.command;

import model.ICalendar;
import view.ITextView;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class ShowCommand implements Command {

  private final String command;

  public ShowCommand(String command) {
    this.command = command.trim();
  }

  @Override
  public void execute(ICalendar model, ITextView view) {
    final String prefix = "show status on ";
    if (!command.startsWith(prefix)) {
      throw new IllegalArgumentException("Malformed show command: \"" + command + "\"");
    }
    String dtStr = Command.getWordAfter("on", command);
    LocalDateTime dt = LocalDateTime.parse(dtStr);
    LocalDate d = dt.toLocalDate();
    List<?> evs = model.getScheduleInRange(d,d);
    view.takeMessage(evs.isEmpty() ? "Available" : "Busy");
  }
}
