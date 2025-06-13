package controller.command;

import model.Calendar;
import model.ICalendar;
import model.ICalendarManager;
import model.CalendarManager;
import model.Event;
import model.IEvent;
import org.junit.Before;
import org.junit.Test;
import view.ITextView;
import view.TextView;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.*;

public class CopyEventsBetweenCommandTest {
  private ICalendarManager mgr;
  private ITextView view;

  @Before
  public void setUp() {
    mgr  = new CalendarManager();
    mgr.addCalendar("A", new Calendar());
    mgr.addCalendar("B", new Calendar());
    view = new TextView(new StringBuilder());

    ICalendar a = mgr.getCalendar("A");
    // events on 6/1, 6/5, 6/10
    a.addEvent(Event.getBuilder()
            .subject("One")
            .startDate(1,6,2025).startTime(9,0)
            .endDate(1,6,2025).endTime(10,0)
            .buildEvent());
    a.addEvent(Event.getBuilder()
            .subject("Five")
            .startDate(5,6,2025).startTime(11,0)
            .endDate(5,6,2025).endTime(12,0)
            .buildEvent());
    a.addEvent(Event.getBuilder()
            .subject("Ten")
            .startDate(10,6,2025).startTime(13,0)
            .endDate(10,6,2025).endTime(14,0)
            .buildEvent());
  }

  @Test
  public void testCopyBetween() {
    String cmd = "copy events between 2025-06-01 and 2025-06-05 --target B to 2025-07-01";
    CopyEventsBetweenCommand c =
            new CopyEventsBetweenCommand(cmd, mgr, "A");

    c.execute(mgr.getCalendar("A"), view);

    ICalendar b = mgr.getCalendar("B");
    // both 6/1 and 6/5 get shifted to 7/1 and 7/5
    List<IEvent> all = b.getScheduleInRange(
            LocalDate.of(2025,7,1),
            LocalDate.of(2025,7,5)
    );
    assertEquals(2, all.size());
    assertEquals("One", all.get(0).getSubject());
    assertEquals("Five", all.get(1).getSubject());
  }
}
