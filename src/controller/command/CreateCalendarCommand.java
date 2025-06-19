package controller.command;

import java.time.DateTimeException;
import java.time.ZoneId;

import model.Calendar;

import model.ICalendarManager;
import view.ITextView;

/**
 * Create a new calendar.
 */
public class CreateCalendarCommand implements ManagerCommand {
  private final String cmd;

  /**
   * Constrcuts the command.
   *
   * @param cmd string
   */
  public CreateCalendarCommand(String cmd) {
    this.cmd = cmd;
  }

  /**
   * Executes the command.
   *
   * @param mgr  manager
   * @param view view to be seen
   */
  public void execute(ICalendarManager mgr, ITextView view) {
    // parse: --name NAME --timezone ZONE
    String name = Command.getWordAfter("--name", cmd);
    String tz = Command.getWordAfter("--timezone", cmd);
    // validate ZoneId.of(tz)
    ZoneId zid;
    try {
      zid = ZoneId.of(tz);
    } catch (DateTimeException e) {
      throw new IllegalArgumentException("Invalid timezone: " + tz);
    }
    mgr.addCalendar(name, new Calendar(zid));
    view.takeMessage("Created calendar \"" + name + "\" in timezone " + tz);
  }
}
