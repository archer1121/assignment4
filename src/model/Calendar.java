package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Calendar implements ICalendar {
  private static final IDateTimeFacade facade = new DateTimeFacade();
  private List<IEvent> fullCalendar;
  private List<IEventSeries> seriesList;
  private List<IEvent> eventList;

  public Calendar() {
    eventList = new ArrayList<>();
  }

//  @Override
//  public void addEvent(Event e) {
//    if (e == null) {
//      throw new IllegalArgumentException("Cannot add a null event");
//    }
//    // Optionally check for duplicates, conflicts, etc.
//    eventList.add(e);
//    System.out.println("Added single event: " + e);
//    // from another function i was converting before i renamed the methods
//    int idx = -1;
//    for (int i = 0; i < eventList.size(); i++) {
//      if (eventList.get(i).equals(event)) {
//        idx = i;
//        break;
//      }
//    }
//    if (idx < 0) {
//      throw new IllegalArgumentException("Event to edit not found: " + event);
//    }
//    eventList.set(idx, event);
//    System.out.println("Edited event: " + event);
//  }

  @Override
  public void addEvent(IEvent event) {
    if (event == null) {
      throw new IllegalArgumentException("Cannot add a null event");
    }
    // Optionally check for duplicates, conflicts, etc.
    eventList.add(event);
    // System.out.println("Added single event: " + e);
  }

  @Override
  public void removeEvent(IEvent event) {

  }


  @Override
  public void addEventSeries(IEventSeries series) {
    if (series == null || series.getEvents().isEmpty()) {
      throw new IllegalArgumentException("Event series list cannot be null or empty");
    }
    System.out.println("Added event series of size: " + series.getEvents().size());
    seriesList.add(series);
  }

  @Override
  public void replaceEvent(IEvent oldEvent, IEvent newEvent) {
    if (oldEvent == null || newEvent == null) {
      throw new IllegalArgumentException("Neither oldEvent nor newEvent may be null");
    }
    int idx = -1;
    for (int i = 0; i < eventList.size(); i++) {
      if (eventList.get(i).equals(oldEvent)) {
        idx = i;
        break;
      }
    }
    if (idx < 0) {
      throw new IllegalArgumentException("Event to replace not found: " + oldEvent);
    }
   // eventList.set(idx, newEvent);
  }

  @Override
  public List<IEvent> getEvents() {
    List<IEvent> fullList = new ArrayList<>();
    for (IEvent e : eventList) {
      fullList.add(e);
    }
    for (IEventSeries s : seriesList) {
      fullList.addAll(s.getEvents());
    }
    return fullList.stream().sorted(new Event.EventComparator()).collect(Collectors.toList());
  }


  @Override
  public List<IEvent> getScheduleInRange(LocalDate start, LocalDate end) {
    List<IEvent> result = new ArrayList<>();
    for (IEvent e : eventList) {
      LocalDate d = e.getStartDate();
      if ((d.isAfter(start) || d.isEqual(start)) &&
              (d.isBefore(end) || d.isEqual(end))) {
        result.add(e);
      }
    }
    //System.out.println("getScheduleInRange(" + start + ", " + end + ") -> " + result.size() + " events");
    return result;
  }

  private void ensureDistinct(IEvent e) {
    for (IEventSeries s : seriesList) {
      for (IEvent event : s.getEvents()) {
        if (e.equals(event)) {
          throw new IllegalArgumentException("Calendar already has this event: " + event);
        }
      }
    }
    for (IEvent event : eventList) {
      if (e.equals(event)) {
        throw new IllegalArgumentException("Calendar already has this event: " + event);
      }
    }
  }
}
