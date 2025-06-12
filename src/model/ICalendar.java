package model;

import java.time.LocalDate;
import java.util.List;
import java.time.ZoneId;

/**
 * An Interface which represents a calendar that can hold events and event series separately.
 */
public interface ICalendar {

  /**
   * Returns the timezone that this calendar is in.
   * @return ZoneId representing this calendar's time zone.
   */
  ZoneId getTimeZone();

  /**
   * Sets the timezone of this calendar. When this method is called, all events held in the calendar
   * will be shifted by the offest created from the difference between the previous timezone, and
   * the new one.
   * @param timeZone The new timezone to set.
   * @return ICalendar with the resulting changes.
   */
  ICalendar setTimeZone(ZoneId timeZone);


  /**
   * Adds an event to the calendar.
   * @param event The IEvent to Add.
   */
  void addEvent(IEvent event);

  /**
   * Removes an event from the calendar.
   * @param event The IEvent to be removed.
   */
  void removeEvent(IEvent event);

  /**
   * Adds and Event Series to the calendar.
   * @param series The IEventSeries to add.
   */
  void addEventSeries(IEventSeries series);

  /**
   * Removes an Event Series from the calendar.
   * @param series The IEventSeries to be Removed.
   */
  void removeEventSeries(IEventSeries series);

  /**
   * Replace a specific existing event with a new one.
   * @param oldEvent the event to look up (matched using equals()).
   * @param newEvent the event to put in its place.
   */
  void replaceEvent(IEvent oldEvent, IEvent newEvent);

  /**
   * Returns all events in this calendar.
   * @return List of all events in the calendar.
   */
  List<IEvent> getEvents();

  /**
   * Gets a list of Events within the start and end dates.
   * @param start The start date.
   * @param end The end date.
   * @return A list containing all Events found within the range
   */
  List<IEvent> getScheduleInRange(LocalDate start, LocalDate end);


  IEventSeries getSeriesFor(IEvent event);

  void replaceSeries(IEventSeries oldSeries, IEventSeries newSeries);
}
