package controller.command;

import model.ICalendar;
import model.ICalendarManager;
import view.ITextView;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class CopyEventsOnDateCommand implements Command {
  private final String cmd;
  private final ICalendarManager mgr;
  private final String currentCalendar;

  public CopyEventsOnDateCommand(String cmd,
                                 ICalendarManager mgr,
                                 String currentCalendar) {
    this.cmd             = cmd.trim();
    this.mgr             = mgr;
    this.currentCalendar = currentCalendar;
  }

  @Override
  public void execute(ICalendar srcCal, ITextView view) {
    String dateStr = Command.getWordAfter("copy events on", cmd);
    String target  = Command.getWordAfter("--target", cmd);
    String toDate  = Command.getWordAfter("to", cmd);

    ICalendar dstCal = mgr.getCalendar(target);

    LocalDate dSource = LocalDate.parse(dateStr);
    LocalDate dTarget = LocalDate.parse(toDate);

    // this will shift each event by the difference in days
    dstCal.copyEventsAndShift(dSource, dSource, srcCal, dTarget);

    view.takeMessage(String.format("Copied all events on %s to %s starting %s",
            dateStr, target, toDate));
  }
}
