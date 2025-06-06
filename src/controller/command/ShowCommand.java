package controller.command;

import model.ICalendar;

public class ShowCommand implements Command {

  private String command;

  public ShowCommand(String command) {
    this.command = command;
  }
  @Override
  public void execute(ICalendar model) {

  }
}
