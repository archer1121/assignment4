import org.junit.Before;
import org.junit.Test;

import model.DateTimeFacade;
import model.Event;
import model.EventLocation;
import model.EventStatus;
import model.IDateTimeFacade;
import model.IEvent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class EventTest {
  private  Event.EventBuilder builder;
  private static final IDateTimeFacade facade = new DateTimeFacade();

  @Before
  public void setUp() {
    builder = Event.getBuilder();

  }

  @Test
  public void eventThrowsForNoSubject() {
    assertThrows(IllegalArgumentException.class, () -> builder.subject(null)
            .startDate(1, 1, 2021)
            .startTime(10, 0)
            .buildEvent());
  }

  @Test
  public void eventThrowsForNoStartDate() {
    assertThrows(IllegalArgumentException.class, () -> builder.subject("Fishing")
            .startTime(10, 0)
            .buildEvent());
  }

  @Test
  public void eventThrowsForNoStartTime() {
    assertThrows(IllegalArgumentException.class, () -> builder.subject("Fishing")
            .startDate(1, 1, 2021)
            .buildEvent());
  }

  @Test
  public void builderThrowsWhenNoFieldsAreSet() {
    assertThrows(IllegalArgumentException.class, () -> builder.buildEvent());
  }

  @Test
  public void builderDoesNotThrowWhenRequiredFieldsAreSet() {
    try {
      builder.subject("Fishing")
              .startDate(1, 1, 2021)
              .startTime(10, 0)
              .buildEvent();
    } catch (IllegalArgumentException e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void builderDoesNotThrowWhenAllFieldsAreSet() {
    try {
      builder
              .subject("Fishing")
              .startDate(1, 1, 2021)
              .endDate(1, 1, 2021)
              .startTime(10, 0)
              .endTime(11, 0)
              .description("we are fishing")
              .location(EventLocation.PHYSICAL)
              .status(EventStatus.PUBLIC)
              .buildEvent();
    } catch (IllegalArgumentException e) {
      fail();
    }
  }

  @Test
  public void EventGettersWork() {
    IEvent fishingEvent = builder
            .subject("Fishing")
            .startDate(1, 1, 2021)
            .endDate(1, 1, 2021)
            .startTime(10, 0)
            .endTime(11, 0)
            .description("we are fishing")
            .location(EventLocation.PHYSICAL)
            .status(EventStatus.PUBLIC)
            .buildEvent();

    assertEquals("Fishing", fishingEvent.getSubject());
    assertEquals(facade.dateOf(1, 1, 2021), fishingEvent.getStartDate());
    assertEquals(facade.dateOf(1, 1, 2021), fishingEvent.getEndDate());
    assertEquals(facade.timeOf(10, 0), fishingEvent.getStartTime());
    assertEquals(facade.timeOf(11, 0), fishingEvent.getEndTime());
    assertEquals("we are fishing", fishingEvent.getDescription());
    assertEquals(EventLocation.PHYSICAL, fishingEvent.getLocation());
    assertEquals(EventStatus.PUBLIC, fishingEvent.getStatus());


  }

  @Test
  public void EventThrowsWhenEndDateComesBeforeStartDate() {
    assertThrows(IllegalArgumentException.class, () ->
            builder.subject("Fishing")
                    .startDate(1, 1, 2021)
                    .startTime(10, 0)
                    .endDate(1, 1, 2020)
                    .endTime(1, 0)
                    .buildEvent());
  }

  @Test
  public void EventThrowsWhenEndTimeComesBeforeStartTimeOnSameDate() {
    assertThrows(IllegalArgumentException.class, () ->
            builder.subject("Fishing")
                    .startDate(1, 1, 2021)
                    .startTime(23, 0)
                    .endDate(1, 1, 2021)
                    .endTime(9, 0)
                    .buildEvent());
  }

  @Test
  public void eventDoesNotThrowWhenEndTimeComesBeforeStartTimeOnDifferentDate() {
    try {
      builder.subject("Fishing")
              .startDate(1, 1, 2021)
              .startTime(23, 0)
              .endDate(2, 1, 2021)
              .endTime(9, 0)
              .buildEvent();
    } catch (IllegalArgumentException e) {
      fail(e.getMessage());
    }

  }

  @Test
  public void builderSetsEndTimeTo5pm() {
    IEvent fishing = builder.subject("Fishing")
            .startDate(1, 1, 2021)
            .startTime(10, 0)
            .buildEvent();
    assertEquals(facade.timeOf(17, 0), fishing.getEndTime());
  }

  @Test
  public void builderSetsEndDayToStartDayWhenMissing() {
    IEvent fishing = builder.subject("Fishing")
            .startDate(1, 1, 2021)
            .startTime(10, 0)
            .buildEvent();
    assertEquals(facade.dateOf(1, 1, 2021), fishing.getEndDate());
  }

  @Test
  public void nonSetEventFieldsAreAllNull() {
    IEvent fishing = builder.subject("Fishing")
            .startDate(1, 1, 2021)
            .startTime(10, 0)
            .buildEvent();
    assertNull(fishing.getStatus());
    assertNull(fishing.getLocation());
    assertNull(fishing.getDescription());
  }

  @Test
  public void eventIsAllDayIfStarTimeIs8AndEndTimeIs17() {
    IEvent fishing = builder.subject("Fishing")
            .startDate(1, 1, 2021)
            .startTime(8, 0)
            .endDate(1, 1, 2021)
            .endTime(17, 0)
            .buildEvent();

    assertTrue(fishing.isAllDayEvent());
  }

  @Test
  public void eventIsNotAllDayIfStarTimeIsAfter8AndEndTimeIsBefore17() {
    IEvent fishing = builder.subject("Fishing")
            .startDate(1, 1, 2021)
            .startTime(9, 0)
            .endDate(1, 1, 2021)
            .endTime(16, 0)
            .buildEvent();

    assertFalse(fishing.isAllDayEvent());
  }

  @Test
  public void eventIsAllDayIfStarTimeIsBefore8AndEndTimeIsAfter17() {
    IEvent fishing = builder.subject("Fishing")
            .startDate(1, 1, 2021)
            .startTime(7, 0)
            .endDate(1, 1, 2021)
            .endTime(18, 0)
            .buildEvent();

    assertTrue(fishing.isAllDayEvent());
  }

  @Test
  public void eventIsNotAllDayIfStartAndEndDateIsNotTheSame() {
    IEvent fishing = builder.subject("Fishing")
            .startDate(1, 1, 2021)
            .startTime(8, 0)
            .endDate(2, 1, 2021)
            .endTime(17, 0)
            .buildEvent();

    assertFalse(fishing.isAllDayEvent());
  }

  @Test
  public void equalsWorksForExactSameEvents() {
    IEvent e1 = builder.subject("Fishing")
            .startDate(1, 1, 2021)
            .startTime(8, 0)
            .endDate(2, 1, 2021)
            .endTime(17, 0)
            .buildEvent();

    IEvent e2 = builder.subject("Fishing")
            .startDate(1, 1, 2021)
            .startTime(8, 0)
            .endDate(2, 1, 2021)
            .endTime(17, 0)
            .buildEvent();

    assertEquals(e1, e2);
  }

  @Test
  public void equalsDoesNotWorkForEventsWithDifferingRequiredFields() {
    IEvent e1 = builder.subject("Camping")
            .startDate(1, 1, 2021)
            .startTime(8, 0)
            .endDate(2, 1, 2021)
            .endTime(17, 0)
            .buildEvent();

    IEvent e2 = builder.subject("Fishing")
            .startDate(1, 1, 2021)
            .startTime(8, 0)
            .endDate(2, 1, 2021)
            .endTime(17, 0)
            .buildEvent();
    IEvent e3 = builder.subject("Fishing")
            .startDate(2, 1, 2021)
            .startTime(8, 0)
            .endDate(2, 1, 2021)
            .endTime(17, 0)
            .buildEvent();
    IEvent e4 = builder.subject("Fishing")
            .startDate(2, 1, 2021)
            .startTime(9, 0)
            .endDate(2, 1, 2021)
            .endTime(17, 0)
            .buildEvent();
    IEvent e5 = builder.subject("Fishing")
            .startDate(2, 1, 2021)
            .startTime(9, 0)
            .endDate(3, 1, 2021)
            .endTime(17, 0)
            .buildEvent();
    IEvent e6 = builder.subject("Fishing")
            .startDate(2, 1, 2021)
            .startTime(9, 0)
            .endDate(3, 1, 2021)
            .endTime(18, 0)
            .buildEvent();
    IEvent e7 = builder.subject("Fishing")
            .startDate(2, 1, 2021)
            .startTime(9, 0)
            .endDate(3, 1, 2021)
            .endTime(19, 0)
            .buildEvent();

    assertNotEquals(e1, e2);
    assertNotEquals(e2, e3);
    assertNotEquals(e3, e4);
    assertNotEquals(e4, e5);
    assertNotEquals(e5, e6);
    assertNotEquals(e6, e7);
  }

  @Test
  public void equalsIsNotAffectedByOptionalFields() {
    IEvent e1 = builder.subject("Fishing")
            .startDate(1, 1, 2021)
            .startTime(8, 0)
            .endDate(2, 1, 2021)
            .endTime(17, 0)
            .location(EventLocation.PHYSICAL)
            .buildEvent();

    IEvent e2 = builder.subject("Fishing")
            .startDate(1, 1, 2021)
            .startTime(8, 0)
            .endDate(2, 1, 2021)
            .endTime(17, 0)
            .description("we are fishing")
            .buildEvent();
    assertEquals(e1, e2);
  }

  @Test
  public void editEventEditsEvent() {
    Event e1 = builder.subject("Camping")
            .startDate(1, 1, 2021)
            .startTime(8, 0)
            .endDate(2, 1, 2021)
            .endTime(17, 0)
            .buildEvent();

    Event.EventBuilder eventEditor = Event.editEvent(e1);

    IEvent edited = eventEditor.startTime(7,0).buildEvent();
    assertEquals(facade.timeOf(7, 0), edited.getStartTime());
  }
}
