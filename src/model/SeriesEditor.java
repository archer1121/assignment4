package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


// class is scrappy but it works for the most part
public class SeriesEditor {
  private static final IDateTimeFacade facade = new DateTimeFacade();
  private EventSeries series;

  public SeriesEditor(EventSeries eventSeries) {
    this.series = eventSeries;
  }


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

  public EventSeries getSeries() {
    return this.series;
  }

  // inclusive of the starts and ends
  public SeriesEditor replaceRange(IEvent oldEvent, IEvent newEvent) {
    List<IEvent> beforeActivation = new ArrayList<>();
    List<IEvent> afterActivation = new ArrayList<>();
    boolean activated = false;
    for (int i = 0; i < series.getEvents().size(); i++) {
      if (series.getEvents().get(i).equals(oldEvent)) {
        afterActivation.addAll(
                EventSeries.editSeries(series)
                        .copyEvent(newEvent)
                        .buildSeries()
                        .getEvents().subList(i, series.getEvents().size())
        );
        activated = true;
      } else if (!activated) {
        beforeActivation.add(series.getEvents().get(i));
      }
    }
    beforeActivation.addAll(afterActivation);
    series = series.adopt(beforeActivation);
    return this;
  }

  public SeriesEditor replaceAll(IEvent newEvent) {
    series = series.adopt(
            EventSeries.editSeries(series).copyEvent(newEvent).buildSeries().getEvents()
    );
    return this;
  }

  public IEvent find(int day, int month, int year) {
    return find(facade.dateOf(day, month, year));
  }

  // finds an event with this date as its start date
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
