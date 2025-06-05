package model;

import java.time.LocalDate;
import java.util.List;

public class Calendar implements ICalendar {
  @Override
  public void createEvent() {
    System.out.println("createEvent in model triggered");
  }

  @Override
  public void createEventSeries() {
    System.out.println("createEventSeries in model triggered");
  }

  @Override
  public void editEvent(Event event) {
    System.out.println("editEvent in model triggered");
  }

  @Override
  public List<Event> getScheduleInRange(LocalDate start, LocalDate end) {
    System.out.println("getScheduleInRange in model triggered");
    return null;
  }
}
