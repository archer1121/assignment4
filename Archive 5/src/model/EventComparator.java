package model;

import java.util.Comparator;

public class EventComparator implements Comparator<IEvent> {
  @Override
  public int compare(IEvent o1, IEvent o2) {
    return o1.getStartDate().compareTo(o2.getStartDate());
  }
}
