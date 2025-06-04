import java.time.LocalDate;
import java.util.List;

public interface Calendar {

  void createEvent();

  void createEventSeries();

  void editEvent(Event event);

  List<Event> getScheduleInRange(LocalDate start, LocalDate end);



}
