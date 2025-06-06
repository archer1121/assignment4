package controller.command;

import model.ICalendar;

public class PrintCommand implements Command{

  private String command;

  public PrintCommand(String command) {
    this.command = command;
  }
  @Override
  public void execute(ICalendar model) {

  }
}
