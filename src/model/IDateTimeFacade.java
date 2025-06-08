package model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * A local facade Interface to reduce the coupling between our application and Java time classes.
 */
public interface IDateTimeFacade {
  /**
   * Returns a LocalDate with the specified parameters.
   *
   * @param day   The day.
   * @param month The month.
   * @param year  The year.
   * @return LocalDate
   */
  LocalDate dateOf(int day, int month, int year);

  /**
   * Returns a LocalTime with the specified parameters.
   *
   * @param hour   The hour.
   * @param minute The Minute.
   * @return LocalTime
   */
  LocalTime timeOf(int hour, int minute);

  /**
   * Returns the date passed in, incremented by the amount of days specified.
   *
   * @param date   the initial date.
   * @param amount the days which to step.
   * @return LocalDate
   */
  LocalDate stepDays(LocalDate date, int amount);

  /**
   * Returns the time passed in, incremented by the amount of hours specified.
   *
   * @param time   The initial time.
   * @param amount the amount of hours to step.
   * @return LocalTime
   */
  LocalTime stepHours(LocalTime time, int amount);

  /**
   * Returns the time passed in, incremented by the amount of minutes specified.
   *
   * @param time   the initial time.
   * @param amount the amount of minutes to step.
   * @return LocalTime
   */
  LocalTime stepMinutes(LocalTime time, int amount);

  /**
   * Returns the year of the passed in Date.
   *
   * @param date the date.
   * @return year in the form of an Integer.
   */
  Integer YearOf(LocalDate date);

  /**
   * Returns the month of the passed in Date.
   *
   * @param date the date.
   * @return month in the form of an Integer.
   */
  Integer monthOf(LocalDate date);

  /**
   * Returns the day of the passed in Date.
   *
   * @param date the date.
   * @return day in the form of an Integer.
   */
  Integer dayOf(LocalDate date);

  /**
   * Returns the hour of the passed in Time.
   *
   * @param time the time.
   * @return time in the form of an Integer.
   */
  Integer hourOf(LocalTime time);

  /**
   * Returns the minute of the passed in Time.
   *
   * @param time the time.
   * @return minute in the form of an Integer.
   */
  Integer minuteOf(LocalTime time);

  /**
   * Returns whether time1 is after time2.
   *
   * @param time1 first time to be compared.
   * @param time2 second time to be compared.
   * @return true if time1 is after time2.
   */

  boolean isAfter(LocalTime time1, LocalTime time2);

  /**
   * Returns whether date1 is after date2.
   *
   * @param date1 first date to be compared.
   * @param date2 second date to be compared.
   * @return true if date1 is after date2.
   */
  boolean isAfter(LocalDate date1, LocalDate date2);

  /**
   * Returns whether time1 is equal to time2.
   *
   * @param time1 first time to be compared.
   * @param time2 second time to be compared.
   * @return true if time1 is equal to time2.
   */
  boolean timeEquals(LocalTime time1, LocalTime time2);

  /**
   * Returns whether date1 is equal to date2.
   *
   * @param date1 first date to be compared.
   * @param date2 second date to be compared.
   * @return true if date1 is equal to date2.
   */
  boolean dateEquals(LocalDate date1, LocalDate date2);

  /**
   * Returns a weekDay from a character literal.
   *
   * @param literal character literal representing a week day.
   * @return DayOfWeek
   */
  DayOfWeek weekDayFrom(char literal);

  /**
   * Return a weekDay from a LocalDate.
   *
   * @param date the date to be queried.
   * @return DayOfWeek
   */
  DayOfWeek weekDayOf(LocalDate date);

  /**
   * Return a character literal from a DayOfWeek.
   *
   * @param day the character representation of a week aay.
   * @return DayOfWeek
   */
  Character weekDaySymbolOf(DayOfWeek day);

  /**
   * Takes a list of week days and turns them into a string of literals.
   *
   * @param weekDays the list of DayOfWeek.
   * @return weeks formatted as a string.
   */
  String weeksAsString(List<DayOfWeek> weekDays);

  /**
   * Returns whether the week day of the given date is equal to the given weekday.
   *
   * @param date    the date.
   * @param weekDay the target day.
   * @return true if the week day of date equals the weekday.
   */
  boolean weekDayIs(LocalDate date, DayOfWeek weekDay);


  /**
   * Sunday.
   *
   * @return Day of week sunday.
   */
  DayOfWeek sunday();

  /**
   * Monday.
   *
   * @return Day of week Monday.
   */
  DayOfWeek monday();

  /**
   * Tuesday.
   *
   * @return Day of week Tuesday.
   */
  DayOfWeek tuesday();

  /**
   * Wednesday.
   *
   * @return Day of week Wednesday.
   */
  DayOfWeek wednesday();

  /**
   * Thursday.
   *
   * @return Day of week Thursday.
   */
  DayOfWeek thursday();

  /**
   * Friday.
   *
   * @return Day of week Friday.
   */
  DayOfWeek friday();

  /**
   * Saturday.
   *
   * @return Day of week Saturday.
   */
  DayOfWeek saturday();


}
