package controller;

import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.TimeZone;

import model.Calendar;
import model.Event;
import model.ICalendar;
import model.ICalendarManager;
import model.IEvent;
import view.CalendarAppState;
import view.GuiMessages;
import view.IGuiView;

public class GuiController implements IGuiView.Features {
  private final ICalendarManager mgr;
  private ICalendar current;
  private final IGuiView view;
  private LocalDate cursor = LocalDate.now();   // schedule start date

  public GuiController(ICalendarManager mgr, IGuiView view) {
    this.mgr = mgr;
    this.view = view;
    this.current = mgr.getOrCreateDefault();   // default calendar

    // --- initialise global GUI state *before* any view code runs ---
    CalendarAppState.get().init(current);      // sets activeCalendar & cursorDate

    view.setFeatures(this);
    view.refresh();                            // safe now
  }

  // ----- Features -----
  @Override
  public void createEvent(String subject,
                          LocalTime startTime, LocalDate startDate,
                          LocalTime endTime, LocalDate endDate) {

    LocalTime defaultStartTime = defaultStartTimeIfNull(startTime);
    LocalTime defaultEndTime = defaultEndTimeIfNull(endTime);

    try {
      Event newEv = Event.getBuilder()
              .subject(subject)
              .startDate(
                      startDate.getDayOfMonth(),
                      startDate.getMonthValue(),
                      startDate.getYear())
              .startTime(
                      defaultStartTime.getHour(),
                      defaultStartTime.getMinute())
              .endDate(
                      endDate.getDayOfMonth(),
                      endDate.getMonthValue(),
                      endDate.getYear())
              .endTime(
                      defaultEndTime.getHour(),
                      defaultEndTime.getMinute()
              )
              .buildEvent();
      current.addEvent(newEv);
      // use your existing Event impl
      view.refresh();
    } catch (IllegalArgumentException ex) {
      GuiMessages.error((Component) view, ex.getMessage());
    }
  }

  @Override
  public void switchToCalendar(String name) {
    ICalendar to = mgr.getCalendar(name);

    // get or make default with name and user tz if empty. throw if calendars exist
    if (to == null) {
      if (mgr.getCalendars().isEmpty()) {
        mgr.addCalendar(name, new Calendar(ZoneId.systemDefault()));
      } else {
        GuiMessages.error((Component) view, "Calendar" + name + " not found");
      }
    } else {
      current = to;
    }
  }

  @Override
  public void createCalendar(String name, ZoneId zone) {
    try {
      mgr.addCalendar(name, new Calendar(zone));
    } catch (IllegalArgumentException e) {
      GuiMessages.error((Component) view, e.getMessage());
    }
  }

  @Override
  public void removeCalendar(String name) {
    try {
      mgr.removeCalendar(name);
    }
    catch (IllegalArgumentException e) {
      GuiMessages.error((Component) view, e.getMessage());
    }
  }

  @Override
  public void showSchedule(LocalDate start, LocalDate end) {
    // if baked into view, it may be unnecessary
  }

  @Override
  public void modifyEvent(
          IEvent event,
          String newSubject,
          LocalTime newStartTime, LocalDate newStartDate,
          LocalTime newEndTime, LocalDate newEndDate) {
    try {
      LocalTime defaultStartTime = defaultStartTimeIfNull(newStartTime);
      LocalTime defaultEndTime = defaultEndTimeIfNull(newEndTime);

      IEvent modified = Event.editEvent((Event) event)

              /* very verbose, I should've handled it when implementing */
              .subject(newSubject)
              .startTime(
                      defaultStartTime.getHour(),
                      defaultStartTime.getMinute()
              )
              .startDate(
                      newStartDate.getDayOfMonth(),
                      newStartDate.getMonthValue(),
                      newStartDate.getYear()
              )
              .endTime(
                      defaultEndTime.getHour(),
                      defaultEndTime.getMinute()
              )
              .endDate(
                      newEndDate.getDayOfMonth(),
                      newEndDate.getMonthValue(),
                      newEndDate.getYear()
              )
              .buildEvent();

      current.replaceEvent(event, modified);

    } catch (IllegalArgumentException e) {
      GuiMessages.error((Component) view, e.getMessage());
    }
  }

  @Override
  public void jumpTo(LocalDate start) {
    cursor = start;
    CalendarAppState.get().setCursorDate(start);
    view.refresh();
  }

  private LocalTime defaultEndTimeIfNull(LocalTime endTime) {
    if (endTime != null) {
     return  endTime;
    } else {
      return LocalTime.of(17, 0);
    }
  }

  private LocalTime defaultStartTimeIfNull(LocalTime startTime) {
    if (startTime != null) {
      return  startTime;
    } else {
      return LocalTime.of(8, 0);
    }
  }
}
