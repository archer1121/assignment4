package controller.command;

import model.ICalendar;

public class PrintCommand implements Command{

  private String command;

  public PrintCommand(String command) {
    this.command = command;
  }
  @Override
  public void execute(ICalendar model) {
    String dateStringOnly, dateTimeStringStarting, dateTimeStringEnding;

    // case 1: print events on <date>
    if (command.contains("print events on ")
            && !command.contains(" to ")) {

      dateStringOnly = Command.getWordAfter("on", command);
      // …(parsing code, printing, etc.)…
      return;
    }

    // case 2: print events from <dt1> to <dt2>
    else if (command.contains("print events from ")
            && command.contains(" to ")) {

      dateTimeStringStarting = Command.getWordAfter("from", command);
      dateTimeStringEnding = Command.getWordAfter("to", command);
      // …(parsing code, printing, etc.)…
      return;
    }

    throw new IllegalArgumentException("Malformed print command: \"" + command + "\"");
  }
}
