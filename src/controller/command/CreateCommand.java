package controller.command;

import model.ICalendar;

public class CreateCommand implements Command {

  private String command;

  public CreateCommand(String command) {
    this.command = command;
  }

  @Override
  public void execute(ICalendar model) {


    if (!command.startsWith("create event ")) {
      throw new IllegalArgumentException("Expected 'create event ...' but got: \"" + command + "\"");
    }
    //remove the "create event" part
    String currentStr = command;
    currentStr = Command.removeFirstWord(currentStr);
    currentStr = Command.removeFirstWord(currentStr);

    //now the subject is first word NOTE DO NOT HAVE TO PARSE MULTIPLE WORD SUBJECTS


  }
}
