package controller.command;

import model.ICalendar;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.Assert.*;

/**
 * JUnit 4 tests for ShowCommand. Redirects System.out to capture “Busy”/“Available”.
 * We simulate a fake ICalendar whose getScheduleInRange(date, date) returns
 * a singleton list (→ “Busy”) or empty (→ “Available”).
 */
public class ShowCommandTest {

  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
  private PrintStream originalOut;

  @Before
  public void setUp() {
    originalOut = System.out;
    System.setOut(new PrintStream(outContent));
  }

  @After
  public void tearDown() {
    System.setOut(originalOut);
  }

  @Test
  public void testShowBusy() {
    ICalendar fakeModel = new ICalendar() {
      @Override
      public java.util.List<model.Event> getScheduleInRange(LocalDate start, LocalDate end) {
        // Expect date portion of “2025-06-10T09:00” → 2025-06-10
        assertEquals(LocalDate.parse("2025-06-10"), start);
        assertEquals(LocalDate.parse("2025-06-10"), end);
        // Return non-empty → “Busy”
        return Collections.singletonList((model.Event) null);
      }
      @Override public void createEvent() {}
      @Override public void createEventSeries() {}
      @Override public void editEvent(model.Event event) {}
    };

    String cmd = "show status on 2025-06-10T09:00";
    new ShowCommand(cmd).execute(fakeModel);

    String output = outContent.toString().trim();
    assertEquals("Busy", output);
  }

  @Test
  public void testShowAvailable() {
    ICalendar fakeModel = new ICalendar() {
      @Override
      public java.util.List<model.Event> getScheduleInRange(LocalDate start, LocalDate end) {
        assertEquals(LocalDate.parse("2025-06-10"), start);
        assertEquals(LocalDate.parse("2025-06-10"), end);
        // Return empty → “Available”
        return Collections.emptyList();
      }
      @Override public void createEvent() {}
      @Override public void createEventSeries() {}
      @Override public void editEvent(model.Event event) {}
    };

    String cmd = "show status on 2025-06-10T10:30";
    new ShowCommand(cmd).execute(fakeModel);

    String output = outContent.toString().trim();
    assertEquals("Available", output);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMalformedShowMissingStatus() {
    ICalendar fakeModel = new ICalendar() {
      @Override public java.util.List<model.Event> getScheduleInRange(LocalDate start, LocalDate end) { return null; }
      @Override public void createEvent() {}
      @Override public void createEventSeries() {}
      @Override public void editEvent(model.Event event) {}
    };

    // “show stat on” is incorrect
    String cmd = "show stat on 2025-06-10T09:00";
    new ShowCommand(cmd).execute(fakeModel);
  }
}
