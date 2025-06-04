import org.junit.Before;
import org.junit.Test;

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
      assertThrows(IllegalArgumentException.class, () -> builder
              .subject("Fishing")
              .startDate(1, 1, 2021)
              .endDate(1,1,2021)
              .startTime(10, 0)
              .endTime(11,0)
              .description("we are fishing")
              .location(Event.Location.PHYSICAL)
              .status(Event.Status.PUBLIC)
              .build());
    } catch (IllegalArgumentException e) {
      fail();
    }
  }
  @Test
  public void () {
    assert
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
