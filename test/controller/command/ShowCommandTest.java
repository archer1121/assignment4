// src/test/java/controller/command/ShowCommandTest.java
package controller.command;

import model.*;
import model.Calendar;
import view.ITextView;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;

import static org.junit.Assert.*;

public class ShowCommandTest {

  // --- parse-only dummy ---
  private static class DummyCal implements ICalendar {
    @Override public void addEvent(IEvent e)            { }
    @Override public void removeEvent(IEvent e)         { }
    @Override public void addEventSeries(IEventSeries s){ }
    public ZoneId getTimeZone() {
      return null;
    }

    @Override
    public void removeEventSeries(IEventSeries series) {

    }

    @Override public void replaceEvent(IEvent o, IEvent n){ }
    @Override public List<IEvent> getEvents()           { return List.of(); }
    @Override public List<IEvent> getScheduleInRange(LocalDate s, LocalDate e){
      return List.of();
    }

    @Override
    public IEventSeries getSeriesFor(IEvent e) {
      return null;
    }

    @Override
    public void replaceSeries(IEventSeries oldSeries, IEventSeries newSeries) {

    }
  }
  private static class DummyView implements ITextView {
    @Override public void takeMessage(String m)      { }
    @Override public void clearTextBuffer()          { }
    @Override public void displayTextInBuffer()      { }
    @Override public List<String> getTextInBuffer()  { return List.of(); }
  }

  @Test public void testValidShowParsing() {
    new ShowCommand("show status on 2025-06-10T09:00")
            .execute(new DummyCal(), new DummyView());
  }
  @Test(expected=IllegalArgumentException.class)
  public void testMalformedShowMissingOn() {
    new ShowCommand("show status 2025-06-10T09:00")
            .execute(new DummyCal(), new DummyView());
  }
  @Test(expected=IllegalArgumentException.class)
  public void testMalformedShowMissingDateTime() {
    new ShowCommand("show status on ")
            .execute(new DummyCal(), new DummyView());
  }

  // --- integration with real Calendar and fake view ---
  private static class FakeView implements ITextView {
    final List<String> buf = new ArrayList<>();
    @Override public void takeMessage(String m)      { buf.add(m); }
    @Override public void clearTextBuffer()          { buf.clear(); }
    @Override public void displayTextInBuffer()      { }
    @Override public List<String> getTextInBuffer()  { return List.copyOf(buf); }
  }

  @Test public void testShowBusyIntegration() {
    Calendar cal = new Calendar();
    // put an event on 2025-07-01
    IEvent e = Event.getBuilder()
            .subject("Evt")
            .startDate(1,7,2025).startTime(8,0)
            .endDate(1,7,2025).endTime(9,0)
            .buildEvent();
    cal.addEvent(e);

    FakeView view = new FakeView();
    new ShowCommand("show status on 2025-07-01T08:30")
            .execute(cal, view);

    List<String> out = view.getTextInBuffer();
    assertEquals(1, out.size());
    assertEquals("Busy", out.get(0));
  }

  @Test public void testShowAvailableIntegration() {
    Calendar cal = new Calendar();
    FakeView view = new FakeView();
    new ShowCommand("show status on 2025-07-02T10:00")
            .execute(cal, view);

    List<String> out = view.getTextInBuffer();
    assertEquals(1, out.size());
    assertEquals("Available", out.get(0));
  }
}
