package model;

public interface ICalendarManager {
  void getCalendars();
  void getCalendar(String name);

  void addCalendar(String name, ICalendar calendar);      // needs somewhere which creates calendars

  void removeCalendar(String name);



}
