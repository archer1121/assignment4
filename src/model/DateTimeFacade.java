package model;

import java.time.LocalDate;
import java.time.LocalTime;

public class DateTimeFacade implements IDateTimeFacade {

//  public model.DateFacadeImpl() {
//
//  }

  @Override
  public LocalDate dateOf(int day, int month, int year) {
    return LocalDate.of(year, month, day);
  }

  @Override
  public LocalTime timeOf(int hour, int minute) {
    return LocalTime.of(hour, minute);
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
    return date1.equals(date2);
  }
}
