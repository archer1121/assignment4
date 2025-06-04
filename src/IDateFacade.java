import java.time.LocalDate;
import java.time.LocalTime;

public interface IDateFacade {
  LocalDate dateOf(int day, int month, int year);
  LocalTime timeOf(int hour, int minute);

  Integer YearOf(LocalDate date);
  Integer monthOf(LocalDate date);
  Integer dayOf(LocalDate date);

  Integer hourOf(LocalTime time);
  Integer minuteOf(LocalTime time);
}
