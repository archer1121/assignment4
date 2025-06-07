package controller.command;

import model.Event;
import model.ICalendar;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * JUnit 4 tests for PrintCommand. Valid “print events on …” and “print events from … to …”
 * should not throw; malformed strings should throw IllegalArgumentException.
 */
public class PrintCommandTest {

  private final ICalendar dummyModel = new ICalendar() {

    @Override
    public void createEvent(Event event) {

    }

    @Override
    public void createEventSeries(List<Event> series) {

    }

    @Override public void editEvent(model.Event event) { }
    @Override public java.util.List<model.Event> getScheduleInRange(java.time.LocalDate start, java.time.LocalDate end) { return null; }
  };

  @Test
  public void testValidPrintOnDate() {
    String cmd = "print events on 2025-06-10";
    new PrintCommand(cmd).execute(dummyModel);
  }

  @Test
  public void testValidPrintFromTo() {
    String cmd = "print events from 2025-06-01T08:00 to 2025-06-01T09:00";
    new PrintCommand(cmd).execute(dummyModel);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMalformedPrintMissingOnOrFrom() {
    // Neither “on” nor “from … to …”
    String cmd = "print eventson 2025-06-10";
    new PrintCommand(cmd).execute(dummyModel);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMalformedPrintMissingDate() {
    // “on” but no date after it
    String cmd = "print events on ";
    new PrintCommand(cmd).execute(dummyModel);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMalformedPrintMissingTo() {
    // “from” present but no “to”
    String cmd = "print events from 2025-06-01T08:00";
    new PrintCommand(cmd).execute(dummyModel);
  }
}
