import java.time.LocalDate;
import java.time.LocalTime;

public class DateFacadeImpl implements IDateFacade {

//  public DateFacadeImpl() {
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
}
