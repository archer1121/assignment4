package controller.command;

import model.Calendar;
import model.Event;
import model.ICalendar;
import model.IEvent;

import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * JUnit 4 tests for CreateCommand. Valid “create event …” strings should not throw;
 * malformed strings should throw IllegalArgumentException.
 */
public class CreateCommandTest {

  private final ICalendar dummyModel = new ICalendar() {


    @Override
    public void createEvent(Event event) {

    }

    @Override
    public void createEventSeries(List<Event> series) {

    }

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
  @Test
  public void testEventBuilderValid() {
    // Build an event from 2025-06-10T09:00 → 2025-06-10T10:00, subject “Meeting”
    IEvent e = Event.getBuilder()
            .subject("Meeting")
            .startDate(10, 6, 2025)
            .startTime(9, 0)
            .endDate(10, 6, 2025)
            .endTime(10, 0)
            .buildEvent();

    assertEquals("Meeting", e.getSubject());
    assertEquals(LocalDate.of(2025, 6, 10), e.getStartDate());
    assertEquals(LocalTime.of(9, 0), e.getStartTime());
    assertEquals(LocalDate.of(2025, 6, 10), e.getEndDate());
    assertEquals(LocalTime.of(10, 0), e.getEndTime());
  }

  @Test
  public void testCreateEventAddsToList() {
    ICalendar cal = new Calendar();
    IEvent e = Event.getBuilder()
            .subject("Lunch")
            .startDate(15, 6, 2025)
            .startTime(12, 0)
            .endDate(15, 6, 2025)
            .endTime(13, 0)
            .buildEvent();

    cal.createEvent((Event) e);
    List<Event> all = cal.getScheduleInRange(LocalDate.of(2025, 6, 15),
            LocalDate.of(2025, 6, 15));
    assertEquals(1, all.size());
    assertEquals(e, all.get(0));
  }


}
