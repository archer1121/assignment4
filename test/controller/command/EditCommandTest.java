package controller.command;

import model.Calendar;
import model.ICalendar;
import model.Event;
import model.IEvent;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * JUnit 4 tests for EditCommand. Valid “edit …” strings should not throw;
 * malformed strings should throw IllegalArgumentException.
 */
public class EditCommandTest {
  private ICalendar cal;

  @Before
  public void setUp() {
    // Use the real Calendar implementation as the model
    cal = new Calendar();
  }

  private final ICalendar dummyModel = new ICalendar() {

    @Override
    public void createEvent(Event event) {

    }

    @Override
    public void createEventSeries(List<Event> series) {

    }

    @Override public void editEvent(Event event) { }
    @Override public java.util.List<Event> getScheduleInRange(java.time.LocalDate start, java.time.LocalDate end) { return null; }
  };

  @Test
  public void testValidEditEventSingle() {
    // “edit event <property> <subject> from <dt1> to <dt2> with <new>”
    String cmd = "edit event location Meeting from 2025-06-10T09:00 to 2025-06-10T10:00 with RoomA";
    new EditCommand(cmd).execute(dummyModel);
  }

  @Test
  public void testValidEditEventsFrom() {
    // “edit events <property> <subject> from <dt> with <new>”
    String cmd = "edit events status Task1 from 2025-06-11T14:00 with Completed";
    new EditCommand(cmd).execute(dummyModel);
  }

  @Test
  public void testValidEditSeriesFrom() {
    // “edit series <property> <subject> from <dt> with <new>”
    String cmd = "edit series description Reminder from 2025-06-12T08:00 with NewDesc";
    new EditCommand(cmd).execute(dummyModel);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMalformedEditMissingProperty() {
    // Missing <property> between “edit event” and <subject>
    String cmd = "edit event Meeting from 2025-06-10T09:00 to 2025-06-10T10:00 with LocationA";
    new EditCommand(cmd).execute(dummyModel);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMalformedEditMissingFrom() {
    // Missing “from”
    String cmd = "edit event location Meeting to 2025-06-10T10:00 with RoomA";
    new EditCommand(cmd).execute(dummyModel);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMalformedEditMissingWith() {
    // Missing “with”
    String cmd = "edit events status Task1 from 2025-06-11T14:00 Completed";
    new EditCommand(cmd).execute(dummyModel);
  }

  @Test
  public void testEditSubject() {
    // 1) Create and add an Event "Lunch" from 2025-06-15T12:00 to 2025-06-15T13:00
    IEvent original = Event.getBuilder()
            .subject("Lunch")
            .startDate(15, 6, 2025)
            .startTime(12, 0)
            .endDate(15, 6, 2025)
            .endTime(13, 0)
            .buildEvent();
    cal.createEvent((Event) original);

    // 2) Issue an edit command to change subject "Lunch" → "Brunch"
    String cmd = "edit event subject Lunch from 2025-06-15T12:00 to 2025-06-15T13:00 with Brunch";
    new EditCommand(cmd).execute(cal);

    // 3) Retrieve all events on 2025-06-15 and verify:
    List<Event> results = cal.getScheduleInRange(LocalDate.of(2025, 6, 15),
            LocalDate.of(2025, 6, 15));
    assertEquals(1, results.size());
    Event edited = results.get(0);

    assertEquals("Brunch", edited.getSubject());
    assertEquals(LocalDate.of(2025, 6, 15), edited.getStartDate());
    assertEquals(LocalTime.of(12, 0), edited.getStartTime());
    assertEquals(LocalTime.of(13, 0), edited.getEndTime());
  }

  @Test
  public void testEditStartTime() {
    IEvent original = Event.getBuilder()
            .subject("Meeting")
            .startDate(10, 6, 2025)
            .startTime(9, 0)
            .endDate(10, 6, 2025)
            .endTime(10, 0)
            .buildEvent();
    cal.createEvent((Event) original);

    // Change the start time from 09:00 → 09:30
    String cmd = "edit event start Meeting from 2025-06-10T09:00 to 2025-06-10T10:00 with 2025-06-10T09:30";
    new EditCommand(cmd).execute(cal);

    List<Event> results = cal.getScheduleInRange(LocalDate.of(2025, 6, 10),
            LocalDate.of(2025, 6, 10));
    assertEquals(1, results.size());
    Event edited = results.get(0);

    assertEquals("Meeting", edited.getSubject());
    assertEquals(LocalTime.of(9, 30), edited.getStartTime());
    assertEquals(LocalTime.of(10, 0), edited.getEndTime());
  }

  @Test
  public void testEditEndTime() {
    IEvent original = Event.getBuilder()
            .subject("Call")
            .startDate(12, 6, 2025)
            .startTime(14, 0)
            .endDate(12, 6, 2025)
            .endTime(15, 0)
            .buildEvent();
    cal.createEvent((Event) original);

    // Change the end time from 15:00 → 15:30
    String cmd = "edit event end Call from 2025-06-12T14:00 to 2025-06-12T15:00 with 2025-06-12T15:30";
    new EditCommand(cmd).execute(cal);

    List<Event> results = cal.getScheduleInRange(LocalDate.of(2025, 6, 12),
            LocalDate.of(2025, 6, 12));
    assertEquals(1, results.size());
    Event edited = results.get(0);

    assertEquals("Call", edited.getSubject());
    assertEquals(LocalTime.of(14, 0), edited.getStartTime());
    assertEquals(LocalTime.of(15, 30), edited.getEndTime());
  }

  @Test
  public void testEditDescription() {
    IEvent original = Event.getBuilder()
            .subject("Workshop")
            .startDate(20, 6, 2025)
            .startTime(9, 0)
            .endDate(20, 6, 2025)
            .endTime(17, 0)
            .description("Old description")
            .buildEvent();
    cal.createEvent((Event) original);

    // Change description to "New description"
    String cmd = "edit event description Workshop from 2025-06-20T09:00 to 2025-06-20T17:00 with New_description_here";
    new EditCommand(cmd).execute(cal);

    List<Event> results = cal.getScheduleInRange(LocalDate.of(2025, 6, 20),
            LocalDate.of(2025, 6, 20));
    assertEquals(1, results.size());
    Event edited = results.get(0);

    assertEquals("Workshop", edited.getSubject());
    assertEquals("New_description_here", edited.getDescription());
    assertEquals(LocalTime.of(9, 0), edited.getStartTime());
    assertEquals(LocalTime.of(17, 0), edited.getEndTime());
  }

  @Test
  public void testEditLocation() {
    Event original = Event.getBuilder()
            .subject("Conference")
            .startDate(25, 6, 2025)
            .startTime(10, 0)
            .endDate(25, 6, 2025)
            .endTime(11, 0)
            .location(EventLocation.OFFICE)
            .buildEvent();
    cal.createEvent(original);

    // Change location from OFFICE → CONFERENCE_ROOM
    String cmd = "edit event location Conference from 2025-06-25T10:00 to 2025-06-25T11:00 with CONFERENCE_ROOM";
    new EditCommand(cmd).execute(cal);

    List<Event> results = cal.getScheduleInRange(LocalDate.of(2025, 6, 25),
            LocalDate.of(2025, 6, 25));
    assertEquals(1, results.size());
    Event edited = results.get(0);

    assertEquals("Conference", edited.getSubject());
    assertEquals(EventLocation.CONFERENCE_ROOM, edited.getLocation());
  }

  @Test
  public void testEditStatus() {
    Event original = Event.getBuilder()
            .subject("Review")
            .startDate(30, 6, 2025)
            .startTime(16, 0)
            .endDate(30, 6, 2025)
            .endTime(17, 0)
            .status(EventStatus.TENTATIVE)
            .buildEvent();
    cal.createEvent(original);

    // Change status from TENTATIVE → CONFIRMED
    String cmd = "edit event status Review from 2025-06-30T16:00 to 2025-06-30T17:00 with CONFIRMED";
    new EditCommand(cmd).execute(cal);

    List<Event> results = cal.getScheduleInRange(LocalDate.of(2025, 6, 30),
            LocalDate.of(2025, 6, 30));
    assertEquals(1, results.size());
    Event edited = results.get(0);

    assertEquals("Review", edited.getSubject());
    assertEquals(EventStatus.CONFIRMED, edited.getStatus());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEditMissingPropertyThrows() {
    // Missing <property> between “edit event” and subject
    String cmd = "edit event  from 2025-06-10T09:00 to 2025-06-10T10:00 with Something";
    new EditCommand(cmd).execute(cal);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEditUnknownPropertyThrows() {
    // “priority” is not a valid property
    String cmd = "edit event priority Lunch from 2025-06-15T12:00 to 2025-06-15T13:00 with HIGH";
    new EditCommand(cmd).execute(cal);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEditNonexistentEventThrows() {
    // No event exists with subject “Nonexistent”
    String cmd = "edit event subject Nonexistent from 2025-06-01T08:00 to 2025-06-01T09:00 with Whatever";
    new EditCommand(cmd).execute(cal);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMalformedEditThrows() {
    // Missing “with <value>”
    String cmd = "edit event subject Lunch from 2025-06-15T12:00 to 2025-06-15T13:00";
    new EditCommand(cmd).execute(cal);
  }

}
