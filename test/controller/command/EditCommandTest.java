package controller.command;

import model.Calendar;
import model.Event;
import model.EventLocation;
import model.EventStatus;
import model.IEvent;
import view.ITextView;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Test edit command.
 */
public class EditCommandTest {

  private Calendar cal;
  private static final ITextView DUMMY_VIEW = new ITextView() {

    @Override public void takeMessage(String m) {
      //comment
    }

    @Override public void clearTextBuffer() {
      //comment
    }

    @Override public void displayTextInBuffer() {
      //comment
    }

    @Override public List<String> getTextInBuffer() {
      return List.of();
    }
  };

  @Before
  public void setUp() {

    cal = new Calendar();
  }

  @Test
  public void testEditSubject() {
    IEvent original = Event.getBuilder()
            .subject("Lunch")
            .startDate(15,6,2025).startTime(12,0)
            .endDate(15,6,2025).endTime(13,0)
            .buildEvent();
    cal.addEvent(original);

    new EditCommand(
            "edit event subject Lunch from 2025-06-15T12:00 to 2025-06-15T13:00 with Brunch"
    ).execute(cal, DUMMY_VIEW);

    List<IEvent> res = cal.getScheduleInRange(
            LocalDate.of(2025,6,15),
            LocalDate.of(2025,6,15)
    );
    assertEquals(1, res.size());
    IEvent e = res.get(0);
    assertEquals("Brunch", e.getSubject());
    assertEquals(LocalTime.of(12,0), e.getStartTime());
    assertEquals(LocalTime.of(13,0), e.getEndTime());
  }

  @Test
  public void testEditStartTime() {
    IEvent original = Event.getBuilder()
            .subject("Meeting")
            .startDate(10,6,2025).startTime(9,0)
            .endDate(10,6,2025).endTime(10,0)
            .buildEvent();
    cal.addEvent(original);

    new EditCommand(
            "edit event start Meeting from 2025-06-10T09:00 to" +
                    " 2025-06-10T10:00 with 2025-06-10T09:30"
    ).execute(cal, DUMMY_VIEW);

    IEvent e = cal.getScheduleInRange(
            LocalDate.of(2025,6,10),
            LocalDate.of(2025,6,10)
    ).get(0);
    assertEquals(LocalTime.of(9,30), e.getStartTime());
    assertEquals(LocalTime.of(10,0), e.getEndTime());
  }

  @Test
  public void testEditEndTime() {
    IEvent original = Event.getBuilder()
            .subject("Call")
            .startDate(12,6,2025).startTime(14,0)
            .endDate(12,6,2025).endTime(15,0)
            .buildEvent();
    cal.addEvent(original);

    new EditCommand(
            "edit event end Call from 2025-06-12T14:00 to 2025-06-12T15:00 with 2025-06-12T15:30"
    ).execute(cal, DUMMY_VIEW);

    IEvent e = cal.getScheduleInRange(
            LocalDate.of(2025,6,12),
            LocalDate.of(2025,6,12)
    ).get(0);
    assertEquals(LocalTime.of(15,30), e.getEndTime());
  }

  @Test
  public void testEditDescription() {
    IEvent original = Event.getBuilder()
            .subject("Workshop")
            .startDate(20,6,2025).startTime(9,0)
            .endDate(20,6,2025).endTime(17,0)
            .description("Old")
            .buildEvent();
    cal.addEvent(original);

    new EditCommand(
            "edit event description Workshop from 2025-06-20T09:00 to 2025-06-20T17:00 with New"
    ).execute(cal, DUMMY_VIEW);

    IEvent e = cal.getScheduleInRange(
            LocalDate.of(2025,6,20),
            LocalDate.of(2025,6,20)
    ).get(0);
    assertEquals("New", e.getDescription());
  }

  @Test
  public void testEditLocation() {
    IEvent original = Event.getBuilder()
            .subject("Conf")
            .startDate(25,6,2025).startTime(10,0)
            .endDate(25,6,2025).endTime(11,0)
            .location(EventLocation.PHYSICAL)
            .buildEvent();
    cal.addEvent(original);

    new EditCommand(
            "edit event location Conf from 2025-06-25T10:00 to 2025-06-25T11:00 with ONLINE"
    ).execute(cal, DUMMY_VIEW);

    IEvent e = cal.getScheduleInRange(
            LocalDate.of(2025,6,25),
            LocalDate.of(2025,6,25)
    ).get(0);
    assertEquals(EventLocation.ONLINE, e.getLocation());
  }

  @Test
  public void testEditStatus() {
    IEvent original = Event.getBuilder()
            .subject("Review")
            .startDate(30,6,2025).startTime(16,0)
            .endDate(30,6,2025).endTime(17,0)
            .status(EventStatus.PRIVATE)
            .buildEvent();
    cal.addEvent(original);

    new EditCommand(
            "edit event status Review from 2025-06-30T16:00 to 2025-06-30T17:00 with PUBLIC"
    ).execute(cal, DUMMY_VIEW);

    IEvent e = cal.getScheduleInRange(
            LocalDate.of(2025,6,30),
            LocalDate.of(2025,6,30)
    ).get(0);
    assertEquals(EventStatus.PUBLIC, e.getStatus());
  }

  // — malformed —

  @Test(expected = IllegalArgumentException.class)
  public void testEditMissingPropertyThrows() {
    new EditCommand(
            "edit event  from 2025-06-10T09:00 to 2025-06-10T10:00 with Something"
    ).execute(cal, DUMMY_VIEW);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEditUnknownPropertyThrows() {
    new EditCommand(
            "edit event priority Lunch from 2025-06-15T12:00 to 2025-06-15T13:00 with HIGH"
    ).execute(cal, DUMMY_VIEW);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEditNonexistentEventThrows() {
    new EditCommand(
            "edit event subject Nonexistent from 2025-06-01T08:00 to 2025-06-01T09:00 with Whatever"
    ).execute(cal, DUMMY_VIEW);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMalformedEditThrows() {
    new EditCommand(
            "edit event subject Lunch from 2025-06-15T12:00 to 2025-06-15T13:00"
    ).execute(cal, DUMMY_VIEW);
  }
}
