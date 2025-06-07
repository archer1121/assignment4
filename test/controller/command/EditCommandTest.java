package controller.command;

import model.Calendar;
import model.Event;
import model.EventLocation;
import model.EventStatus;
import model.ICalendar;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Unit tests for EditCommand, updated to match EventStatus.{PUBLIC, PRIVATE}.
 */
public class EditCommandTest {

  private ICalendar cal;

  @Before
  public void setUp() {
    cal = new Calendar();
  }

  @Test
  public void testEditSubject() {
    Event original = Event.getBuilder()
            .subject("Lunch")
            .startDate(15, 6, 2025)
            .startTime(12, 0)
            .endDate(15, 6, 2025)
            .endTime(13, 0)
            .buildEvent();
    cal.createEvent(original);

    String cmd = "edit event subject Lunch from 2025-06-15T12:00 to 2025-06-15T13:00 with Brunch";
    new EditCommand(cmd).execute(cal);

    List<Event> results = cal.getScheduleInRange(
            LocalDate.of(2025, 6, 15),
            LocalDate.of(2025, 6, 15)
    );
    assertEquals(1, results.size());
    Event edited = results.get(0);

    assertEquals("Brunch", edited.getSubject());
    assertEquals(LocalTime.of(12, 0), edited.getStartTime());
    assertEquals(LocalTime.of(13, 0), edited.getEndTime());
  }

  @Test
  public void testEditStartTime() {
    Event original = Event.getBuilder()
            .subject("Meeting")
            .startDate(10, 6, 2025)
            .startTime(9, 0)
            .endDate(10, 6, 2025)
            .endTime(10, 0)
            .buildEvent();
    cal.createEvent(original);

    String cmd = "edit event start Meeting from 2025-06-10T09:00 to 2025-06-10T10:00 with 2025-06-10T09:30";
    new EditCommand(cmd).execute(cal);

    List<Event> results = cal.getScheduleInRange(
            LocalDate.of(2025, 6, 10),
            LocalDate.of(2025, 6, 10)
    );
    assertEquals(1, results.size());
    Event edited = results.get(0);

    assertEquals("Meeting", edited.getSubject());
    assertEquals(LocalTime.of(9, 30), edited.getStartTime());
    assertEquals(LocalTime.of(10, 0), edited.getEndTime());
  }

  @Test
  public void testEditEndTime() {
    Event original = Event.getBuilder()
            .subject("Call")
            .startDate(12, 6, 2025)
            .startTime(14, 0)
            .endDate(12, 6, 2025)
            .endTime(15, 0)
            .buildEvent();
    cal.createEvent(original);

    String cmd = "edit event end Call from 2025-06-12T14:00 to 2025-06-12T15:00 with 2025-06-12T15:30";
    new EditCommand(cmd).execute(cal);

    List<Event> results = cal.getScheduleInRange(
            LocalDate.of(2025, 6, 12),
            LocalDate.of(2025, 6, 12)
    );
    assertEquals(1, results.size());
    Event edited = results.get(0);

    assertEquals("Call", edited.getSubject());
    assertEquals(LocalTime.of(14, 0), edited.getStartTime());
    assertEquals(LocalTime.of(15, 30), edited.getEndTime());
  }

  @Test
  public void testEditDescription() {
    Event original = Event.getBuilder()
            .subject("Workshop")
            .startDate(20, 6, 2025)
            .startTime(9, 0)
            .endDate(20, 6, 2025)
            .endTime(17, 0)
            .description("Old description")
            .buildEvent();
    cal.createEvent(original);

    String cmd = "edit event description Workshop from 2025-06-20T09:00 to 2025-06-20T17:00 with New_description_here";
    new EditCommand(cmd).execute(cal);

    List<Event> results = cal.getScheduleInRange(
            LocalDate.of(2025, 6, 20),
            LocalDate.of(2025, 6, 20)
    );
    assertEquals(1, results.size());
    Event edited = results.get(0);

    assertEquals("Workshop", edited.getSubject());
    assertEquals("New_description_here", edited.getDescription());
  }

  @Test
  public void testEditLocation() {
    Event original = Event.getBuilder()
            .subject("Conference")
            .startDate(25, 6, 2025)
            .startTime(10, 0)
            .endDate(25, 6, 2025)
            .endTime(11, 0)
            .location(EventLocation.PHYSICAL)
            .buildEvent();
    cal.createEvent(original);

    // Change location from PHYSICAL → ONLINE
    String cmd = "edit event location Conference from 2025-06-25T10:00 to 2025-06-25T11:00 with ONLINE";
    new EditCommand(cmd).execute(cal);

    List<Event> results = cal.getScheduleInRange(
            LocalDate.of(2025, 6, 25),
            LocalDate.of(2025, 6, 25)
    );
    assertEquals(1, results.size());
    Event edited = results.get(0);

    assertEquals("Conference", edited.getSubject());
    assertEquals(EventLocation.ONLINE, edited.getLocation());
  }

  @Test
  public void testEditStatus() {
    Event original = Event.getBuilder()
            .subject("Review")
            .startDate(30, 6, 2025)
            .startTime(16, 0)
            .endDate(30, 6, 2025)
            .endTime(17, 0)
            .status(EventStatus.PRIVATE)
            .buildEvent();
    cal.createEvent(original);

    // Change status from PRIVATE → PUBLIC
    String cmd = "edit event status Review from 2025-06-30T16:00 to 2025-06-30T17:00 with PUBLIC";
    new EditCommand(cmd).execute(cal);

    List<Event> results = cal.getScheduleInRange(
            LocalDate.of(2025, 6, 30),
            LocalDate.of(2025, 6, 30)
    );
    assertEquals(1, results.size());
    Event edited = results.get(0);

    assertEquals("Review", edited.getSubject());
    assertEquals(EventStatus.PUBLIC, edited.getStatus());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEditMissingPropertyThrows() {
    String cmd = "edit event  from 2025-06-10T09:00 to 2025-06-10T10:00 with Something";
    new EditCommand(cmd).execute(cal);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEditUnknownPropertyThrows() {
    String cmd = "edit event priority Lunch from 2025-06-15T12:00 to 2025-06-15T13:00 with HIGH";
    new EditCommand(cmd).execute(cal);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEditNonexistentEventThrows() {
    String cmd = "edit event subject Nonexistent from 2025-06-01T08:00 to 2025-06-01T09:00 with Whatever";
    new EditCommand(cmd).execute(cal);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMalformedEditThrows() {
    String cmd = "edit event subject Lunch from 2025-06-15T12:00 to 2025-06-15T13:00";
    new EditCommand(cmd).execute(cal);
  }
}
