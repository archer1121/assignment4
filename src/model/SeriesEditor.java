package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * An editor for the EventSeries. Supports 3 variant replace() operations.
 */
public class SeriesEditor {
  private static final IDateTimeFacade facade = new DateTimeFacade();
  private EventSeries series;

  /**
   * Constructs a new SeriesEditor.
   * @param eventSeries The Series to be manipulated.
   */
  public SeriesEditor(EventSeries eventSeries) {
    this.series = eventSeries;
  }

  /**
   * Replaces oldEvent with newEvent if possible.
   * @param oldEvent The Event to be replaced.
   * @param newEvent The replacement for oldEvent.
   * @return SeriesEditor
   */
  public SeriesEditor replace(IEvent oldEvent, IEvent newEvent) {
    List<IEvent> newlist = new ArrayList<>(series.getEvents());
    for (int i = 0; i < newlist.size(); i++) {
      if (newlist.get(i).equals(oldEvent)) {
        newlist.set(i, newEvent);
        break;
      }
    }
    series = series.adopt(newlist);
    return this;
  }

  /**
   * Returns the series that this Editor is operating on.
   * @return EventSeries
   */
  public EventSeries getSeries() {
    return this.series;
  }

  /**
   * Replaces all events following (and including) oldEvent with newEvent.
   * @param oldEvent The event denoting when replacement should start.
   * @param newEvent The event which is the replacement.
   * @return seriesEditor
   */
  public SeriesEditor replaceRange(IEvent oldEvent, IEvent newEvent) {
    List<IEvent> beforeActivation = new ArrayList<>();
    boolean activated = false;

    // we need to add original events until we hit the place of oldEvent.
    for (int i = 0; i < series.getEvents().size(); i++) {
      if (series.getEvents().get(i).equals(oldEvent)) {
        activated = true;
        break;
      }
      beforeActivation.add(series.getEvents().get(i));
    }

    if (!activated) {
      throw new IllegalArgumentException("Old event isn't in the series.");
    }

    // we need to create a similar series containing the new events
    List<IEvent> replacements = EventSeries
            .editSeries(series)
            .copyEvent(newEvent)
            .buildSeries()
            .getEvents();

    // we need an identifier for knowing where exactly to splice these two series lists together.
    LocalDate cutoffDate = oldEvent.getStartDate();

    List<IEvent> afterActivation = replacements.stream()
            .filter(e -> !facade.isBefore(e.getStartDate(), cutoffDate))
            .collect(Collectors.toList());


    beforeActivation.addAll(afterActivation);
    series = series.adopt(beforeActivation);
    return this;
  }

  /**
   * Replaces all Events in the series with the newEvent.
   * @param newEvent The replacement event.
   * @return SeriesEditor
   */
  public SeriesEditor replaceAll(IEvent newEvent) {
    series = series.adopt(
            EventSeries.editSeries(series).copyEvent(newEvent).buildSeries().getEvents()
    );
    return this;
  }

  /**
   * Finds the first Event in this series with the specified dates.
   * @param day The day.
   * @param month The month.
   * @param year The year.
   * @return The IEvent found, or null if none.
   */
  public IEvent find(int day, int month, int year) {
    return find(facade.dateOf(day, month, year));
  }


  /**
   * Finds the first Event in this series with the specified dates.
   * @param date the start date of the Event to be found.
   * @return The IEvent found, or null if none.
   */
  public IEvent find(LocalDate date) {
    List<IEvent> list = series.getEvents();
    for (IEvent iEvent : list) {
      if (facade.dateEquals(iEvent.getStartDate(), date)) {
        return iEvent;
      }
    }
    return null;
  }


}
