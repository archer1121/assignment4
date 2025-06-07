package model;

import java.time.LocalDate;
import java.util.List;

public interface ICalendar {

  void addEvent(IEvent event);

  void removeEvent(IEvent event);

  void addEventSeries(IEventSeries series);
  /**
   * Replace a specific existing event with a new one.
   * @param oldEvent  the event to look up (matched using equals())
   * @param newEvent  the event to put in its place
   */
  void replaceEvent(IEvent oldEvent, IEvent newEvent);

  List<IEvent> getEvents();

  List<IEvent> getScheduleInRange(LocalDate start, LocalDate end);



}
