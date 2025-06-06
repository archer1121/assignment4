import org.junit.Before;
import org.junit.Test;

import model.EventSeries;
import model.IEvent;

public class EventSeriesTest {
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
}
