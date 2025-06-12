import static org.junit.Assert.*;

import model.Calendar;
import model.IEvent;

import model.*;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

import static org.junit.Assert.*;

public class CalendarTest {
  private ICalendar calendar;
  private IEvent testEvent;
  private IEvent shiftedEvent;
  private ZoneId defaultZone;

  @Before
  public void setup() {
    calendar = new Calendar();
    defaultZone = ZoneId.of("America/New_York");

    testEvent = Event.getBuilder()
            .subject("Test Event")
            .startDate(1, 6, 2025)
            .startTime(9, 0)
            .endDate(1, 6, 2025)
            .endTime(10, 0)
            .buildEvent();

    shiftedEvent = testEvent.shiftDays(3);
  }

  @Test
  public void addEventAddsEvent() {
    calendar.addEvent(testEvent);
    assertTrue(calendar.getEvents().contains(testEvent));
  }

  @Test(expected = IllegalArgumentException.class)
  public void addingDuplicateEventThrows() {
    calendar.addEvent(testEvent);
    calendar.addEvent(testEvent); // should throw
  }

  @Test
  public void removeEventWorks() {
    calendar.addEvent(testEvent);
    calendar.removeEvent(testEvent);
    assertFalse(calendar.getEvents().contains(testEvent));
  }

  @Test
  public void replaceEvent() {
    calendar.addEvent(testEvent);
    calendar.replaceEvent(testEvent, shiftedEvent);
    assertTrue(calendar.getEvents().contains(shiftedEvent));
    assertFalse(calendar.getEvents().contains(testEvent));
  }

  @Test(expected = IllegalArgumentException.class)
  public void replaceEventThrowsWhenEventNotFound() {
    calendar.replaceEvent(testEvent, shiftedEvent);
  }

  @Test
  public void getScheduleInRangeWorks() {
    calendar.addEvent(testEvent);
    LocalDate from = LocalDate.of(2025, 6, 1);
    LocalDate to = LocalDate.of(2025, 6, 5);
    List<IEvent> result = calendar.getScheduleInRange(from, to);
    assertEquals(1, result.size());
    assertTrue(result.contains(testEvent));
  }

  @Test
  public void copyEventsAndShiftWorks() {
    Calendar source = new Calendar();
    source.addEvent(testEvent);

    LocalDate rangeStart = LocalDate.of(2025, 6, 1);
    LocalDate rangeEnd = LocalDate.of(2025, 6, 2);
    LocalDate newStart = LocalDate.of(2025, 6, 10);

    calendar.copyEventsAndShift(rangeStart, rangeEnd, source, newStart);

    List<IEvent> copied = calendar.getEvents();
    assertEquals(1, copied.size());
    assertEquals(testEvent.shiftDays(9), copied.get(0));
  }

  @Test
  public void setTimeZoneSetsZone() {
    calendar.addEvent(testEvent);
    ICalendar shifted = calendar.setTimeZone(ZoneId.of("UTC"));
    assertNotEquals(calendar.getTimeZone(), shifted.getTimeZone());
    assertEquals(ZoneId.of("UTC"), shifted.getTimeZone());
  }

  @Test
  public void addEventSeriesEventsToCalendarWorks() {
    EventSeries series = EventSeries.getBuilder()
            .subject("Series Test")
            .eventStartDate(1, 6, 2025)
            .eventEndDate(1, 6, 2025)
            .eventStartTime(10, 0)
            .eventEndTime(11, 0)
            .weekDays("MTWRF")
            .seriesEndDate(LocalDate.of(2025, 6, 12))
            .buildSeries();

    for (IEvent event : series) {
      calendar.addEvent(event);
    }

    assertEquals(9, calendar.getEvents());
  }


}

