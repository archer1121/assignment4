package model;

import java.time.LocalDate;
import java.util.List;

public interface ICalendar {

  void createEvent(Event event);

  void createEventSeries(List<Event> series);

  /**
   * Replace a specific existing event with a new one.
   * @param oldEvent  the event to look up (matched using equals())
   * @param newEvent  the event to put in its place
   */
  void editEvent(Event oldEvent, Event newEvent);


  List<Event> getScheduleInRange(LocalDate start, LocalDate end);



}
