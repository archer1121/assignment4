package model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * A class which holds a series of events along with a few unique methods for more querying of
 * the event series.
 */
public class EventSeries implements IEventSeries {
  private static final IDateTimeFacade facade = new DateTimeFacade();

  /**
   * Builder class for constructing an event series.
   */
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

    /**
     * Sets the event's description.
     *
     * @param description the description.
     * @return EventSeriesBuilder
     */
    public EventSeriesBuilder description(String description) {
      eventBuilder = eventBuilder.description(description);
      return this;
    }

    /**
     * Copies the properties of the provided event.
     * @param event the event to be copied in.
     * @return EventSeriesBuilder
     */
    public EventSeriesBuilder copyEvent(IEvent event) {
      return new EventSeriesBuilder(
          Event.getBuilder().subject(
          event.getSubject())
          .status(event.getStatus())
          .startTime(
            facade.hourOf(event.getStartTime()),
            facade.minuteOf(event.getStartTime())
          )
          .startDate(
            facade.dayOf(event.getStartDate()),
            facade.monthOf(event.getStartDate()),
            facade.YearOf(event.getStartDate())
          )
          .endTime(
            facade.hourOf(event.getEndTime()),
            facade.minuteOf(event.getEndTime())
          )
          .endDate(
            facade.dayOf(event.getEndDate()),
            facade.monthOf(event.getEndDate()),
            facade.YearOf(event.getEndDate())
          )
          .description(event.getDescription())
              , weekDays, seriesEndDate, initialDate);
    }

    /**
     * Sets the event's subject.
     * @param subject the subject.
     * @return EventSeriesBuilder
     */
    public EventSeriesBuilder subject(String subject) {
      eventBuilder = eventBuilder.subject(subject);
      return this;
    }

    /**
     * Sets the event's location.
     * @param location the location.
     * @return EventSeriesBuilder
     */
    public EventSeriesBuilder location(EventLocation location) {
      eventBuilder = eventBuilder.location(location);
      return this;
    }

    /**
     * Sets the events start date.
     *
     * @param day   The day.
     * @param month The month.
     * @param year  The year.
     * @return EventSeriesBuilder
     */
    public EventSeriesBuilder eventStartDate(int day, int month, int year) {

      return new EventSeriesBuilder(
              eventBuilder.startDate(day, month, year), weekDays, seriesEndDate,
              facade.dateOf(day, month, year)
      );
    }

    /**
     * Sets the event's start time.
     * @param hour   The hour.
     * @param minute The minute.
     * @return EventSeriesBuilder
     */
    public EventSeriesBuilder eventStartTime(int hour, int minute) {
      eventBuilder = eventBuilder.startTime(hour, minute);
      return this;
    }

    /**
     * Sets the event's end time.
     *
     * @param hour   The hour.
     * @param minute The minute.
     * @return EventSeriesBuilder
     */
    public EventSeriesBuilder eventEndTime(int hour, int minute) {
      eventBuilder = eventBuilder.endTime(hour, minute);
      return this;
    }

    /**
     * Sets the event's end date.
     *
     * @param day   The day.
     * @param month The month.
     * @param year  The year.
     * @return EventSeriesBuilder
     */
    public EventSeriesBuilder eventEndDate(int day, int month, int year) {
      eventBuilder = eventBuilder.endDate(day, month, year);
      return this;
    }

    /**
     * Sets the event's status.
     *
     * @param status the status.
     * @return EventSeriesBuilder
     */
    public EventSeriesBuilder status(EventStatus status) {
      eventBuilder = eventBuilder.status(status);
      return this;
    }

    /**
     * Sets the event's week days.
     *
     * @param weekDays String containing the character literals representing week days.
     * @return EventSeriesBuilder
     */
    public EventSeriesBuilder weekDays(String weekDays) {
      Set<DayOfWeek> weekDaySet = new HashSet<>();
      for (char weekDay : weekDays.toCharArray()) {
        if (facade.weekDayFrom(weekDay) != null) {
          weekDaySet.add(facade.weekDayFrom(weekDay));
        }
      }
      return new EventSeriesBuilder(eventBuilder, weekDaySet, seriesEndDate, initialDate);
    }

    /**
     * Sets the date that the series will recur until. (inclusive)
     * @param date the date.
     * @return EventSeriesBuilder.
     */
    public EventSeriesBuilder seriesEndDate(LocalDate date) {
      return new EventSeriesBuilder(eventBuilder, weekDays, date, initialDate);
    }

    /**
     * Sets the amount of weeks that an event will recur for.
     * @param weeks The number of weeks.
     * @return EventSeriesBuilder.
     */
    public EventSeriesBuilder seriesEndDateFromWeeks(int weeks) {
      return new EventSeriesBuilder(
              eventBuilder, weekDays, facade.stepDays(initialDate, 7 * weeks), initialDate
      );
    }

    /**
     * Constructs an Event Series.
     * @return EventSeries
     */
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

  /**
   * Gets a builder for an EventSeries.
   * @return EventSeriesBuilder
   */
  public static EventSeriesBuilder getBuilder() {
    return new EventSeriesBuilder();
  }

  /**
   * Takes an existing series and returns a builder which copies its properties.
   * @param series the series to modify.
   * @return EventSeriesBuilder
   */
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

  private EventSeries(Event event, Set<DayOfWeek> weekDays, LocalDate endDate,
                      List<IEvent> newList) {
    this.baseEvent = event;
    this.weekDays = weekDays;
    this.endDate = endDate;
    this.eventSeries = newList;
  }

  // currently ignores adding base event to the list unless it falls on a valid weekday.
  // you can make an event on friday,but if the scheduled days are MW then it will only log those
  // days
  private List<IEvent> constructSeries() {
    List<IEvent> series = new ArrayList<>();
    LocalDate currentDate = baseEvent.getStartDate();
    // inclusive stepping
    while (facade.isAfter(facade.stepDays(endDate, 1), currentDate)) {

      if (weekDays.contains(facade.weekDayOf(currentDate))) {
        //eventSeries.add(baseEvent);
        Event.EventBuilder eventBuilder = Event.getBuilder();

        series.add(
                eventBuilder.subject(baseEvent.getSubject())
                        .location(baseEvent.getLocation())
                        .description(baseEvent.getDescription())
                        .startTime(
                                facade.hourOf(baseEvent.getStartTime()),
                                facade.minuteOf(baseEvent.getStartTime()))
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

  public EventSeries adopt(List<IEvent> newList) {
    return new EventSeries(baseEvent, weekDays, endDate, newList);
  }

  @Override
  public IEventSeries shiftTimeZone(ZoneId from, ZoneId to) {
    IEvent shiftedEvent = baseEvent.shiftTimeZone(from, to);

    int step = (int) facade.daysBetween(baseEvent.getEndDate(), shiftedEvent.getEndDate());

    return EventSeries.editSeries(this)
            .copyEvent(shiftedEvent).seriesEndDate(
                    facade.stepDays(endDate, step)
            ).buildSeries();
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
  public List<IEvent> getEvents() {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o instanceof EventSeries) {
      EventSeries other = (EventSeries) o;
      return this.eventSeries.equals(other.eventSeries);
    }
    return false;
  }

}
