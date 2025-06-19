
package view;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

import model.IEvent;

public interface IGuiView {

  /**
   * Ask the view to refresh its widgets using the current model state.
   */
  void refresh();

  /**
   * Register callbacks that GUI widgets invoke.
   */
  void setFeatures(Features f);

  interface Features {
    void createEvent(String subject,
                     LocalTime startTime, LocalDate startDate,
                     LocalTime endTime, LocalDate endDate);

    void switchToCalendar(String name); // finds or creates one with name

    void createCalendar(String name, ZoneId zone);

    void removeCalendar(String name);

    void showSchedule(LocalDate start, LocalDate end);

    void modifyEvent(
            IEvent event,
            String newSubject,
            LocalTime newStartTime, LocalDate newStartDate,
            LocalTime newEndTime, LocalDate newEndDate);

    void jumpTo(LocalDate startDate);           // schedule view
    // add more  extra-credit features
  }
}
