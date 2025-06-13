package controller.command;

import model.ICalendarManager;
import view.ITextView;

/**
 * Lets user switch calendar.
 */
public class UseCalendarCommand implements ManagerCommand {
  private final String cmd;

  /**
   * Constructs command.
   * @param cmd string
   */
  public UseCalendarCommand(String cmd) {
    this.cmd = cmd.trim();
  }

  /**
   * Extracts the calendar name after “--name”.
   */
  public String getCalendarName() {
    return Command.getWordAfter("--name", cmd);
  }

  @Override
  public void execute(ICalendarManager mgr, ITextView view) {
    // Parse out the name
    String name = getCalendarName();
    // Ensure it exists (will throw if not)
    mgr.getCalendar(name);
    // Confirm to the user
    view.takeMessage("Using calendar: " + name);
  }
}
