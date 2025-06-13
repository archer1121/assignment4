package controller.command;

import java.time.DateTimeException;
import java.time.ZoneId;

import model.Calendar;
import model.ICalendar;
import model.ICalendarManager;
import view.ITextView;

public class CreateCalendarCommand implements ManagerCommand {
  private final String cmd;
  public CreateCalendarCommand(String cmd) { this.cmd = cmd; }


  public void execute(ICalendarManager mgr, ITextView view) {
    // parse: --name NAME --timezone ZONE
    String name = Command.getWordAfter("--name", cmd);
    String tz   = Command.getWordAfter("--timezone", cmd);
    // validate ZoneId.of(tz)
    ZoneId zid;
    try { zid = ZoneId.of(tz); } catch(DateTimeException e) {
      throw new IllegalArgumentException("Invalid timezone: " + tz);
    }
    mgr.addCalendar(name, new Calendar(zid));
    view.takeMessage("Created calendar \""+name+"\" in timezone "+tz);
  }
}
