package view;

import model.ICalendar;
import model.ICalendarManager;
import java.time.LocalDate;

/**
 * Global singleton to store GUI state: active calendar, manager, cursor date, and feature callbacks.
 */
public final class CalendarAppState {
  private static final CalendarAppState INSTANCE = new CalendarAppState();
  private ICalendarManager mgr;
  private ICalendar activeCalendar;
  private String currentCalName;
  private LocalDate cursorDate;
  private IGuiView.Features features;

  private CalendarAppState() { }

  public static CalendarAppState get() { return INSTANCE; }

  /** Initial setup called once by GuiController during startup. */
  public void init(ICalendarManager manager, String defaultName, ICalendar cal) {
    this.mgr = manager;
    this.currentCalName = defaultName;
    this.activeCalendar = cal;
    if (cursorDate == null) {
      cursorDate = LocalDate.now();
    }
  }

  public ICalendarManager mgr() { return mgr; }
  public ICalendar activeCalendar() { return activeCalendar; }
  public LocalDate cursorDate() { return cursorDate; }
  public String currentCalendarName() { return currentCalName; }

  public void setCursorDate(LocalDate d) { cursorDate = d; }
  public void setActiveCalendar(ICalendar cal, String name) {
    this.activeCalendar = cal;
    this.currentCalName = name;
  }
  public void setFeatures(IGuiView.Features f) { this.features = f; }
  public IGuiView.Features features() { return features; }
}
