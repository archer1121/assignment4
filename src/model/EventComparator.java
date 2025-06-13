package model;

import java.util.Comparator;

/**
 * A comparator for IEvents which orders based on start dates.
 */
public class EventComparator implements Comparator<IEvent> {
  @Override
  public int compare(IEvent o1, IEvent o2) {
    return o1.getStartDate().compareTo(o2.getStartDate());
  }
}
