package controller.command;

import model.ICalendar;

/**
 * create event <eventSubject> from <dateStringTtimeString> to <dateStringTtimeString>
 * ...
 */
public class CreateCommand implements Command {

  private String command;

  public CreateCommand(String command) {
    this.command = command;
  }

  @Override
  public void execute(ICalendar model) {
    String dateTimeStringStarting;
    String dateTimeStringEnding;
    String eventSubjectString;
    String daysOfWeekString;
    String numberRepeatsString;
    String endingDateString;
    String dateStringOnly;


    if (!command.startsWith("create event ")) {
      throw new IllegalArgumentException("Expected 'create event ...' but got: \"" + command + "\"");
    }

    // case 1: “create event <subject> from <dt1> to <dt2>”
    if (command.contains("from")
            && command.contains("to")
            && !command.contains("repeats")
            && !command.contains("for")) {

      eventSubjectString = Command.getWordAfter("event", command);
      dateTimeStringStarting = Command.getWordAfter("from", command);
      dateTimeStringEnding = Command.getWordAfter("to", command);

      // ──> once we know it’s a valid “case 1,” exit immediately
      return;
    }

    // case 2: “create event <subject> from <dt1> to <dt2> repeats <wd> for <N> times”
    else if (command.contains("from")
            && command.contains("to")
            && command.contains("repeats")
            && command.contains("for")
            && command.contains("times")) {

      eventSubjectString = Command.getWordAfter("event", command);
      dateTimeStringStarting = Command.getWordAfter("from", command);
      dateTimeStringEnding = Command.getWordAfter("to", command);
      daysOfWeekString = Command.getWordAfter("repeats", command);
      numberRepeatsString = Command.getWordAfter("for", command);

      // ──> valid “case 2,” so exit now
      return;
    }

    // case 3: “create event <subject> from <dt1> to <dt2> repeats <wd> until <date>”
    else if (command.contains("from")
            && command.contains("to")
            && command.contains("repeats")
            && command.contains("until")) {

      eventSubjectString = Command.getWordAfter("event", command);
      dateTimeStringStarting = Command.getWordAfter("from", command);
      dateTimeStringEnding = Command.getWordAfter("to", command);
      daysOfWeekString = Command.getWordAfter("repeats", command);
      endingDateString = Command.getWordAfter("until", command);

      // ──> valid “case 3,” so exit now
      return;
    }

    // case 4: “create event <subject> on <date>”
    else if (command.contains(" on ")
            && !command.contains(" repeats ")
            && !command.contains(" for ")
            && !command.contains(" until ")) {

      eventSubjectString = Command.getWordAfter("event", command);
      dateStringOnly = Command.getWordAfter("on", command);

      // ──> valid “case 4,” so exit now
      return;
    }

    // case 5: “create event <subject> on <date> repeats <wd> for <N> times”
    else if (command.contains(" on ")
            && command.contains(" repeats ")
            && command.contains(" for ")
            && command.contains(" times")) {

      eventSubjectString = Command.getWordAfter("event", command);
      dateStringOnly = Command.getWordAfter("on", command);
      daysOfWeekString = Command.getWordAfter("repeats", command);
      numberRepeatsString = Command.getWordAfter("for", command);

      // ──> valid “case 5,” so exit now
      return;
    }

    // case 6: “create event <subject> on <date> repeats <wd> until <date>”
    else if (command.contains(" on ")
            && command.contains(" repeats ")
            && command.contains(" until ")) {

      eventSubjectString = Command.getWordAfter("event", command);
      dateStringOnly = Command.getWordAfter("on", command);
      daysOfWeekString = Command.getWordAfter("repeats", command);
      endingDateString = Command.getWordAfter("until", command);

      // ──> valid “case 6,” so exit now
      return;
    }

    // If we reached here, none of the above patterns matched
    throw new IllegalArgumentException("Malformed create command: \"" + command + "\"");
  }
}
