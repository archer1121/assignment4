package controller.command;

import model.ICalendar;
import model.Event;
import org.junit.Test;

/**
 * JUnit 4 tests for EditCommand. Valid “edit …” strings should not throw;
 * malformed strings should throw IllegalArgumentException.
 */
public class EditCommandTest {

  private final ICalendar dummyModel = new ICalendar() {
    @Override public void createEvent() { }
    @Override public void createEventSeries() { }
    @Override public void editEvent(Event event) { }
    @Override public java.util.List<Event> getScheduleInRange(java.time.LocalDate start, java.time.LocalDate end) { return null; }
  };

  @Test
  public void testValidEditEventSingle() {
    // “edit event <property> <subject> from <dt1> to <dt2> with <new>”
    String cmd = "edit event location Meeting from 2025-06-10T09:00 to 2025-06-10T10:00 with RoomA";
    new EditCommand(cmd).execute(dummyModel);
  }

  @Test
  public void testValidEditEventsFrom() {
    // “edit events <property> <subject> from <dt> with <new>”
    String cmd = "edit events status Task1 from 2025-06-11T14:00 with Completed";
    new EditCommand(cmd).execute(dummyModel);
  }

  @Test
  public void testValidEditSeriesFrom() {
    // “edit series <property> <subject> from <dt> with <new>”
    String cmd = "edit series description Reminder from 2025-06-12T08:00 with NewDesc";
    new EditCommand(cmd).execute(dummyModel);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMalformedEditMissingProperty() {
    // Missing <property> between “edit event” and <subject>
    String cmd = "edit event Meeting from 2025-06-10T09:00 to 2025-06-10T10:00 with LocationA";
    new EditCommand(cmd).execute(dummyModel);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMalformedEditMissingFrom() {
    // Missing “from”
    String cmd = "edit event location Meeting to 2025-06-10T10:00 with RoomA";
    new EditCommand(cmd).execute(dummyModel);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMalformedEditMissingWith() {
    // Missing “with”
    String cmd = "edit events status Task1 from 2025-06-11T14:00 Completed";
    new EditCommand(cmd).execute(dummyModel);
  }
}
