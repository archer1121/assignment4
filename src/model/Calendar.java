package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Calendar implements ICalendar {

  private List<Event> eventList;

  public Calendar() {
    eventList = new ArrayList<>();
  }

  @Override
  public void createEvent(Event e) {
    if (e == null) {
      throw new IllegalArgumentException("Cannot add a null event");
    }
    // Optionally check for duplicates, conflicts, etc.
    eventList.add(e);
    System.out.println("Added single event: " + e);
  }


  @Override
  public void createEventSeries(List<Event> series) {
    if (series == null || series.isEmpty()) {
      throw new IllegalArgumentException("Event series list cannot be null or empty");
    }
    eventList.addAll(series);
    System.out.println("Added event series of size: " + series.size());
  }

  @Override
  public void editEvent(Event event) {
    // For simplicity, assume that Event.equals(...) is defined so you can find and replace it.
    // In practice, you might identify events by subject+start time, etc.
    int idx = -1;
    for (int i = 0; i < eventList.size(); i++) {
      if (eventList.get(i).equals(event)) {
        idx = i;
        break;
      }
    }
    if (idx < 0) {
      throw new IllegalArgumentException("Event to edit not found: " + event);
    }
    eventList.set(idx, event);
    System.out.println("Edited event: " + event);
  }

  @Override
  public List<Event> getScheduleInRange(LocalDate start, LocalDate end) {
    List<Event> result = new ArrayList<>();
    for (Event e : eventList) {
      LocalDate d = e.getStartDate();
      if ((d.isAfter(start) || d.isEqual(start)) &&
              (d.isBefore(end) || d.isEqual(end))) {
        result.add(e);
      }
    }
    System.out.println("getScheduleInRange(" + start + ", " + end + ") -> " + result.size() + " events");
    return result;
  }
}
