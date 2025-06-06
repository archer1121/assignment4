package controller.command;

import model.ICalendar;
import org.junit.Test;

/**
 * JUnit 4 tests for CreateCommand. Valid “create event …” strings should not throw;
 * malformed strings should throw IllegalArgumentException.
 */
public class CreateCommandTest {

  private final ICalendar dummyModel = new ICalendar() {
    @Override public void createEvent() { }
    @Override public void createEventSeries() { }
    @Override public void editEvent(model.Event event) { }
    @Override public java.util.List<model.Event> getScheduleInRange(java.time.LocalDate start, java.time.LocalDate end) { return null; }
  };

  @Test
  public void testValidCreateSingleTimed() {
    // case 1: no “repeats”
    String cmd = "create event Meeting from 2025-06-10T09:00 to 2025-06-10T10:00";
    new CreateCommand(cmd).execute(dummyModel);
    // no exception → pass
  }

  @Test
  public void testValidCreateTimedSeriesFor() {
    // case 2: “repeats … for … times”
    String cmd = "create event Standup from 2025-06-12T09:00 to 2025-06-12T09:15 repeats MWF for 5 times";
    new CreateCommand(cmd).execute(dummyModel);
  }

  @Test
  public void testValidCreateTimedSeriesUntil() {
    // case 3: “repeats … until …”
    String cmd = "create event Report from 2025-06-01T08:00 to 2025-06-01T09:00 repeats TU until 2025-06-30";
    new CreateCommand(cmd).execute(dummyModel);
  }

  @Test
  public void testValidCreateSingleAllDay() {
    // case 4: “on <date>”, no repeats
    String cmd = "create event Holiday on 2025-12-25";
    new CreateCommand(cmd).execute(dummyModel);
  }

  @Test
  public void testValidCreateAllDaySeriesFor() {
    // case 5: “on <date> repeats … for … times”
    String cmd = "create event Yoga on 2025-06-11 repeats TRF for 3 times";
    new CreateCommand(cmd).execute(dummyModel);
  }

  @Test
  public void testValidCreateAllDaySeriesUntil() {
    // case 6: “on <date> repeats … until …”
    String cmd = "create event Checkup on 2025-07-01 repeats W until 2025-07-31";
    new CreateCommand(cmd).execute(dummyModel);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMalformedCreateMissingKeyword() {
    // Missing “event”
    String cmd = "create evnt BadCommand";
    new CreateCommand(cmd).execute(dummyModel);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMalformedCreateMissingFromTo() {
    // Has “from” but no “to”
    String cmd = "create event Meeting from 2025-06-10T09:00";
    new CreateCommand(cmd).execute(dummyModel);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMalformedCreateMissingOn() {
    // Should be “on <date>”, but missing “on”
    String cmd = "create event Holiday 2025-12-25";
    new CreateCommand(cmd).execute(dummyModel);
  }
}
