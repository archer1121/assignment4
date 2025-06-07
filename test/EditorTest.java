import org.junit.Test;

import model.DateTimeFacade;
import model.Event;
import model.EventLocation;
import model.EventSeries;
import model.EventStatus;
import model.IDateTimeFacade;
import model.IEvent;
import model.SeriesEditor;

public class EditorTest {
  private static final IDateTimeFacade facade = new DateTimeFacade();


  // NOT AN ACTUAL TEST
  @Test
  public void test() {
    IEvent newEvent = Event.getBuilder()
            .subject("Camping")
            .startDate(1, 1, 2021)
            .endDate(1, 1, 2021)
            .startTime(10, 0)
            .endTime(11, 0)
            .description("we are camping")
            .location(EventLocation.PHYSICAL)
            .status(EventStatus.PUBLIC)
            .buildEvent();

    EventSeries s = EventSeries.getBuilder()
            .subject("Fishing")
            .eventEndDate(1, 1, 2021)
            .eventStartDate(1, 1, 2021)
            .eventStartTime(9, 0)
            .eventEndTime(17, 0)
            .seriesEndDateFromWeeks(8)
            //.seriesEndDate(facade.dateOf(4,2,2021))
            .weekDays("F")
            .buildSeries();
    IEvent old = newEvent;
    SeriesEditor e = new SeriesEditor(s);

    for (int i = 0; i < e.getSeries().getEvents().size() ; i++) {
      System.out.println(e.getSeries().getEvents().get(i).toString());
      if (i == 3) {
        old = e.getSeries().getEvents().get(i);
      }
    }
    System.out.println("-----");
    e.replaceAll( newEvent);

    for (int i = 0; i < e.getSeries().getEvents().size() ; i++) {
      System.out.println(e.getSeries().getEvents().get(i).toString());
    }
  }
}
