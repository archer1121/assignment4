package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Calendar implements ICalendar {
  private static final IDateTimeFacade facade = new DateTimeFacade();
  private final List<IEvent> eventList;
  private final List<IEventSeries> seriesList;
  private final ZoneId zone;


  public Calendar() {
    eventList = new ArrayList<>();
    seriesList = new ArrayList<>();
    zone = ZoneId.of("America/NewYork");
  }

  private Calendar(List<IEvent> eventList, List<IEventSeries> seriesList, ZoneId zone) {
    this.eventList = new ArrayList<>(eventList);
    this.seriesList = new ArrayList<>(seriesList);
    this.zone = zone;
  }

  @Override
  public ZoneId getTimeZone() {
    return zone;
  }

  @Override
  public ICalendar setTimeZone(ZoneId timeZone) {
    List<IEvent> shiftedEventList = new ArrayList<>(eventList);
    List<IEventSeries> shiftedSeriesList = new ArrayList<>(seriesList);

    // this hunk needs to be moved out and into a more suited class.
    for (IEvent e : eventList) {
      shiftedEventList.add(e.shiftTimeZone(zone, timeZone));
    }
    for (IEventSeries s : seriesList) {
      shiftedSeriesList.add(( s.shiftTimeZone(zone, timeZone)));
    }


    // shift everything before mutating. we don't want to keep a stale state in the case that the
    // shift fails.
    return new Calendar(shiftedEventList, shiftedSeriesList, timeZone);
  }

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
    // do we need this if its not in spec?
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
    List<IEvent> allEvents = new ArrayList<>(eventList);




    return List.copyOf(eventList);
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
