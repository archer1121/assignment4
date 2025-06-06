package controller.command;

import model.ICalendar;

public class EditCommand implements Command {

  private String command;

  public EditCommand(String command) {
    this.command = command;
  }
  @Override
  public void execute(ICalendar model) {

  }
}
