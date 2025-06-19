package controller.command;

import model.Calendar;
import model.Event;
import model.ICalendar;
import model.IEvent;
import model.IEventSeries;
import view.ITextView;

import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Unit‐tests for CreateCommand.
 */
public class CreateCommandTest {

  /**
   * Dummy calendar that does absolutely nothing.
   */
  private static class DummyCal implements ICalendar {
    /**
     * Always returns null.
     */
    @Override
    public ZoneId getTimeZone() {
      // No‐op
      return null;
    }

    /**
     * No‐op.
     */
    @Override
    public ICalendar setTimeZone(ZoneId timeZone) {
      return this;
    }

    /**
     * No‐op.
     */
    @Override
    public void addEvent(IEvent e) {
      // no‐op
    }

    /**
     * No‐op.
     */
    @Override
    public void removeEvent(IEvent e) {
      // no‐op
    }

    /**
     * No‐op.
     */
    @Override
    public void addEventSeries(IEventSeries s) {
      // no‐op
    }

    /**
     * No‐op.
     */
    @Override
    public void removeEventSeries(IEventSeries series) {
      // no‐op
    }

    /**
     * No‐op.
     */
    @Override
    public void replaceEvent(IEvent o, IEvent n) {
      // no‐op
    }

    /**
     * No‐op.
     */
    @Override
    public void copyEventsAndShift(
            LocalDate rangeStart,
            LocalDate rangeEnd,
            ICalendar from,
            LocalDate atStartDate
    ) {
      // no‐op
    }

    /**
     * No‐op.
     */
    @Override
    public void copyEvents(LocalDate rangeStart, LocalDate rangeEnd, ICalendar from) {
      // no‐op
    }

    /**
     * Always empty.
     */
    @Override
    public List<IEvent> getEvents() {
      return List.of();
    }

    /**
     * Always empty.
     */
    @Override
    public List<IEvent> getScheduleInRange(LocalDate s, LocalDate e) {
      return List.of();
    }

    /**
     * Always null.
     */
    @Override
    public IEventSeries getSeriesFor(IEvent e) {
      return null;
    }

    /**
     * No‐op.
     */
    @Override
    public void replaceSeries(IEventSeries oldSeries, IEventSeries newSeries) {
      // no‐op
    }
  }

  /**
   * A dummy view that swallows messages.
   */
  private static class DummyView implements ITextView {
    /**
     * No‐op.
     */
    @Override
    public void takeMessage(String m) {
      // no‐op
    }

    /**
     * No‐op.
     */
    @Override
    public void clearTextBuffer() {
      // no‐op
    }

    /**
     * No‐op.
     */
    @Override
    public void displayTextInBuffer() {
      // no‐op
    }

    /**
     * Always empty.
     */
    @Override
    public List<String> getTextInBuffer() {
      return List.of();
    }
  }

  /**
   * A calendar that counts how many events/series it receives.
   */
  private static class RecordingCal extends DummyCal {
    int singleCount = 0;
    int seriesCount = 0;

    @Override
    public void addEvent(IEvent e) {
      singleCount++;
    }

    @Override
    public void addEventSeries(IEventSeries s) {
      seriesCount = s.getEvents().size();
    }
  }

  private final ICalendar dummyCal = new DummyCal();
  private final ITextView dummyView = new DummyView();













  // ─── malformed commands still throw ─────────────────────────────────

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

  // ─── proper builder‐tests and integration tests ──────────────────────

  @Test
  public void testEventBuilderValid() {
    Event.EventBuilder builder = Event.getBuilder()
            .subject("Meeting")
            .startDate(10, 6, 2025)
            .startTime(9, 0)
            .endDate(10, 6, 2025)
            .endTime(10, 0);
    IEvent e = builder.buildEvent();

    assertEquals("Meeting", e.getSubject());
    assertEquals(LocalDate.of(2025, 6, 10), e.getStartDate());
    assertEquals(LocalTime.of(9, 0), e.getStartTime());
    assertEquals(LocalDate.of(2025, 6, 10), e.getEndDate());
    assertEquals(LocalTime.of(10, 0), e.getEndTime());
  }

  @Test
  public void testCreateEventAddsToList() {
    Calendar cal = new Calendar();
    IEvent e = Event.getBuilder()
            .subject("Lunch")
            .startDate(15, 6, 2025).startTime(12, 0)
            .endDate(15, 6, 2025).endTime(13, 0)
            .buildEvent();

    cal.addEvent(e);
    List<IEvent> all = cal.getScheduleInRange(
            LocalDate.of(2025, 6, 15),
            LocalDate.of(2025, 6, 15)
    );

    assertEquals(1, all.size());
    assertEquals(e, all.get(0));
  }

  @Test
  public void testCreateTimedSeriesForAddsCorrectCount() {
    RecordingCal cal = new RecordingCal();
    String cmd = ""
            + "create event Standup from 2025-06-12T09:00 to 2025-06-12T09:15 "
            + "repeats MWF for 5 times";
    new CreateCommand(cmd).execute(cal, dummyView);
    assertEquals("Series‐for on MWF for 5 occurrences", 5, cal.seriesCount);
  }

  @Test
  public void testCreateSeriesUntilAddsCorrectCount() {
    RecordingCal cal = new RecordingCal();
    new CreateCommand(
            "create event Bar from 2025-06-01T08:00 to 2025-06-01T09:00 "
                    + "repeats SU until 2025-06-01"
    ).execute(cal, dummyView);
    assertEquals("Sunday on June 1 yields exactly 1", 1, cal.seriesCount);
  }
}
