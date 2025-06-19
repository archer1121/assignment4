package view;

import model.ICalendar;
import java.time.LocalDate;

/** Small global used only by the GUI side to keep state in sync. */
public final class CalendarAppState {
  private static final CalendarAppState INSTANCE = new CalendarAppState();
  private ICalendar activeCalendar;
  private LocalDate  cursorDate;

  private CalendarAppState() { }

  public static CalendarAppState get() { return INSTANCE; }

  public void init(ICalendar cal) {
    this.activeCalendar = cal;
    if (cursorDate == null) {
      cursorDate = java.time.LocalDate.now();
    }
  }

  public ICalendar activeCalendar() { return activeCalendar; }
  public LocalDate  cursorDate()    { return cursorDate; }
  public void setCursorDate(LocalDate d) { cursorDate = d; }
}
