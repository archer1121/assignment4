package model;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

/**
 * An Interface which all EventSeries Implementations must have. These methods allow querying
 * and utilizing the series as externally needed.
 */
public interface IEventSeries extends Iterable<IEvent> {
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
   * Returns a copy of this series with the given list.
   *
   * @param newList the new List of events to represent
   * @return a new EventSeries
   */
  IEventSeries adopt(List<IEvent> newList);

  /**
   * Treats every event in this series as if it's in the time zone of 'from' and shifts it over
   * to the matching time in 'to'.
   * @param from The initial timezone.
   * @param to The destination timezone.
   * @return A new series reflecting these shifts.
   */
  IEventSeries shiftTimeZone(ZoneId from, ZoneId to);

}
