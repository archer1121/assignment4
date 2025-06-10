// src/test/java/controller/command/CreateCommandTest.java
package controller.command;

import model.*;
import view.ITextView;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

import static org.junit.Assert.*;

public class CreateCommandTest {

  /** A dummy calendar that accepts calls but does nothing. */
  private static class DummyCal implements ICalendar {
    @Override
    public ZoneId getTimeZone() {
      return null;
    }

    @Override public void addEvent(IEvent e)            { }
    @Override public void removeEvent(IEvent e)         { }
    @Override public void addEventSeries(IEventSeries s){ }

    @Override
    public void removeEventSeries(IEventSeries series) {

    }

    @Override public void replaceEvent(IEvent o, IEvent n){ }
    @Override public List<IEvent> getEvents()           { return List.of(); }
    @Override public List<IEvent> getScheduleInRange(LocalDate s, LocalDate e){ return List.of(); }

    @Override
    public IEventSeries getSeriesFor(IEvent e) {
      return null;
    }

    @Override
    public void replaceSeries(IEventSeries oldSeries, IEventSeries newSeries) {

    }
  }

  /** A stub view that swallows anything. */
  private static class DummyView implements ITextView {
    @Override public void takeMessage(String m)      { }
    @Override public void clearTextBuffer()          { }
    @Override public void displayTextInBuffer()      { }
    @Override public List<String> getTextInBuffer()  { return List.of(); }
  }

  /** A calendar that records how many events or series were added. */
  private static class RecordingCal extends DummyCal {
    int singleCount  = 0;
    int seriesCount  = 0;
    @Override public void addEvent(IEvent e)             { singleCount++; }
    @Override public void addEventSeries(IEventSeries s) { seriesCount = s.getEvents().size(); }
  }

  private final ICalendar dummyCal  = new DummyCal();
  private final ITextView dummyView = new DummyView();

  // — parsing‐only smoke tests — no exceptions

  @Test public void testValidCreateSingleTimed() {
    new CreateCommand(
            "create event Meeting from 2025-06-10T09:00 to 2025-06-10T10:00"
    ).execute(dummyCal, dummyView);
  }

  @Test public void testValidCreateTimedSeriesFor() {
    new CreateCommand(
            "create event Standup from 2025-06-12T09:00 to 2025-06-12T09:15 repeats MWF for 5 times"
    ).execute(dummyCal, dummyView);
  }

  @Test public void testValidCreateTimedSeriesUntil() {
    new CreateCommand(
            "create event Report from 2025-06-01T08:00 to 2025-06-01T09:00 repeats TU until 2025-06-01"
    ).execute(dummyCal, dummyView);
  }

  @Test public void testValidCreateSingleAllDay() {
    new CreateCommand(
            "create event Holiday on 2025-12-25"
    ).execute(dummyCal, dummyView);
  }

  @Test public void testValidCreateAllDaySeriesFor() {
    new CreateCommand(
            "create event Yoga on 2025-06-11 repeats TRF for 3 times"
    ).execute(dummyCal, dummyView);
  }

  @Test public void testValidCreateAllDaySeriesUntil() {
    new CreateCommand(
            "create event Checkup on 2025-07-01 repeats U until 2025-07-01"
    ).execute(dummyCal, dummyView);
  }

  // — malformed commands —

  @Test(expected = IllegalArgumentException.class)
  public void testMalformedCreateMissingKeyword() {
    new CreateCommand("create evnt BadCommand")
            .execute(dummyCal, dummyView);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMalformedCreateMissingFromTo() {
    new CreateCommand("create event Meeting from 2025-06-10T09:00")
            .execute(dummyCal, dummyView);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMalformedCreateMissingOn() {
    new CreateCommand("create event Holiday 2025-12-25")
            .execute(dummyCal, dummyView);
  }

  // — event‐builder unit test —

  @Test public void testEventBuilderValid() {
    IEvent e = Event.getBuilder()
            .subject("Meeting")
            .startDate(10, 6, 2025)
            .startTime(9, 0)
            .endDate(10, 6, 2025)
            .endTime(10, 0)
            .buildEvent();

    assertEquals("Meeting", e.getSubject());
    assertEquals(LocalDate.of(2025,6,10), e.getStartDate());
    assertEquals(LocalTime.of(9,0),     e.getStartTime());
    assertEquals(LocalDate.of(2025,6,10), e.getEndDate());
    assertEquals(LocalTime.of(10,0),    e.getEndTime());
  }

  // — integration: single event in real Calendar —

  @Test public void testCreateEventAddsToList() {
    Calendar cal = new Calendar();
    IEvent e = Event.getBuilder()
            .subject("Lunch")
            .startDate(15,6,2025).startTime(12,0)
            .endDate(15,6,2025).endTime(13,0)
            .buildEvent();

    cal.addEvent(e);
    List<IEvent> all = cal.getScheduleInRange(
            LocalDate.of(2025,6,15), LocalDate.of(2025,6,15)
    );
    assertEquals(1, all.size());
    assertEquals(e, all.get(0));
  }

  // — new: verify series‐for and series‐until produce correct counts —

  @Test public void testCreateTimedSeriesForAddsCorrectCount() {
    RecordingCal cal = new RecordingCal();
    // match exactly your valid‐command test:
    String cmd =
            "create event Standup from 2025-06-12T09:00 to 2025-06-12T09:15 repeats MWF for 5 times";
    new CreateCommand(cmd).execute(cal, dummyView);
    assertEquals(5, cal.seriesCount);
  }

  @Test public void testCreateSeriesUntilAddsCorrectCount() {
    // June 1 2025 is Sunday → repeat SU until June 1 2025 yields 1
    RecordingCal cal = new RecordingCal();
    new CreateCommand(
            "create event Bar from 2025-06-01T08:00 to 2025-06-01T09:00 repeats SU until 2025-06-01"
    ).execute(cal, dummyView);
    assertEquals(1, cal.seriesCount);
  }

}
