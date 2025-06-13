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

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests for copy event single.
 */
public class CopyEventCommandTest {
  private ICalendarManager mgr;
  private ITextView view;

  @Before
  public void setUp() {
    mgr  = new CalendarManager();
    mgr.addCalendar("A", new Calendar());
    mgr.addCalendar("B", new Calendar());
    view = new TextView(new StringBuilder());

    // add one event to A
    ICalendar a = mgr.getCalendar("A");
    IEvent ev = Event.getBuilder()
            .subject("Meeting")
            .startDate(10,6,2025)
            .startTime(9,0)
            .endDate(10,6,2025)
            .endTime(10,0)
            .buildEvent();
    a.addEvent(ev);
  }

  @Test
  public void testCopySingleEvent() {
    String cmd = "copy event Meeting on 2025-06-10T09:00 --target B to 2025-06-11T14:30";
    CopyEventCommand c = new CopyEventCommand(cmd, mgr, "A");

    ICalendar a = mgr.getCalendar("A");
    c.execute(a, view);

    ICalendar b = mgr.getCalendar("B");
    List<IEvent> all = b.getScheduleInRange(
            LocalDateTime.parse("2025-06-11T14:30").toLocalDate(),
            LocalDateTime.parse("2025-06-11T14:30").toLocalDate()
    );
    assertEquals(1, all.size());
    IEvent copy = all.get(0);
    assertEquals("Meeting", copy.getSubject());
    assertEquals(LocalDateTime.parse("2025-06-11T14:30").toLocalTime(), copy.getStartTime());

    List<String> msgs = view.getTextInBuffer();
    assertEquals(1, msgs.size());
    assertTrue(msgs.get(0).contains("Copied"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCopyMissingEventFails() {
    String cmd = "copy event Foo on 2025-06-10T09:00 --target B to 2025-06-11T14:30";
    new CopyEventCommand(cmd, mgr, "A")
            .execute(mgr.getCalendar("A"), view);
  }
}
