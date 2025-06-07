package controller.command;

import model.Calendar;
import model.Event;
import model.ICalendar;
import model.EventLocation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests for PrintCommand using the real Calendar model.
 */
public class PrintCommandTest {

  // ---------- Dummy parsing-only tests (no real model) ----------
  private final ICalendar dummyModel = new ICalendar() {
    @Override
    public void createEvent(Event event) {}
    @Override
    public void createEventSeries(java.util.List<Event> series) {}
    @Override
    public void editEvent(Event oldEvent, Event newEvent) {}
    @Override
    public java.util.List<Event> getScheduleInRange(java.time.LocalDate start,
                                                    java.time.LocalDate end) {
      // Return empty list so parsing-only tests don't hit NPE
      return java.util.Collections.emptyList();
    }
  };

  @Test
  public void testValidPrintOnDateParsing() {
    new PrintCommand("print events on 2025-06-10").execute(dummyModel);
  }

  @Test
  public void testValidPrintFromToParsing() {
    new PrintCommand("print events from 2025-06-01T08:00 to 2025-06-01T09:00").execute(dummyModel);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMalformedPrintMissingOnOrFromParsing() {
    new PrintCommand("print eventson 2025-06-10").execute(dummyModel);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMalformedPrintMissingDateParsing() {
    new PrintCommand("print events on ").execute(dummyModel);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMalformedPrintMissingToParsing() {
    new PrintCommand("print events from 2025-06-01T08:00").execute(dummyModel);
  }

  // ---------- Integration tests with real Calendar model ----------
  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
  private PrintStream originalOut;

  @Before
  public void setUpStreams() {
    originalOut = System.out;
    System.setOut(new PrintStream(outContent));
  }

  @After
  public void restoreStreams() {
    System.setOut(originalOut);
  }

  @Test
  public void testPrintEventsOnDate_TimedOnly() {
    ICalendar cal = new Calendar();
    Event meeting = Event.getBuilder()
            .subject("Meeting")
            .startDate(10, 6, 2025)
            .startTime(9, 0)
            .endDate(10, 6, 2025)
            .endTime(10, 0)
            .buildEvent();
    cal.createEvent(meeting);
    // clear model's own output
    outContent.reset();

    new PrintCommand("print events on 2025-06-10").execute(cal);

    String[] lines = outContent.toString().trim().split("\r?\n");
    assertEquals(1, lines.length);
    assertEquals("• Meeting 09:00–10:00", lines[0]);
  }

  @Test
  public void testPrintEventsOnDate_AllDayWithLocation() {
    ICalendar cal = new Calendar();
    Event holiday = Event.getBuilder()
            .subject("Holiday")
            .startDate(25, 12, 2025)
            .startTime(8, 0)
            .endDate(25, 12, 2025)
            .endTime(17, 0)
            .location(EventLocation.ONLINE)
            .buildEvent();
    cal.createEvent(holiday);
    // clear model's own output
    outContent.reset();

    new PrintCommand("print events on 2025-12-25").execute(cal);

    String[] lines = outContent.toString().trim().split("\r?\n");
    assertEquals(1, lines.length);
    assertEquals("• Holiday (All day) @ ONLINE", lines[0]);
  }

  @Test
  public void testPrintEventsFromTo_Range() {
    ICalendar cal = new Calendar();
    Event ev1 = Event.getBuilder()
            .subject("Ev1")
            .startDate(1, 6, 2025)
            .startTime(8, 0)
            .endDate(1, 6, 2025)
            .endTime(9, 0)
            .buildEvent();
    Event ev2 = Event.getBuilder()
            .subject("Ev2")
            .startDate(1, 6, 2025)
            .startTime(10, 0)
            .endDate(1, 6, 2025)
            .endTime(11, 0)
            .buildEvent();
    Event ev3 = Event.getBuilder()
            .subject("Ev3")
            .startDate(2, 6, 2025)
            .startTime(12, 0)
            .endDate(2, 6, 2025)
            .endTime(13, 0)
            .buildEvent();
    cal.createEvent(ev1);
    cal.createEvent(ev2);
    cal.createEvent(ev3);
    // clear model's own output
    outContent.reset();

    new PrintCommand("print events from 2025-06-01T00:00 to 2025-06-02T23:59").execute(cal);

    String[] lines = outContent.toString().trim().split("\r?\n");
    assertEquals(3, lines.length);
    assertEquals("• Ev1 08:00–09:00", lines[0]);
    assertEquals("• Ev2 10:00–11:00", lines[1]);
    assertEquals("• Ev3 12:00–13:00", lines[2]);
  }
}
