import org.junit.Before;
import org.junit.Test;

import model.Calendar;
import model.CalendarManager;
import model.ICalendar;
import model.ICalendarManager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

/**
 * Class containing test for the CalendarManager.
 */
public class CalendarManagerTest {
  private ICalendarManager manager;

  @Before
  public void setUp() {
    manager =  new CalendarManager();
  }

  @Test
  public void getAllCalendarsReturnsALlCalendars() {
    manager.addCalendar("TestCal1", new Calendar());
    manager.addCalendar("TestCal2", new Calendar());
    assertEquals(2, manager.getCalendars().size());
  }

  @Test
  public void getCalFromNameWorksCorrectly() {
    String name = "TestCal";
    ICalendar calendar = new Calendar();

    manager.addCalendar("TestCal", calendar);
    assertEquals(calendar, manager.getCalendar(name));
  }

  @Test
  public void addCalendarThrowsForDuplicateNames() {
    String name = "TestCal";
    ICalendar calendar = new Calendar();

    manager.addCalendar(name, calendar);

    assertThrows(IllegalArgumentException.class, () -> manager.addCalendar(name, calendar));
  }

  @Test
  public void removeCalendarThrowsForNonExistingCalendar() {
    String name = "TestCal";
    assertThrows(IllegalArgumentException.class, () ->  manager.removeCalendar(name));
  }

  @Test
  public void removeCalendarRemovesCalendar() {
    String name = "TestCal";
    ICalendar calendar = new Calendar();

    manager.addCalendar("TestCal", calendar);
    manager.removeCalendar(name);
    assertEquals(0, manager.getCalendars().size());
  }

  @Test
  public void getCalendarThrowsForInvalidName() {
    String name = "TestCal";
    assertThrows(IllegalArgumentException.class, () -> manager.getCalendar(name));
  }

  @Test
  public void changeNameChangesName() {
    String name = "TestCal";
    String newName = "OtherTestCal";

    ICalendar calendar = new Calendar();

    manager.addCalendar(name, calendar);
    manager.changeName(name, newName);

    assertEquals(calendar, manager.getCalendar(newName));
  }

  @Test
  public void changeNameThrowsForAlreadyExistingName() {

    // Declare calendars and their names.
    String name = "TestCal";
    String newName = "OtherTestCal";

    ICalendar calendar1 = new Calendar();
    ICalendar calendar2 = new Calendar();

    // Add them to the manager under their respective names.
    manager.addCalendar(name, calendar1);
    manager.addCalendar(newName, calendar2);

    // Changing the name of one calendar to the name of an existing calendar isn't allowed.
    assertThrows(IllegalArgumentException.class, () -> manager.changeName(name, newName));
  }

  @Test
  public void changeNameThrowsForNonExistingOldName() {
    assertThrows(IllegalArgumentException.class, () -> manager.changeName("Old", "New"));
  }

}
