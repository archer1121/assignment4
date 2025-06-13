package controller.command;

import model.ICalendar;
import model.ICalendarManager;
import view.ITextView;

import java.time.LocalDate;

/**
 * Copy events between dates.
 */
public class CopyEventsBetweenCommand implements Command {
  private final String cmd;
  private final ICalendarManager mgr;
  private final String currentCalendar;

  /**
   * Constructor.
   *
   * @param cmd             string command
   * @param mgr             manager
   * @param currentCalendar to be operated on
   */
  public CopyEventsBetweenCommand(String cmd,
                                  ICalendarManager mgr,
                                  String currentCalendar) {
    this.cmd = cmd.trim();
    this.mgr = mgr;
    this.currentCalendar = currentCalendar;
  }

  @Override
  public void execute(ICalendar srcCal, ITextView view) {
    String startStr = Command.getWordAfter("between", cmd);
    String endStr = Command.getWordAfter("and", cmd);
    String target = Command.getWordAfter("--target", cmd);
    String toDate = Command.getWordAfter("to", cmd);

    ICalendar dstCal = mgr.getCalendar(target);

    LocalDate dStart = LocalDate.parse(startStr);
    LocalDate dEnd = LocalDate.parse(endStr);
    LocalDate atStart = LocalDate.parse(toDate);

    dstCal.copyEventsAndShift(dStart, dEnd, srcCal, atStart);

    view.takeMessage(String.format("Copied events %s --> %s from %s to %s starting %s",
            startStr, endStr, currentCalendar, target, toDate));
  }
}
