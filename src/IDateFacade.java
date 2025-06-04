import java.time.LocalDate;
import java.time.LocalTime;

public interface IDateFacade {
  LocalDate getDate(int day, int month, int year);
  LocalTime getTime(int hour, int minute);

  Integer YearOf(LocalDate date);
  Integer monthOf(LocalDate date);
  Integer dayOf(LocalDate date);

  Integer hourOf(LocalTime time);
  Integer MinuteOf(LocalTime time);


}
