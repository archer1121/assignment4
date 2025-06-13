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

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Test copying events on a given date.
 */
public class CopyEventsOnDateCommandTest {
  private ICalendarManager mgr;
  private ITextView view;

  @Before
  public void setUp() {
    mgr  = new CalendarManager();
    mgr.addCalendar("A", new Calendar());
    mgr.addCalendar("B", new Calendar());
    view = new TextView(new StringBuilder());

    ICalendar a = mgr.getCalendar("A");
    // two on 2025‐06‐10, one on 2025‐06‐11
    IEvent e1 = Event.getBuilder()
            .subject("Ev1")
            .startDate(10,6,2025)
            .startTime(8,0)
            .endDate(10,6,2025)
            .endTime(9,0)
            .buildEvent();
    IEvent e2 = Event.getBuilder()
            .subject("Ev2")
            .startDate(10,6,2025)
            .startTime(10,0)
            .endDate(10,6,2025)
            .endTime(11,0)
            .buildEvent();
    IEvent e3 = Event.getBuilder()
            .subject("Ev3")
            .startDate(11,6,2025)
            .startTime(12,0)
            .endDate(11,6,2025)
            .endTime(13,0)
            .buildEvent();
    a.addEvent(e1);
    a.addEvent(e2);
    a.addEvent(e3);
  }

  @Test
  public void testCopyEventsOnDate() {
    String cmd = "copy events on 2025-06-10 --target B to 2025-06-12";
    CopyEventsOnDateCommand c = new CopyEventsOnDateCommand(cmd, mgr, "A");
    c.execute(mgr.getCalendar("A"), view);

    ICalendar b = mgr.getCalendar("B");
    List<IEvent> all = b.getScheduleInRange(
            java.time.LocalDate.of(2025,6,12),
            java.time.LocalDate.of(2025,6,12)
    );
    // only two from 06-10 show up on 06-12, with same times
    assertEquals(2, all.size());
    assertEquals("Ev1", all.get(0).getSubject());
    assertEquals("Ev2", all.get(1).getSubject());
  }
}
