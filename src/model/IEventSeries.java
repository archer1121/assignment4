package model;

import java.time.LocalDate;
import java.util.List;

/**
 * An Interface which all EventSeries Implementations must have. These methods allow querying
 * and utilizing the series as externally needed.
 */
public interface IEventSeries {
  /**
   * returns the root event containing the start date and info for the event series.
   *
   * @return root event of the series.
   */
  IEvent getBaseEvent();

  /**
   * Returns a String containing the weekdays which the series repeats on.
   *
   * @return the week formatted as a string
   */
  String getRecurringWeekDays();

  /**
   * Gets the date that the series ends on.
   *
   * @return Date represented by a LocalDate.
   */
  LocalDate getSeriesEndDate();

  /**
   * Gets all the events listed in the series.
   *
   * @return List of IEvents
   */
  List<IEvent> getEvents();

  /**
   * Returns a copy of this series with the givent list.
   *
   * @param newList the new List of events to represent
   * @return a new EventSeries
   */
  EventSeries adopt(List<IEvent> newList);

}
