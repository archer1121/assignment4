package controller;

import java.awt.*;
import java.time.LocalDate;

import model.Event;
import model.ICalendar;
import model.ICalendarManager;
import view.CalendarAppState;
import view.GuiMessages;
import view.IGuiView;

public class GuiController implements IGuiView.Features {
  private final ICalendarManager mgr;
  private ICalendar current;
  private final IGuiView view;
  private LocalDate cursor = LocalDate.now();   // schedule start date

  public GuiController(ICalendarManager mgr, IGuiView view) {
    this.mgr     = mgr;
    this.view    = view;
    this.current = mgr.getOrCreateDefault();   // default calendar

    // --- initialise global GUI state *before* any view code runs ---
    CalendarAppState.get().init(current);      // sets activeCalendar & cursorDate

    view.setFeatures(this);
    view.refresh();                            // safe now
  }

  // ----- Features -----
  @Override
  public void createEvent(String subj, LocalDate s, LocalDate e) {
    try {
      Event newEv = Event.getBuilder()
              .subject(subj)
              .startDate(s.getDayOfMonth(), s.getMonthValue(), s.getYear())
              .startTime(8, 0)
              .endDate(e.getDayOfMonth(), e.getMonthValue(), e.getYear())
              .endTime(17, 0)
              .buildEvent();
      current.addEvent(newEv);
      // use your existing Event impl
      view.refresh();
    } catch (IllegalArgumentException ex) {
      GuiMessages.error((Component) view, ex.getMessage());
    }
  }

  @Override
  public void jumpTo(LocalDate start) {
    cursor = start;
    CalendarAppState.get().setCursorDate(start);
    view.refresh();
  }
}
