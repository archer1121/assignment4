package controller.command;

import model.ICalendar;
import model.ICalendarManager;
import view.ITextView;

import java.time.DateTimeException;
import java.time.ZoneId;

/**
 * Command to edit an existing calendar's name or timezone.
 */
public class EditCalendarCommand implements ManagerCommand {
  private final String cmd;

  /**
   * Constructs the command used to edit calendars.
   *
   * @param cmd string
   */
  public EditCalendarCommand(String cmd) {
    this.cmd = cmd.trim();
  }

  @Override
  public void execute(ICalendarManager mgr, ITextView view) {
    // 1) extract the calendar we're talking about
    String oldName = Command.getWordAfter("--name", cmd);

    // 2) extract which property
    String property = Command.getWordAfter("--property", cmd);

    // 3) now grab the new value _after_ the "--property <property>" flag,
    //    so we don't accidentally pick up the wrong token if property="name"
    String flag = "--property " + property;
    String newValue = Command.getWordAfter(flag, cmd);

    switch (property) {
      case "name":
        // rename
        mgr.changeName(oldName, newValue);
        view.takeMessage(
                String.format("Renamed calendar \"%s\" to \"%s\"", oldName, newValue)
        );
        break;

      case "timezone":
        // validate the IANA tz id
        ZoneId zid;
        try {
          zid = ZoneId.of(newValue);
        } catch (DateTimeException ex) {
          throw new IllegalArgumentException("Invalid timezone: " + newValue);
        }
        // shift the calendar itself
        ICalendar oldCal = mgr.getCalendar(oldName);
        ICalendar shifted = oldCal.setTimeZone(zid);
        // replace in the manager under the same key
        mgr.removeCalendar(oldName);
        mgr.addCalendar(oldName, shifted);
        view.takeMessage(
                String.format("Changed timezone of calendar \"%s\" to %s", oldName, newValue)
        );
        break;

      default:
        throw new IllegalArgumentException("Invalid property: " + property);
    }
  }
}
