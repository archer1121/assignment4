import org.junit.Before;
import org.junit.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

import model.DateTimeFacade;
import model.Event;
import model.EventSeries;
import model.IDateTimeFacade;
import model.IEvent;
import model.IEventSeries;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;

/**
 * Class containing tests for the various methods in the EventSeries Class.
 */
public class EventSeriesTest {
  private static final IDateTimeFacade facade = new DateTimeFacade();
  EventSeries.EventSeriesBuilder seriesBuilder;

  @Before
  public void setUp() {
    seriesBuilder = EventSeries.getBuilder();
  }

  @Test
  public void seriesThrowsForEventsWithNonEqualStartAndEndDates() {
    seriesBuilder = seriesBuilder
            .subject("Fishing")
            .eventEndDate(5, 1, 2021)
            .eventStartDate(1, 1, 2021)
            .eventStartTime(10, 0)
            .eventEndTime(10, 0)
            .seriesEndDate(facade.dateOf(4,2,2021));
    assertThrows(IllegalArgumentException.class, () -> seriesBuilder.buildSeries());
  }

  @Test
  public void seriesThrowsWhenEndDateIsBeforeStartDate() {
    seriesBuilder = seriesBuilder
            .subject("Fishing")
            .eventEndDate(1, 1, 2021)
            .eventStartDate(5, 1, 2021)
            .eventStartTime(10, 0)
            .eventEndTime(10, 0)
            .seriesEndDate(facade.dateOf(4,2,2021));;
    assertThrows(IllegalArgumentException.class, () -> seriesBuilder.buildSeries());
  }

  @Test
  public void seriesThrowsWhenNoSeriesEndDateIsSet() {
    seriesBuilder = seriesBuilder
            .subject("Fishing")
            .eventEndDate(1, 1, 2021)
            .eventStartDate(5, 1, 2021)
            .eventStartTime(10, 0)
            .eventEndTime(10, 0);
    assertThrows(IllegalArgumentException.class, () -> seriesBuilder.buildSeries());
  }
  @Test
  public void seriesCreatesNothingWhenNoRecurringWeekDaysAreSet() {
    EventSeries s = seriesBuilder
            .subject("Fishing")
            .eventEndDate(1, 1, 2021)
            .eventStartDate(1, 1, 2021)
            .eventStartTime(10, 0)
            .eventEndTime(10, 0)
            .seriesEndDate(facade.dateOf(4,2,2021))
            .weekDays("")
            .buildSeries();
    assertEquals("", s.getRecurringWeekDays());
    assertEquals(List.of(), s.getEvents());
  }

  @Test
  public void baseEventIsSetCorrectly() {
    EventSeries s = seriesBuilder
            .subject("Fishing")
            .eventEndDate(1, 1, 2021)
            .eventStartDate(1, 1, 2021)
            .eventStartTime(10, 0)
            .eventEndTime(13, 0)
            .seriesEndDate(facade.dateOf(4,2,2021))
            .weekDays("")
            .buildSeries();
    Event e = Event.getBuilder()
            .subject("Fishing")
            .startDate(1,1,2021)
            .startTime(10,0)
            .endDate(1,1,2021)
            .endTime(13,0)
            .buildEvent();
    assertEquals(e, s.getBaseEvent());
  }

  @Test
  public void shiftEventSeriesTimeZoneWorks() {
    EventSeries original = EventSeries.getBuilder()
            .subject("TimeZone")
            .eventStartDate(1, 6, 2025)
            .eventEndDate(1, 6, 2025)
            .eventStartTime(8, 0)
            .eventEndTime(9, 0)
            .weekDays("W")
            .seriesEndDate(LocalDate.of(2025, 6, 12))
            .buildSeries();

    IEventSeries shifted = original.shiftTimeZone(ZoneId.of("America/New_York"), ZoneId.of("UTC"));

    List<IEvent> events = shifted.getEvents();
    assertEquals(2, events.size()); // Wednesdays: June 4, 11
    assertEquals(LocalTime.of(12, 0), events.get(0).getStartTime());
    assertEquals(LocalTime.of(13, 0), events.get(0).getEndTime());
  }

  @Test
  public void eventSeriesAreConsistentWithStartAndEndDates() {
    EventSeries series = EventSeries.getBuilder()
            .subject("event date consistency")
            .eventStartDate(3, 6, 2025)  // example date
            .eventEndDate(3, 6, 2025)
            .eventStartTime(10, 0)
            .eventEndTime(11, 0)
            .weekDays("MTWRF")
            .seriesEndDate(LocalDate.of(2025, 6, 10))
            .buildSeries();

    for (IEvent event : series.getEvents()) {
      assertEquals(event.getStartDate(), event.getEndDate());
    }
  }

  @Test
  public void seriesOccurrenceCountIsCorrect() {
    EventSeries series = EventSeries.getBuilder()
            .subject("christmas")
            .eventStartDate(2, 6, 2025)
            .eventEndDate(2, 6, 2025)
            .eventStartTime(10, 0)
            .eventEndTime(11, 0)
            .weekDays("M")
            .seriesEndDate(LocalDate.of(2025, 6, 30))
            .buildSeries();

    List<IEvent> events = series.getEvents();

    // should be 5 days, the 2nd, 9, 16, 23, 30
    int correctCount = 5;

    assertEquals(correctCount, events.size());
  }

  @Test
  public void eventSeriesRepeatsUntilEndDate() {
    EventSeries series = EventSeries.getBuilder()
            .subject("christmas")
            .eventStartDate(2, 6, 2025)
            .eventEndDate(2, 6, 2025)
            .eventStartTime(10, 0)
            .eventEndTime(11, 0)
            .weekDays("MTWR")
            .seriesEndDate(LocalDate.of(2025, 6, 12))
            .buildSeries();

    List<IEvent> events = series.getEvents();
    // last event must be on or before
    LocalDate lastDate = events.get(events.size() - 1).getStartDate();
    assertFalse(lastDate.isAfter(LocalDate.of(2025, 6, 12)));
  }

  @Test
  public void eventSeriesRepeatsOnCorrectWeekDays() {
    EventSeries series = EventSeries.getBuilder()
            .subject("Weekday Repeats Test")
            .eventStartDate(2, 6, 2025)
            .eventEndDate(2, 6, 2025)
            .eventStartTime(10, 0)
            .eventEndTime(11, 0)
            .weekDays("MTWRF")
            .seriesEndDate(LocalDate.of(2025, 6, 12))
            .buildSeries();

    List<IEvent> events = series.getEvents();

    List<DayOfWeek> expectedDays = List.of(
            DayOfWeek.MONDAY, DayOfWeek.TUESDAY,
            DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY,
            DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY
    );

    List<DayOfWeek> actualDays = events.stream()
            .map(e -> e.getStartDate().getDayOfWeek()).collect(Collectors.toList());

    assertEquals(expectedDays, actualDays);
  }
}
