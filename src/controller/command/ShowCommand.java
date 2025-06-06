package controller.command;

import model.ICalendar;

public class ShowCommand implements Command {

  private String command;

  public ShowCommand(String command) {
    this.command = command;
  }
  @Override
  public void execute(ICalendar model) {
// Expect exactly: "show status on <dateTime>"
    if (command.contains("show status on ")) {
      String dateTimeString = Command.getWordAfter("on", command);
//      LocalDateTime dateTime = LocalDateTime.parse(dateTimeString);
//
//      boolean busy = model.isBusyAt(dateTime);
//      System.out.println(busy ? "Busy" : "Available");
//      return;
    }
  }
}
