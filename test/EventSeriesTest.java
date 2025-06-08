import org.junit.Before;
import org.junit.Test;

import java.util.List;

import model.DateTimeFacade;
import model.Event;
import model.EventSeries;
import model.IDateTimeFacade;
import model.IEvent;

import static org.junit.Assert.assertEquals;
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
  public void stubTest() {
    EventSeries series = seriesBuilder
            .subject("Fishing")
            .eventStartDate(1, 1, 2021)
            .eventStartTime(10, 0)
            .eventEndDate(1, 1, 2021)
            .eventEndTime(12, 0)
            .seriesEndDateFromWeeks(3)
            .weekDays("U")
            .buildSeries();

    for (IEvent eSeries : series) {
      System.out.println(eSeries.toString());
    }
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
  public void SeriesCreatesNothingWhenNoRecurringWeekDaysAreSet() {
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
}
