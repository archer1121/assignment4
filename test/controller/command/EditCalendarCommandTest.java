package controller.command;

import model.CalendarManager;
import model.ICalendarManager;
import view.ITextView;
import view.TextView;
import org.junit.Before;
import org.junit.Test;

import java.time.ZoneId;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * A Test class to ensure the Edit Calendar Command works as intended.
 */
public class EditCalendarCommandTest {

  private ICalendarManager mgr;
  private ITextView view;

  @Before
  public void setUp() {
    this.mgr  = new CalendarManager();
    this.view = new TextView(new StringBuilder());
  }

  @Test
  public void testRenameCalendar() {
    // Arrange: create a calendar named "Office"
    mgr.addCalendar("Office", new model.Calendar());
    // Act: rename it to "Work"
    new EditCalendarCommand("edit calendar --name Office --property name Work")
            .execute(mgr, view);

    // Assert: getCalendars() returns the new name, not the old one
    List<String> names = mgr.getCalendars();
    assertTrue("Should contain new name", names.contains("Work"));
    assertFalse("Should no longer contain old name", names.contains("Office"));
  }

  @Test
  public void testChangeTimezone() {
    // Arrange: create a calendar named "Home" with default TZ
    mgr.addCalendar("Home", new model.Calendar());

    // Act: change its timezone to Asia/Kolkata
    new EditCalendarCommand(
            "edit calendar --name Home --property timezone Asia/Kolkata")
            .execute(mgr, view);

    // Assert: when we fetch it by name, its timezone has changed
    ZoneId tz = mgr.getCalendar("Home").getTimeZone();
    assertEquals(ZoneId.of("Asia/Kolkata"), tz);
  }
}
