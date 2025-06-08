package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Calendar implements ICalendar {
  private final List<IEvent> eventList = new ArrayList<>();
  private final List<IEventSeries> seriesList = new ArrayList<>();
  private static final IDateTimeFacade facade = new DateTimeFacade();

  @Override
  public void addEvent(IEvent event) {
    ensureDistinct(event);
    eventList.add(event);
  }

  @Override
  public void removeEvent(IEvent event) {
    eventList.remove(event);
  }

  @Override
  public void addEventSeries(IEventSeries series) {
    if (series == null || series.getEvents().isEmpty()) {
      throw new IllegalArgumentException("Event series cannot be null or empty");
    }
    for (IEvent e : series.getEvents()) {
      ensureDistinct(e);
    }
    seriesList.add(series);
  }

  @Override
  public void removeEventSeries(IEventSeries series) {

  }

  @Override
  public void replaceEvent(IEvent oldEvent, IEvent newEvent) {
    int idx = eventList.indexOf(oldEvent);
    if (idx < 0) {
      throw new IllegalArgumentException("Event to replace not found: " + oldEvent);
    }
    eventList.set(idx, newEvent);
  }

  @Override
  public List<IEvent> getEvents() {
    return null;
  }

  @Override
  public List<IEvent> getScheduleInRange(LocalDate start, LocalDate end) {
    List<IEvent> results = new ArrayList<>();
    for (IEvent e : eventList) {
      LocalDate d = e.getStartDate();
      if ((d.isEqual(start) || d.isAfter(start)) &&
              (d.isEqual(end)   || d.isBefore(end))) {
        results.add(e);
      }
    }
    for (IEventSeries s : seriesList) {
      for (IEvent e : s.getEvents()) {
        LocalDate d = e.getStartDate();
        if ((d.isEqual(start) || d.isAfter(start)) &&
                (d.isEqual(end)   || d.isBefore(end))) {
          results.add(e);
        }
      }
    }
    return results.stream()
            .sorted(new EventComparator())
            .collect(Collectors.toList());
  }

  @Override
  public IEventSeries getSeriesFor(IEvent event) {
    for (IEventSeries s : seriesList) {
      if (s.getEvents().contains(event)) {
        return s;
      }
    }
    return null;
  }

  @Override
  public void replaceSeries(IEventSeries oldSeries, IEventSeries newSeries) {
    int idx = seriesList.indexOf(oldSeries);
    if (idx < 0) {
      throw new IllegalArgumentException("Series to replace not found: " + oldSeries);
    }
    seriesList.set(idx, newSeries);
  }

  private void ensureDistinct(IEvent e) {
    for (IEvent existing : eventList) {
      if (existing.equals(e)) {
        throw new IllegalArgumentException("Duplicate event: " + e);
      }
    }
    for (IEventSeries s : seriesList) {
      for (IEvent existing : s.getEvents()) {
        if (existing.equals(e)) {
          throw new IllegalArgumentException("Duplicate event in series: " + e);
        }
      }
    }
  }

}
