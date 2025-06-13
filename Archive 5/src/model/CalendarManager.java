package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalendarManager implements ICalendarManager {
  Map<String, ICalendar> calendars;

  public CalendarManager() {
    this.calendars = new HashMap<>();
  }

  // in CalendarManager.java
  @Override
  public List<String> getCalendars() {
    // Return the keys (names) of the map
    return new ArrayList<>(this.calendars.keySet());
  }


  @Override
  public ICalendar getCalendar(String name) {
    assertContainsCalendar(name);
    return calendars.get(name);
  }

  @Override
  public void changeName(String oldName, String newName) {
    assertContainsCalendar(oldName, "Cannot change name. " + oldName + " not in calendar.");
    assertNameNotTaken(newName);

    ICalendar oldCalendar =  calendars.get(oldName);

    calendars.remove(oldName);
    calendars.put(newName, oldCalendar);
  }

  @Override
  public void addCalendar(String name, ICalendar calendar) {
    assertNameNotTaken(name);
    calendars.put(name, calendar);
  }

  @Override
  public void removeCalendar(String name) {
    assertContainsCalendar(name, "Cannot remove calendar that doesn't exist: " + name);
    calendars.remove(name);
  }

  private void assertContainsCalendar(String name) {
    assertContainsCalendar(name, "Calendar '" + name + "' not found" );
  }

  private void assertContainsCalendar(String name, String message) {
    if (calendars.get(name) == null) {
      throw new IllegalArgumentException(message);
    }
  }

  private void assertNameNotTaken(String name) {
    if (calendars.containsKey(name)) {
      throw new IllegalArgumentException("Calendar name already taken: " + name);
    }
  }


}
