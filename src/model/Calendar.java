package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * An Implementation of ICalendar which holds two separate lists for events and Event Series.
 * This calendar only combines the two when queried using getEvents().
 */
public class Calendar implements ICalendar {
  private static final IDateTimeFacade facade = new DateTimeFacade();
  private final List<IEvent> fullCalendar;
  private final List<IEventSeries> seriesList;
  private final List<IEvent> eventList;

  /**
   * Constructs a new Calendar.
   */
  public Calendar() {
    eventList = new ArrayList<>();
    fullCalendar = new ArrayList<>();
    seriesList = new ArrayList<>();
  }

  @Override
  public void addEvent(IEvent event) {
    if (event == null) {
      throw new IllegalArgumentException("Cannot add a null event");
    }
    assertAbsense(event);
    eventList.add(event);
  }

  @Override
  public void removeEvent(IEvent event) {
    if (eventList.contains(event)) {
      eventList.remove(event);
    } else {
      IEventSeries removal = null;
      IEventSeries updatedSeries = null;
      for (IEventSeries series : seriesList) {
        if (series.getEvents().contains(event)) {
          removal = series;
          List<IEvent> updated = series.getEvents();
          updated.remove(event);
          updatedSeries = series.adopt(updated);
          break;
        }
      }
      if (removal != null && updatedSeries != null) {
        seriesList.remove(removal);
        seriesList.add(updatedSeries);
      }
    }
  }


  @Override
  public void addEventSeries(IEventSeries series) {

    if (series == null || series.getEvents().isEmpty()) {
      throw new IllegalArgumentException("Event series list cannot be null or empty");
    }
    System.out.println("Added event series of size: " + series.getEvents().size());
    for (int i = 0; i < series.getEvents().size(); i++) {
      assertAbsense(series.getEvents().get(i));
    }
    seriesList.add(series);
  }

  @Override
  public void removeEventSeries(IEventSeries series) {
    seriesList.remove(series);
  }

  @Override
  public void replaceEvent(IEvent oldEvent, IEvent newEvent) {
    if (oldEvent == null || newEvent == null) {
      throw new IllegalArgumentException("Neither oldEvent nor newEvent may be null");
    }
    assertAbsense(newEvent);
    // find in event list
    if (eventList.contains(oldEvent)) {
      int idx = -1;
      for (int i = 0; i < eventList.size(); i++) {
        if (eventList.get(i).equals(oldEvent)) {
          eventList.set(i, newEvent);
          idx = i;
          break;
        }
      }
    } else {

      for (IEventSeries series : seriesList) {

        if (series.getEvents().contains(oldEvent)) {
          if (!(series instanceof EventSeries)) {
            throw new IllegalArgumentException("no replacing support for non EventSeries object");
          }
          SeriesEditor s = new SeriesEditor((EventSeries) series);
          s.replace(oldEvent, newEvent);
          seriesList.remove(series);
          addEventSeries(s.getSeries());
        }
      }
    }

    // eventList.set(idx, newEvent);
  }

  @Override
  public List<IEvent> getEvents() {
    List<IEvent> fullList = new ArrayList<>(eventList);
    for (IEventSeries s : seriesList) {
      fullList.addAll(s.getEvents());
    }
    return fullList.stream().sorted(new IEventComparator()).collect(Collectors.toList());
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

  private void assertAbsense(IEvent e) {
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
