package model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class EventSeries implements IEventSeries, Iterable<IEvent> {
  private static final IDateTimeFacade facade = new DateTimeFacade();

  public static class EventSeriesBuilder {
    /*
    allowed mutability here as creating new objects when already modifying the event builder
    leads to redundant code and adds runtime overhead.
    */
    private final LocalDate seriesEndDate;
    private final Set<DayOfWeek> weekDays;
    private Event.EventBuilder eventBuilder;
    private final LocalDate initialDate;

    private EventSeriesBuilder() {
      seriesEndDate = null; // Placeholder
      weekDays = new HashSet<>();
      eventBuilder = Event.getBuilder();
      initialDate = null;
    }

    private EventSeriesBuilder(
            Event.EventBuilder eventBuilder,
            Set<DayOfWeek> weekDays,
            LocalDate endDate,
            LocalDate initialDate
    ) {
      this.seriesEndDate = endDate;
      this.eventBuilder = eventBuilder;
      this.weekDays = weekDays;
      this.initialDate = initialDate;
    }

    public EventSeriesBuilder description(String description) {
      eventBuilder = eventBuilder.description(description);
      return this;
    }

    public EventSeriesBuilder subject(String subject) {
      eventBuilder = eventBuilder.subject(subject);
      return this;
    }

    public EventSeriesBuilder location(EventLocation location) {
      eventBuilder = eventBuilder.location(location);
      return this;
    }

    public EventSeriesBuilder eventStartDate(int day, int month, int year) {

      return new EventSeriesBuilder(
              eventBuilder.startDate(day, month, year), weekDays, seriesEndDate,
              facade.dateOf(day, month, year)
      );
    }

    public EventSeriesBuilder eventStartTime(int hour, int minute) {
      eventBuilder = eventBuilder.startTime(hour, minute);
      return this;
    }

    public EventSeriesBuilder eventEndTime(int hour, int minute) {
      eventBuilder = eventBuilder.endTime(hour, minute);
      return this;
    }

    public EventSeriesBuilder eventEndDate(int day, int month, int year) {
      eventBuilder = eventBuilder.endDate(day, month, year);
      return this;
    }

    public EventSeriesBuilder status(EventStatus status) {
      eventBuilder = eventBuilder.status(status);
      return this;
    }

    public EventSeriesBuilder weekDays(String weekDays) {
      Set<DayOfWeek> weekDaySet = new HashSet<>();
      for (char weekDay : weekDays.toCharArray()) {
        if (facade.weekDayFrom(weekDay) != null) {
          weekDaySet.add(facade.weekDayFrom(weekDay));
        }
      }
      return new EventSeriesBuilder(eventBuilder, weekDaySet, seriesEndDate, initialDate);
    }

    public EventSeriesBuilder seriesEndDate(LocalDate date) {
      return new EventSeriesBuilder(eventBuilder, weekDays, date, initialDate);
    }
  // setting the number of weeks you want to recur for
    public EventSeriesBuilder seriesEndDateFromWeeks(int weeks) {
      return new EventSeriesBuilder(
              eventBuilder, weekDays, facade.stepDays(initialDate, 7 * weeks), initialDate
      );
    }

    public EventSeries buildSeries() {
      Event event = eventBuilder.buildEvent();
      if (!event.getStartDate().equals(event.getEndDate())) {
        throw new IllegalArgumentException(
                "Cannot build event series with an event with different start and end dates"
        );
      }
      return new EventSeries(event, weekDays, seriesEndDate);
    }
  }

  private final Event baseEvent;
  private final Set<DayOfWeek> weekDays;
  private final LocalDate endDate;

  private final List<IEvent> eventSeries;

  public static EventSeriesBuilder getBuilder() {
    return new EventSeriesBuilder();
  }

  public static EventSeriesBuilder editSeries(EventSeries series) {
    return new EventSeriesBuilder(
      Event.editEvent(series.baseEvent),
      series.weekDays,
      series.endDate,
      series.baseEvent.getStartDate());
  }

  private EventSeries(Event event, Set<DayOfWeek> weekDays, LocalDate endDate) {
    this.baseEvent = event;
    this.weekDays = weekDays;
    this.endDate = endDate;
    this.eventSeries = constructSeries();
  }

  //currently ignores adding base event to the list unless it falls on a valid weekday.
  // you can make an event on friday,but if the scheduled days are MW then it will only log those
  // days
  private List<IEvent> constructSeries() {
    List<IEvent> series = new ArrayList<>();
    LocalDate currentDate = baseEvent.getStartDate();
    // inclusive stepping
    while (facade.isAfter(facade.stepDays(endDate , 1),  currentDate)) {

      if (weekDays.contains(facade.weekDayOf(currentDate))) {
        //eventSeries.add(baseEvent);
        Event.EventBuilder eventBuilder = Event.getBuilder();

        series.add(
                eventBuilder.subject(baseEvent.getSubject())
                        .location(baseEvent.getLocation())
                        .description(baseEvent.getDescription())
                        .startTime(
                                facade.hourOf(baseEvent.getStartTime()),
                                facade.minuteOf(baseEvent.getEndTime()))
                        .endTime(
                                facade.hourOf(baseEvent.getEndTime()),
                                facade.minuteOf(baseEvent.getEndTime())
                        )
                        .startDate(
                                facade.dayOf(currentDate),
                                facade.monthOf(currentDate),
                                facade.YearOf(currentDate)
                        )
                        .endDate(
                                facade.dayOf(currentDate),
                                facade.monthOf(currentDate),
                                facade.YearOf(currentDate)
                        )
                        .buildEvent());
      }
      currentDate = facade.stepDays(currentDate, 1);

    }
    return series;
  }

  @Override
  public Iterator<IEvent> iterator() {
    return eventSeries.iterator();
  }

  @Override
  public IEvent getBaseEvent() {
    return baseEvent;
  }

  @Override
  public List<IEvent> getSeries() {
    return List.copyOf(eventSeries);
  }

  @Override
  public String getRecurringWeekDays() {
    return facade.weeksAsString(List.copyOf(weekDays));
  }

  @Override
  public LocalDate getSeriesEndDate() {
    return endDate;
  }
}
