
package controller.command;

import model.Calendar;

import model.Event;
import model.ICalendar;
import model.IEvent;
import model.IEventSeries;
import view.ITextView;

import org.junit.Test;

import java.time.LocalDate;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Tests for the Show Command to ensure correct functionality.
 */
public class ShowCommandTest {

  // --- parse-only dummy ---
  private static class DummyCal implements ICalendar {
    @Override
    public void addEvent(IEvent e) {
      //command
    }

    @Override
    public void removeEvent(IEvent e) {
      //comment
    }

    @Override
    public void addEventSeries(IEventSeries s) {
      //comment
    }

    public ZoneId getTimeZone() {
      return null;
    }

    @Override
    public ICalendar setTimeZone(ZoneId timeZone) {

      return null;
    }

    @Override
    public void removeEventSeries(IEventSeries series) {
      //comment
    }

    @Override
    public void replaceEvent(IEvent o, IEvent n) {
      //comment
    }

    @Override
    public void copyEventsAndShift(
        LocalDate rangeStart, LocalDate rangeEnd,
        ICalendar from, LocalDate atStartDate) {
      //comment
    }

    @Override
    public void copyEvents(LocalDate rangeStart, LocalDate rangeEnd, ICalendar from) {
      //comment
    }

    @Override
    public List<IEvent> getEvents() {
      return List.of();
    }

    @Override
    public List<IEvent> getScheduleInRange(LocalDate s, LocalDate e) {
      return List.of();
    }

    @Override
    public IEventSeries getSeriesFor(IEvent e) {

      return null;
    }

    @Override
    public void replaceSeries(IEventSeries oldSeries, IEventSeries newSeries) {
      //comment
    }
  }

  private static class DummyView implements ITextView {
    @Override
    public void takeMessage(String m) {
      //comment
    }

    @Override
    public void clearTextBuffer() {
      //comment
    }

    @Override
    public void displayTextInBuffer() {
      //comment
    }

    @Override
    public List<String> getTextInBuffer() {
      return List.of();
    }
  }


  @Test(expected = IllegalArgumentException.class)
  public void testMalformedShowMissingOn() {
    new ShowCommand("show status 2025-06-10T09:00")
            .execute(new DummyCal(), new DummyView());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMalformedShowMissingDateTime() {
    new ShowCommand("show status on ")
            .execute(new DummyCal(), new DummyView());
  }

  // --- integration with real Calendar and fake view ---
  private static class FakeView implements ITextView {
    final List<String> buf = new ArrayList<>();

    @Override
    public void takeMessage(String m) {
      buf.add(m);
    }

    @Override
    public void clearTextBuffer() {
      buf.clear();
    }

    @Override
    public void displayTextInBuffer() {
      //comment
    }

    @Override
    public List<String> getTextInBuffer() {
      return List.copyOf(buf);
    }
  }

  @Test
  public void testShowBusyIntegration() {
    Calendar cal = new Calendar();
    // put an event on 2025-07-01
    IEvent e = Event.getBuilder()
            .subject("Evt")
            .startDate(1, 7, 2025).startTime(8, 0)
            .endDate(1, 7, 2025).endTime(9, 0)
            .buildEvent();
    cal.addEvent(e);

    FakeView view = new FakeView();
    new ShowCommand("show status on 2025-07-01T08:30")
            .execute(cal, view);

    List<String> out = view.getTextInBuffer();
    assertEquals(1, out.size());
    assertEquals("Busy", out.get(0));
  }

  @Test
  public void testShowAvailableIntegration() {
    Calendar cal = new Calendar();
    FakeView view = new FakeView();
    new ShowCommand("show status on 2025-07-02T10:00")
            .execute(cal, view);

    List<String> out = view.getTextInBuffer();
    assertEquals(1, out.size());
    assertEquals("Available", out.get(0));
  }
}
