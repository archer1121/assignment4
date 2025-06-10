package model;

import java.util.List;

public interface ICalendarManager {
  List<ICalendar> getCalendars();

  ICalendar getCalendar(String name);

  void changeName(String oldName, String newName);

  void addCalendar(String name, ICalendar calendar);    // needs somewhere which creates calendars

  void removeCalendar(String name);
}
