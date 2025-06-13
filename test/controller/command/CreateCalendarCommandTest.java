package controller.command;

import model.Calendar;
import model.ICalendar;
import model.ICalendarManager;
import model.CalendarManager;
import org.junit.Before;
import org.junit.Test;
import view.ITextView;
import view.TextView;

import java.time.ZoneId;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests for creating a calendar.
 */
public class CreateCalendarCommandTest {
  private ICalendarManager mgr;
  private ITextView view;

  @Before
  public void setUp() {
    mgr  = new CalendarManager();
    view = new TextView(new StringBuilder());
  }

  @Test
  public void testCreateCalendarSucceeds() {
    String cmd = "create calendar --name Work --timezone Europe/Paris";
    new CreateCalendarCommand(cmd).execute(mgr, view);

    // Manager now contains “Work”
    ICalendar cal = mgr.getCalendar("Work");
    assertNotNull(cal);
    assertEquals(ZoneId.of("Europe/Paris"), cal.getTimeZone());

    // View got the right confirmation
    List<String> msgs = view.getTextInBuffer();
    assertEquals(1, msgs.size());
    assertTrue(msgs.get(0).contains("Created calendar \"Work\""));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateCalendarDuplicateNameFails() {
    mgr.addCalendar("Home", new Calendar());
    String cmd = "create calendar --name Home --timezone Asia/Tokyo";
    new CreateCalendarCommand(cmd).execute(mgr, view);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateCalendarBadTzFails() {
    String cmd = "create calendar --name Foo --timezone NoSuch/Zone";
    new CreateCalendarCommand(cmd).execute(mgr, view);
  }
}
