
package view;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

import model.IEvent;

/**
 * A view interface designed for GUI's of the calendar app. This interface contains GUI
 * specific methods and an internal Features class which is to be used in conjunction with this
 * view.
 */
public interface IGuiView {

  /**
   * Ask the view to refresh its widgets using the current model state.
   */
  void refresh();

  /**
   * Register callbacks that GUI widgets invoke.
   */
  void setFeatures(Features f);

  /**
   * A Set of methods ideal for controllers using this view to have. These are the expected
   * functionalities of the controller.
   */
  interface Features {
    /**
     * Creates and stores an event.
     * @param subject The subject of the event.
     * @param startTime The start time of the event
     * @param startDate The start date of the event.
     * @param endTime The end time of the event
     * @param endDate The end date of the event.
     */
    void createEvent(String subject,
                     LocalTime startTime, LocalDate startDate,
                     LocalTime endTime, LocalDate endDate);

    /**
     * Switches the calendar to whichever is identified by 'name'.
     * @param name The name of the calendar to switch to.
     */
    void switchToCalendar(String name); // finds or creates one with name

    /**
     * Creates a calendar which will be identified by the provided name.
     * @param name the name of the calendar.
     * @param zone the timezone of the calendar.
     */
    void createCalendar(String name, ZoneId zone);

    /**
     * Removes the calendar specified by the given name.
     * @param name the name of the calendar to be removed.
     */
    void removeCalendar(String name);

    /**
     * Modifies the given event with the given properties.
     * @param event the event to modify.
     * @param newSubject the new subject of the event.
     * @param newStartTime the new start time of the event.
     * @param newStartDate the new start date of the event.
     * @param newEndTime   the new end time of the event.
     * @param newEndDate   the new end date of the event.
     */
    void modifyEvent(
            IEvent event,
            String newSubject,
            LocalTime newStartTime, LocalDate newStartDate,
            LocalTime newEndTime, LocalDate newEndDate);

    /**
     * Jumps the cursor to the given date.
     * @param startDate the date to jump to.
     */
    void jumpTo(LocalDate startDate);
  }
}
