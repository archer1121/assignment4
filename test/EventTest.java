import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.fail;

public class EventTest {
  public Event.EventBuilder builder;
  @Before
  public void setUp() {
    builder = Event.getBuilder();
  }

  @Test
  public void eventThrowsForNoSubject() {
    assertThrows(IllegalArgumentException.class, () -> builder.subject(null)
            .startDate(1,1,2021)
            .startTime(10,0)
            .build());
  }
  @Test
  public void eventThrowsForNoStartDate() {
    assertThrows(IllegalArgumentException.class, () -> builder.subject("Fishing")
            .startTime(10,0)
            .build());
  }

  @Test
  public void eventThrowsForNoStartTime() {
    assertThrows(IllegalArgumentException.class, () -> builder.subject("Fishing")
            .startDate(1, 1, 2021)
            .build());
  }
  @Test
  public void builderThrowsWhenNoFieldsAreSet() {
    assertThrows(IllegalArgumentException.class, () -> builder.build());
  }
  @Test
  public void builderDoesNotThrowWhenRequiredFieldsAreSet() {
    try {
      builder.subject("Fishing")
              .startDate(1, 1, 2021)
              .startTime(10, 0)
              .build();
    } catch (IllegalArgumentException e) {
      fail();
    }
  }

  @Test
  public void builderDoesNotThrowWhenAllFieldsAreSet() {
    try {
      builder
              .subject("Fishing")
              .startDate(1, 1, 2021)
              .endDate(1,1,2021)
              .startTime(10, 0)
              .endTime(11,0)
              .description("we are fishing")
              .location(EventLocation.PHYSICAL)
              .status(EventStatus.PUBLIC)
              .build();
    } catch (IllegalArgumentException e) {
      fail();
    }
  }
  @Test
  public void EventGettersWork() {
    Event fishingEvent  = builder
            .subject("Fishing")
            .startDate(1, 1, 2021)
            .endDate(1,1,2021)
            .startTime(10, 0)
            .endTime(11,0)
            .description("we are fishing")
            .location(EventLocation.PHYSICAL)
            .status(EventStatus.PUBLIC)
            .build();

    assertEquals("Fishing", fishingEvent.getSubject());
    assertEquals( LocalDate.of (1, 1, 2021), fishingEvent.get());
    assertEquals(, fishingEvent.get());
    assertEquals(, fishingEvent.get());
    assertEquals(, fishingEvent.get());
    assertEquals(, fishingEvent.get());
    assertEquals(, fishingEvent.get());
    assertEquals(, fishingEvent.get());



  }
  @Test
  public void () {
    assert
  }
  @Test
  public void () {
    assert
  }






}
