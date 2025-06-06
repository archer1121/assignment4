package model;

import java.time.LocalDate;
import java.util.List;

public interface ICalendar {

  void createEvent(Event event);

  void createEventSeries(List<Event> series);

  void editEvent(Event event);

  List<Event> getScheduleInRange(LocalDate start, LocalDate end);



}
