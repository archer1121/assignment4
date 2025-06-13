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
   * Copies all events yielded by from in the given range into this calendar and shifts them
   * so that the earliest date in the range matches atStartDate, then the others follow, keeping
   * their original pattern and structure.
   * @param rangeStart The start date of the range.
   * @param rangeEnd  The end date of the range.
   * @param from The calendar to copy from.
   * @param atStartDate new position in the timeline the range of events will begin at.
   * */
  void copyEventsAndShift(
          LocalDate rangeStart, LocalDate rangeEnd, ICalendar from, LocalDate atStartDate
  );

  /**
   * Copies all events yielded by from in the given range into this calendar.
   * @param rangeStart The start date of the range.
   * @param rangeEnd  The end date of the range.
   * @param from The calendar to copy from.
   */
  void copyEvents(LocalDate rangeStart, LocalDate rangeEnd, ICalendar from);

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

  /**
   * Takes an event and finds the series its located in if possible.
   * @param event the event seed which is used to find a series.
   * @return the series if found, or null otherwise.
   */
  IEventSeries getSeriesFor(IEvent event);

  /**
   * Replaces the oldSeries in the calendar with the newSeries. Useful for bulk operations and
   * series edits when combined with SeriesEditor and methods which utilize a series builder.
   * @param oldSeries The series to be replaced.
   * @param newSeries The replacement series.
   */
  void replaceSeries(IEventSeries oldSeries, IEventSeries newSeries);
}
