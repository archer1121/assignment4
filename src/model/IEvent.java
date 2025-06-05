package model;

import java.time.LocalDate;
import java.time.LocalTime;

public interface IEvent {
  String getSubject();

  LocalDate getStartDate();

  LocalTime getStartTime();

  LocalDate getEndDate();

  LocalTime getEndTime();

  EventLocation getLocation();

  EventStatus getStatus();

  String getDescription();

  boolean isAllDayEvent();
}
