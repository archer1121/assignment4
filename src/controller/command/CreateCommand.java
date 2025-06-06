package controller.command;

import model.ICalendar;

/**
 * create event <eventSubject> from <dateStringTtimeString> to <dateStringTtimeString>
 * Creates a single event in the calendar. Note \<dateString> is a string of the form "YYYY-MM-DD" \<timeString> is a string of the form "hh:mm" and \<dateStringTtimeString> is a string of the form "YYYY-MM-DDThh::mm".
 * <p>
 * create event <eventSubject> from <dateStringTtimeString> to <dateStringTtimeString> repeats <weekdays> for <N> times
 * Creates an event series that repeats N times on specific weekdays. Note \<weekdays> is a sequence of characters where character denotes a day of the week, e.g., MRU. 'M' is Monday, 'T' is Tuesday, 'W' is Wednesday, 'R' is Thursday, 'F' is Friday, 'S' is Saturday, and 'U' is Sunday.
 * <p>
 * create event <eventSubject> from <dateStringTtimeString> to <dateStringTtimeString> repeats <weekdays> until <dateString>
 * Creates an event series until a specific date (inclusive).
 * <p>
 * create event <eventSubject> on <dateString>
 * Creates a single all day event.
 * <p>
 * create event <eventSubject> on <dateString> repeats <weekdays> for <N> times
 * Creates a series of all day events that repeats N times on specific weekdays.
 * <p>
 * create event <eventSubject> on <dateString> repeats <weekdays> until <dateString>
 * Creates a series of all day events until a specific date (inclusive).
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
    //case 1:
    //create event <eventSubject> from <dateStringTtimeString> to <dateStringTtimeString>

    //Creates a single event in the calendar. Note \<dateString> is a string of the form
    // "YYYY-MM-DD" \<timeString> is a string of the form "hh:mm" and \<dateStringTtimeString>
    // is a string of the form "YYYY-MM-DDThh::mm".

    if (command.contains("from")
            && command.contains("to")
    && !command.contains("repeats")
    && !command.contains("for")) {

      eventSubjectString = Command.getWordAfter("event", command);
      dateTimeStringStarting = Command.getWordAfter("from", command);
      dateTimeStringEnding = Command.getWordAfter("to", command);
    }

    //case 2
    //create event <eventSubject> from <dateStringTtimeString> to <dateStringTtimeString>
    // repeats <weekdays> for <N> times

    // Creates an event series that repeats N times on specific weekdays.
    // Note \<weekdays> is a sequence of characters where character denotes a day of
    // the week, e.g., MRU. 'M' is Monday, 'T' is Tuesday, 'W' is Wednesday, 'R' is Thursday,
    // 'F' is Friday, 'S' is Saturday, and 'U' is Sunday.

    else if(command.contains("from")
            && command.contains("to")
            && command.contains("repeats")
            && command.contains("for")
            && command.contains("times")
    ){
      eventSubjectString = Command.getWordAfter("event", command);
      dateTimeStringStarting = Command.getWordAfter("from", command);
      dateTimeStringEnding = Command.getWordAfter("to", command);
      daysOfWeekString = Command.getWordAfter("repeats", command);
      numberRepeatsString = Command.getWordAfter("for", command);


    }

    //case 3

    //create event <eventSubject> from <dateStringTtimeString> to
    // <dateStringTtimeString> repeats <weekdays> until <dateString>

    //Creates an event series until a specific date (inclusive)
    else if(command.contains("from")
            && command.contains("to")
            && command.contains("repeats")
            && command.contains("until")
    ){
      eventSubjectString = Command.getWordAfter("event", command);
      dateTimeStringStarting = Command.getWordAfter("from", command);
      dateTimeStringEnding = Command.getWordAfter("to", command);
      daysOfWeekString = Command.getWordAfter("repeats", command);
      endingDateString = Command.getWordAfter("until", command);


    }

    //case 4

    //create event <eventSubject> on <dateString>

    //Creates a single all day event.
    else if (command.contains(" on ") &&
            !command.contains(" repeats ") &&
            !command.contains(" for ") &&
            !command.contains(" until ")) {

      eventSubjectString = Command.getWordAfter("event", command);
      dateStringOnly = Command.getWordAfter("on", command);

    }


    // Case 5: all-day series, repeats <weekdays> for <N> times

    //   create event <eventSubject> on <date> repeats <weekdays> for <N> times
    else if (command.contains(" on ") &&
            command.contains(" repeats ") &&
            command.contains(" for ") &&
            command.contains(" times")) {

      eventSubjectString = Command.getWordAfter("event", command);
      dateStringOnly = Command.getWordAfter("on", command);
      daysOfWeekString = Command.getWordAfter("repeats", command);
      numberRepeatsString = Command.getWordAfter("for", command);
    }

    // Case 6: all-day series until <date>

    //   create event <eventSubject> on <date> repeats <weekdays> until <date>

    else if (command.contains(" on ") &&
            command.contains(" repeats ") &&
            command.contains(" until ")) {

      eventSubjectString = Command.getWordAfter("event", command);
      dateStringOnly = Command.getWordAfter("on", command);
      daysOfWeekString = Command.getWordAfter("repeats", command);
      endingDateString = Command.getWordAfter("until", command);
    }
    throw new IllegalArgumentException("Malformed create command: \"" + command + "\"");
  }
}
