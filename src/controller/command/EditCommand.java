package controller.command;

import model.ICalendar;

public class EditCommand implements Command {

  private String command;

  public EditCommand(String command) {
    this.command = command;
  }
  @Override
  public void execute(ICalendar model) {
    String property;
    String eventSubjectString;
    String dateTimeStringStarting;
    String dateTimeStringEnding; // only for “edit event”
    String newValue;

    // 1) “edit event … from … to … with …”
    if (command.contains("edit event ")
            && command.contains(" from ")
            && command.contains(" to ")
            && command.contains(" with ")) {

      // Parse <property> after "edit event"
      property = Command.getWordAfter("edit event", command);
      eventSubjectString = Command.getWordAfter(property, command);
      dateTimeStringStarting = Command.getWordAfter("from", command);
      dateTimeStringEnding = Command.getWordAfter("to", command);
      newValue = Command.getWordAfter("with", command);

//      // Convert date‐time strings to LocalDateTime
//      LocalDateTime startDT = LocalDateTime.parse(dateTimeStringStarting);
//      LocalDateTime endDT   = LocalDateTime.parse(dateTimeStringEnding);
//
//      // Call model: only this single occurrence
//      model.editEventProperty(
//              eventSubjectString,
//              startDT,
//              property,
//              newValue,
//              /* onlyThisInstance= */ true
//      );
    }

    // 2) “edit events … from … with …”  (no “to”)
    else if (command.contains("edit events ")
            && command.contains(" from ")
            && command.contains(" with ")
            && !command.contains(" to ")) {


      property = Command.getWordAfter("edit events", command);
      eventSubjectString = Command.getWordAfter(property, command);
      dateTimeStringStarting = Command.getWordAfter("from", command);
      newValue = Command.getWordAfter("with", command);

//      LocalDateTime startDT = LocalDateTime.parse(dateTimeStringStarting);
//
//      // Call model: edit this + all future occurrences
//      model.editEventProperty(
//              eventSubjectString,
//              startDT,
//              property,
//              newValue,
//              /* onlyThisInstance= */ false
//      );
//      return;
    }

    // 3) “edit series … from … with …”  (no “to”)
    else if (command.contains("edit series ")
            && command.contains(" from ")
            && command.contains(" with ")
            && !command.contains(" to ")) {


      property = Command.getWordAfter("edit series", command);
      eventSubjectString = Command.getWordAfter(property, command);
      dateTimeStringStarting = Command.getWordAfter("from", command);
      newValue = Command.getWordAfter("with", command);

//      LocalDateTime startDT = LocalDateTime.parse(dateTimeStringStarting);
//
//      // Call model: edit the entire series
//      model.editSeriesProperty(
//              eventSubjectString,
//              startDT,
//              property,
//              newValue
//      );
//      return;
    }

    // If none of the above matched, it’s malformed
    throw new IllegalArgumentException("Malformed edit command: \"" + command + "\"");
  }

}
