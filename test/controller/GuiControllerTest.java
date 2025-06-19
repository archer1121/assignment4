package controller;

import model.ICalendar;
import model.ICalendarManager;
import model.IEvent;
import model.IEventSeries;
import view.CalendarAppState;
import view.IGuiView;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Unit tests for GuiController that verify logic without using the real Swing GUI.
 * Uses mock objects.
 */
public class GuiControllerTest {

  private MockCalendar mockCalendar;
  private MockView mockView;
  private GuiController controller;

  @Before
  public void setUp() {
    mockCalendar = new MockCalendar();
    mockView     = new MockView();
    MockManager manager = new MockManager(mockCalendar);

    controller = new GuiController(manager, mockView);
  }

  /** createEvent should add exactly one Event and refresh the view. */
  @Test
  public void testJumpToSetsCursorAndRefreshes() {
    LocalDate target = LocalDate.of(2025, 7, 1);
    controller.jumpTo(target);

    assertEquals("cursor moved", target, CalendarAppState.get().cursorDate());
    assertTrue("view.refresh() was called", mockView.refreshed);
  }


  /** jumpTo should update cursorDate (tracked in mockView) and refresh. */


  /* ─────────────────────  Mock helper classes  ───────────────────── */

  /** Minimal CalendarManager that always returns the provided calendar. */
  private static class MockManager implements ICalendarManager {
    private final ICalendar cal;
    MockManager(ICalendar cal) { this.cal = cal; }

    @Override public ICalendar getOrCreateDefault() { return cal; }

    /* Unused interface methods */
    @Override public List<String> getCalendars() { return new ArrayList<>(); }
    @Override public ICalendar getCalendar(String name) { return null; }
    @Override public void changeName(String o, String n) {}
    @Override public void addCalendar(String n, ICalendar c) {}
    @Override public void removeCalendar(String n) {}
  }

  /** Records added events; stubs everything else. */
  private static class MockCalendar implements ICalendar {
    final List<IEvent> addedEvents = new ArrayList<>();

    @Override public void addEvent(IEvent e) { addedEvents.add(e); }

    /* Remaining ICalendar methods stubbed out */
    @Override public void removeEvent(IEvent e) {}
    @Override public void copyEventsAndShift(LocalDate rs, LocalDate re, ICalendar f, LocalDate at) {}
    @Override public void copyEvents(LocalDate rs, LocalDate re, ICalendar f) {}
    @Override public void replaceEvent(IEvent o, IEvent n) {}
    @Override public void addEventSeries(IEventSeries s) {}
    @Override public void removeEventSeries(IEventSeries s) {}
    @Override public void replaceSeries(IEventSeries o, IEventSeries n) {}
    @Override public List<IEvent> getEvents() { return new ArrayList<>(); }
    @Override public List<IEvent> getScheduleInRange(LocalDate s, LocalDate e) { return new ArrayList<>(); }
    @Override public IEventSeries getSeriesFor(IEvent e) { return null; }
    @Override public ZoneId getTimeZone() { return ZoneId.systemDefault(); }
    @Override public ICalendar setTimeZone(ZoneId tz) { return this; }
  }

  /**
   * Tracks whether refresh() is called and what date jumpTo() received.
   * */
  private static class MockView implements IGuiView {
    Features  features;
    boolean   refreshed    = false;
    LocalDate lastJumpedTo = null;

    @Override public void setFeatures(Features f) { this.features = f; }

    @Override public void refresh() {
      refreshed = true;
      lastJumpedTo = CalendarAppState.get().cursorDate();  // pull current cursor
    }
  }
}
