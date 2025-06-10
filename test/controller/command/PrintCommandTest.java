// src/test/java/controller/command/PrintCommandTest.java
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

public class PrintCommandTest {

  // --- a fake view that records takeMessage calls ---
  private static class FakeView implements ITextView {
    final List<String> buf = new ArrayList<>();
    @Override public void takeMessage(String m)       { buf.add(m); }
    @Override public void clearTextBuffer()           { buf.clear(); }
    @Override public void displayTextInBuffer()       { /* not used */ }
    @Override public List<String> getTextInBuffer()   { return List.copyOf(buf); }
  }

  // --- dummy model for parsing-only tests ---
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

  // parsing‐only
  @Test public void testValidPrintOnDateParsing() {
    new PrintCommand("print events on 2025-06-10")
            .execute(new DummyCal(), new FakeView());
  }
  @Test public void testValidPrintFromToParsing() {
    new PrintCommand("print events from 2025-06-01T08:00 to 2025-06-01T09:00")
            .execute(new DummyCal(), new FakeView());
  }
  @Test(expected=IllegalArgumentException.class)
  public void testMalformedPrintMissingOnOrFrom() {
    new PrintCommand("print eventson 2025-06-10")
            .execute(new DummyCal(), new FakeView());
  }
  @Test(expected=IllegalArgumentException.class)
  public void testMalformedPrintMissingDate() {
    new PrintCommand("print events on ")
            .execute(new DummyCal(), new FakeView());
  }
  @Test(expected=IllegalArgumentException.class)
  public void testMalformedPrintMissingTo() {
    new PrintCommand("print events from 2025-06-01T08:00")
            .execute(new DummyCal(), new FakeView());
  }

  // integration w/ real Calendar
  @Test public void testPrintEventsOnDate_TimedOnly() {
    Calendar cal = new Calendar();
    IEvent m = Event.getBuilder()
            .subject("Meeting")
            .startDate(10,6,2025).startTime(9,0)
            .endDate(10,6,2025).endTime(10,0)
            .buildEvent();
    cal.addEvent(m);

    FakeView view = new FakeView();
    new PrintCommand("print events on 2025-06-10")
            .execute(cal, view);

    List<String> out = view.getTextInBuffer();
    assertEquals(1, out.size());
    assertEquals("• Meeting 09:00–10:00", out.get(0));
  }

  @Test public void testPrintEventsOnDate_AllDayWithLocation() {
    Calendar cal = new Calendar();
    IEvent h = Event.getBuilder()
            .subject("Holiday")
            .startDate(25,12,2025).startTime(8,0)
            .endDate(25,12,2025).endTime(17,0)
            .location(EventLocation.ONLINE)
            .buildEvent();
    cal.addEvent(h);

    FakeView view = new FakeView();
    new PrintCommand("print events on 2025-12-25")
            .execute(cal, view);

    List<String> out = view.getTextInBuffer();
    assertEquals(1, out.size());
    assertEquals("• Holiday (All day) @ ONLINE", out.get(0));
  }

  @Test public void testPrintEventsFromTo_Range() {
    Calendar cal = new Calendar();
    IEvent ev1 = Event.getBuilder()
            .subject("Ev1")
            .startDate(1,6,2025).startTime(8,0)
            .endDate(1,6,2025).endTime(9,0)
            .buildEvent();
    IEvent ev2 = Event.getBuilder()
            .subject("Ev2")
            .startDate(1,6,2025).startTime(10,0)
            .endDate(1,6,2025).endTime(11,0)
            .buildEvent();
    IEvent ev3 = Event.getBuilder()
            .subject("Ev3")
            .startDate(2,6,2025).startTime(12,0)
            .endDate(2,6,2025).endTime(13,0)
            .buildEvent();

    cal.addEvent(ev1);
    cal.addEvent(ev2);
    cal.addEvent(ev3);

    FakeView view = new FakeView();
    new PrintCommand("print events from 2025-06-01T00:00 to 2025-06-02T23:59")
            .execute(cal, view);

    List<String> out = view.getTextInBuffer();
    assertEquals(3, out.size());
    assertEquals("• Ev1 08:00–09:00", out.get(0));
    assertEquals("• Ev2 10:00–11:00", out.get(1));
    assertEquals("• Ev3 12:00–13:00", out.get(2));
  }

}
