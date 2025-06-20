package model;

import java.util.List;

/**
 * An Interface for Calendar managers. This Interface allows them to add, remove, and change
 * names of its calendars.
 */
public interface ICalendarManager {
  /**
   * Returns a list containing the names of all calendars managed by this instance.
   * @return List of calendar names
   */
  List<String> getCalendars();

  /**
   * Gets a calendar from the provided name.
   * @param name The name of the desired calendar.
   * @return the calendar, or null if not found.
   */
  ICalendar getCalendar(String name);

  /**
   * Performs an in place name change of a calendar.
   * @param oldName The name to be changed
   * @param newName The name to be set
   */
  void changeName(String oldName, String newName);

  /**
   * Adds the provided calendar to this manager, identified by the given name.
   * @param name The name which will identify this calendar.
   * @param calendar The calendar to be stored.
   */
  void addCalendar(String name, ICalendar calendar);    // needs somewhere which creates calendars

  /**
   * Removes the calendar specified by the given name
   * @param name the identifier of the calendar to be removed.
   */
  void removeCalendar(String name);

  /**
   * Gets or creates the calendar which is under the name 'default'
   * @return the default calendar.
   */
  ICalendar getOrCreateDefault();
}
