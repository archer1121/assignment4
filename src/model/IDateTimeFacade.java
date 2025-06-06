package model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface IDateTimeFacade {
  LocalDate dateOf(int day, int month, int year);
  LocalTime timeOf(int hour, int minute);

  LocalDate stepDays(LocalDate date, int amount);

  LocalTime stepHours(LocalTime time, int amount);

  LocalTime stepMinutes(LocalTime time, int amount);

  Integer YearOf(LocalDate date);
  Integer monthOf(LocalDate date);
  Integer dayOf(LocalDate date);

  Integer hourOf(LocalTime time);
  Integer minuteOf(LocalTime time);

  boolean isAfter(LocalTime time1, LocalTime time2);

  boolean isAfter(LocalDate date1, LocalDate date2);

  boolean timeEquals(LocalTime time1, LocalTime time2);

  boolean dateEquals(LocalDate date1, LocalDate date2);

  DayOfWeek weekDayFrom(char literal);

  DayOfWeek weekDayOf(LocalDate date);
  Character weekDaySymbolOf(DayOfWeek day);

  String weeksAsString(List<DayOfWeek> weekDays);

  boolean weekDayIs(LocalDate date, DayOfWeek weekDay);



  DayOfWeek sunday();

  DayOfWeek monday();

  DayOfWeek tuesday();

  DayOfWeek wednesday();

  DayOfWeek thursday();

  DayOfWeek friday();

  DayOfWeek saturday();


}
