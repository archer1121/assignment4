package controller.command;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import model.ICalendar;

public class EditCommand implements Command {

  private static final Set<String> VALID_PROPERTIES = new HashSet<>(
          Arrays.asList("subject", "start", "end", "description", "location", "status")
  );
  private String command;

  public EditCommand(String command) {
    this.command = command;
  }
  @Override
  public void execute(ICalendar model) {
    String property, eventSubjectString, dateTimeStringStarting, dateTimeStringEnding, newValue;



    // 1) edit event … from … to … with …
    if (command.contains("edit event ")
            && command.contains(" from ")
            && command.contains(" to ")
            && command.contains(" with ")) {

      // parse out property, subject, from, to, with…
      property = Command.getWordAfter("edit event", command);
      eventSubjectString = Command.getWordAfter(property, command);
      dateTimeStringStarting = Command.getWordAfter("from", command);
      dateTimeStringEnding = Command.getWordAfter("to", command);
      newValue = Command.getWordAfter("with", command);

      if (!VALID_PROPERTIES.contains(property)) {
        throw new IllegalArgumentException("Invalid property: \"" + property + "\"");
      }

      // now return, so we don’t hit the final throw
      return;
    }

    // 2) edit events … from … with …
    else if (command.contains("edit events ")
            && command.contains(" from ")
            && command.contains(" with ")
            && !command.contains(" to ")) {

      property = Command.getWordAfter("edit events", command);
      eventSubjectString = Command.getWordAfter(property, command);
      dateTimeStringStarting = Command.getWordAfter("from", command);
      newValue = Command.getWordAfter("with", command);

      if (!VALID_PROPERTIES.contains(property)) {
        throw new IllegalArgumentException("Invalid property: \"" + property + "\"");
      }

      return;
    }

    // 3) edit series … from … with …
    else if (command.contains("edit series ")
            && command.contains(" from ")
            && command.contains(" with ")
            && !command.contains(" to ")) {

      property = Command.getWordAfter("edit series", command);
      eventSubjectString = Command.getWordAfter(property, command);
      dateTimeStringStarting = Command.getWordAfter("from", command);
      newValue = Command.getWordAfter("with", command);

      if (!VALID_PROPERTIES.contains(property)) {
        throw new IllegalArgumentException("Invalid property: \"" + property + "\"");
      }

      return;
    }

    // none matched → throw
    throw new IllegalArgumentException("Malformed edit command: \"" + command + "\"");

  }

}
