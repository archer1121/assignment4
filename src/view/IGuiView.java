
package view;

import java.time.LocalDate;
import java.util.List;

import model.ICalendar;
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
                     LocalDate startDate, LocalDate endDate);

    void switchToCalendar(String name);

    void createCalendar(String name);

    void showSchedule(LocalDate start, LocalDate end);

    void modifyEvent(IEvent event, String subject, LocalDate startDate, LocalDate endDate);

    void jumpTo(LocalDate startDate);           // schedule view
    // add more  extra-credit features
  }
}
