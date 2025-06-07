package controller.command;

import model.Calendar;
import model.Event;
import model.ICalendar;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * JUnit 4 tests for ShowCommand.
 *
 * Includes parsing‐only (dummy model) tests and integration tests with the real Calendar model.
 */
public class ShowCommandTest {

  // -------------- Parsing‐only dummy tests --------------
  private final ICalendar dummyModel = new ICalendar() {
    @Override public void createEvent(Event e) {}
    @Override public void createEventSeries(List<Event> s) {}
    @Override public void editEvent(Event o, Event n) {}
    @Override public List<Event> getScheduleInRange(LocalDate start, LocalDate end) {
      return Collections.emptyList();
    }
  };

  @Test
  public void testValidShowParsing_NoException() {
    new ShowCommand("show status on 2025-06-10T09:00").execute(dummyModel);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMalformedShow_MissingOn() {
    new ShowCommand("show status 2025-06-10T09:00").execute(dummyModel);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMalformedShow_MissingDateTime() {
    new ShowCommand("show status on ").execute(dummyModel);
  }

  // -------------- Integration tests with real Calendar model --------------
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
  public void testShowBusy_Integration() {
    ICalendar cal = new Calendar();
    // add an event on 2025-07-01
    Event ev = Event.getBuilder()
            .subject("Event1")
            .startDate(1, 7, 2025)
            .startTime(8, 0)
            .endDate(1, 7, 2025)
            .endTime(9, 0)
            .buildEvent();
    cal.createEvent(ev);
    outContent.reset(); // clear any model output

    new ShowCommand("show status on 2025-07-01T08:30").execute(cal);
    String output = outContent.toString().trim();
    assertEquals("Busy", output);
  }

  @Test
  public void testShowAvailable_Integration() {
    ICalendar cal = new Calendar();
    // no events added -> should be Available
    new ShowCommand("show status on 2025-07-02T10:00").execute(cal);
    String output = outContent.toString().trim();
    assertEquals("Available", output);
  }

}
