package model;

import java.time.LocalDate;
import java.time.LocalTime;

public interface IDateTimeFacade {
  LocalDate dateOf(int day, int month, int year);
  LocalTime timeOf(int hour, int minute);

  Integer YearOf(LocalDate date);
  Integer monthOf(LocalDate date);
  Integer dayOf(LocalDate date);

  Integer hourOf(LocalTime time);
  Integer minuteOf(LocalTime time);

  boolean isAfter(LocalTime time1, LocalTime time2);

  boolean isAfter(LocalDate date1, LocalDate date2);

  boolean timeEquals(LocalTime time1, LocalTime time2);

  boolean dateEquals(LocalDate date1, LocalDate date2);
}
