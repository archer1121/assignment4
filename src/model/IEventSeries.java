package model;

import java.time.LocalDate;
import java.util.List;

public interface IEventSeries {

  IEvent getBaseEvent();

  String getRecurringWeekDays();

  //the amount of weeks that this event occurs.
  LocalDate getSeriesEndDate();

  List<IEvent> getSeries();

}
