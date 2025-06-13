package controller.command;

import model.ICalendar;
import model.ICalendarManager;
import model.IEvent;
import model.Event;
import view.ITextView;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Command for copying one event.
 */
public class CopyEventCommand implements Command {
  private final String cmd;
  private final ICalendarManager mgr;

  /**
   * Makes object for command.
   *
   * @param cmd        test command
   * @param mgr        manager
   * @param srcCalName the calendar to operate from
   */
  public CopyEventCommand(String cmd, ICalendarManager mgr, String srcCalName) {
    this.cmd = cmd.trim();
    this.mgr = mgr;
  }

  @Override
  public void execute(ICalendar srcCal, ITextView view) {
    // parse out pieces
    String subject = Command.getWordAfter("copy event", cmd);
    String onStr = Command.getWordAfter("on", cmd);
    String target = Command.getWordAfter("--target", cmd);
    String toStr = Command.getWordAfter("to", cmd);

    LocalDateTime origDT = LocalDateTime.parse(onStr);
    LocalDateTime newStartDT = LocalDateTime.parse(toStr);

    // 1) find the matching event in the source calendar
    List<IEvent> candidates = srcCal.getScheduleInRange(
            origDT.toLocalDate(), origDT.toLocalDate());
    IEvent toCopy = null;
    for (IEvent e : candidates) {
      if (e.getSubject().equals(subject)
              && e.getStartTime().equals(origDT.toLocalTime())) {
        toCopy = e;
        break;
      }
    }
    if (toCopy == null) {
      throw new IllegalArgumentException(
              "No matching event found with subject=\"" + subject +
                      "\" starting at " + origDT);
    }

    // 2) compute original duration
    LocalDateTime oldStart = LocalDateTime.of(
            toCopy.getStartDate(), toCopy.getStartTime());
    LocalDateTime oldEnd = LocalDateTime.of(
            toCopy.getEndDate(), toCopy.getEndTime());
    long minutes = Duration.between(oldStart, oldEnd).toMinutes();

    // 3) build the new event in the target calendar
    LocalDateTime newEndDT = newStartDT.plusMinutes(minutes);
    Event.EventBuilder b = Event.getBuilder()
            .subject(toCopy.getSubject())
            // optional fields
            .description(toCopy.getDescription())
            .location(toCopy.getLocation())
            .status(toCopy.getStatus())
            // start at newStartDT
            .startDate(newStartDT.getDayOfMonth(),
                    newStartDT.getMonthValue(),
                    newStartDT.getYear())
            .startTime(newStartDT.getHour(),
                    newStartDT.getMinute())
            // end at newEndDT
            .endDate(newEndDT.getDayOfMonth(),
                    newEndDT.getMonthValue(),
                    newEndDT.getYear())
            .endTime(newEndDT.getHour(),
                    newEndDT.getMinute());

    IEvent copied = b.buildEvent();

    // 4) perform the copy
    ICalendar dstCal = mgr.getCalendar(target);
    dstCal.addEvent(copied);

    view.takeMessage(String.format(
            "Copied \"%s\" to %s", subject, target));
  }
}
