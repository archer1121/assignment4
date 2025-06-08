package model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * An Implementation of IDateTimeFacade. All necessary Date/Time operations for this application
 * are called through this class to reduce direct coupling with javas Date/Time classes.
 */
public class DateTimeFacade implements IDateTimeFacade {

  @Override
  public LocalDate dateOf(int day, int month, int year) {
    return LocalDate.of(year, month, day);
  }

  @Override
  public LocalTime timeOf(int hour, int minute) {
    return LocalTime.of(hour, minute);
  }

  @Override
  public LocalDate stepDays(LocalDate date, int amount) {
    return date.plusDays(amount);
  }

  @Override
  public LocalTime stepHours(LocalTime time, int amount) {
    return time.plusHours(amount);
  }

  @Override
  public LocalTime stepMinutes(LocalTime time, int amount) {
    return time.plusMinutes(amount);
  }

  @Override
  public String weeksAsString(List<DayOfWeek> weekDays) {
    StringBuilder sb = new StringBuilder();
    for (DayOfWeek d : weekDays) {
      sb.append(weekDaySymbolOf(d));
    }
    return sb.toString();
  }

  @Override
  public Integer YearOf(LocalDate date) {
    return date.getYear();
  }

  @Override
  public Integer monthOf(LocalDate date) {
    return date.getMonthValue();
  }

  @Override
  public Integer dayOf(LocalDate date) {
    return date.getDayOfMonth();
  }

  @Override
  public boolean weekDayIs(LocalDate date, DayOfWeek weekDay) {
    return date.getDayOfWeek().equals(weekDay);
  }

  @Override
  public DayOfWeek weekDayOf(LocalDate date) {
    return date.getDayOfWeek();
  }

  @Override
  public Character weekDaySymbolOf(DayOfWeek day) {
    switch (day) {
      case MONDAY:
        return 'M';
      case TUESDAY:
        return 'T';
      case WEDNESDAY:
        return 'W';
      case THURSDAY:
        return 'R';
      case FRIDAY:
        return 'F';
      case SATURDAY:
        return 'S';
      case SUNDAY:
        return 'U';
    }
    return null;
  }

  @Override
  public DayOfWeek weekDayFrom(char literal) {
    switch (literal) {
      case 'M':
        return DayOfWeek.MONDAY;
      case 'T':
        return DayOfWeek.TUESDAY;
      case 'W':
        return DayOfWeek.WEDNESDAY;
      case 'R':
        return DayOfWeek.THURSDAY;
      case 'F':
        return DayOfWeek.FRIDAY;
      case 'S':
        return DayOfWeek.SATURDAY;
      case 'U':
        return DayOfWeek.SUNDAY;
    }
    return null;
  }

  @Override
  public Integer hourOf(LocalTime time) {
    return time.getHour();
  }

  @Override
  public Integer minuteOf(LocalTime time) {
    return time.getMinute();
  }

  @Override
  public boolean isAfter(LocalTime time1, LocalTime time2) {
    return time1.isAfter(time2);
  }

  @Override
  public boolean isAfter(LocalDate date1, LocalDate date2) {
    return date1.isAfter(date2);
  }

  @Override
  public boolean timeEquals(LocalTime time1, LocalTime time2) {
    return time1.equals(time2);
  }

  @Override
  public boolean dateEquals(LocalDate date1, LocalDate date2) {
    return date1.isEqual(date2);
  }

  @Override
  public DayOfWeek sunday() {
    return DayOfWeek.SUNDAY;
  }

  @Override
  public DayOfWeek monday() {
    return DayOfWeek.MONDAY;
  }

  @Override
  public DayOfWeek tuesday() {
    return DayOfWeek.TUESDAY;
  }

  @Override
  public DayOfWeek wednesday() {
    return DayOfWeek.WEDNESDAY;
  }

  @Override
  public DayOfWeek thursday() {
    return DayOfWeek.THURSDAY;
  }

  @Override
  public DayOfWeek friday() {
    return DayOfWeek.FRIDAY;
  }

  @Override
  public DayOfWeek saturday() {
    return DayOfWeek.SATURDAY;
  }
}
