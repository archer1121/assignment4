package model;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * An Interface containing getters which an Event must have. These getters ensure that
 * implementations have the correct data and functionality that is found in an event
 */
public interface IEvent {
  /**
   * Returns the subject of this event.
   *
   * @return subject of the event.
   */
  String getSubject();

  /**
   * Returns the start date of this event.
   *
   * @return Start date in form of a LocalDate.
   */
  LocalDate getStartDate();

  /**
   * Returns the start time of this event.
   *
   * @return The start time of this event formatted as a LocalTime.
   */
  LocalTime getStartTime();

  /**
   * Returns the end date of this event.
   *
   * @return end date in form of a LocalDate.
   */
  LocalDate getEndDate();

  /**
   * Returns the end time of this event.
   *
   * @return The end time of this event formatted as a LocalTime.
   */
  LocalTime getEndTime();

  /**
   * Returns the location of this event.
   *
   * @return Whether the location is physical or online, or unspecified.
   */
  EventLocation getLocation();

  /**
   * Returns the status of this event.
   *
   * @return Whether the status is public or private.
   */
  EventStatus getStatus();

  /**
   * Returns the description of this event
   *
   * @return A String containing the description for this event.
   */
  String getDescription();

  /**
   * Returns whether this event is all day or not.
   *
   * @return true if the event starts at the lastest 8 and ends at th earliest 17 on the same day.
   */
  boolean isAllDayEvent();
}
