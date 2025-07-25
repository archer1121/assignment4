package controller.command;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 *  A Test class to ensure the Use Calendar Command works as intended.
 */
public class UseCalendarCommandTest {

  @Test
  public void testParseName() {
    UseCalendarCommand uc = new UseCalendarCommand("use calendar --name Personal");
    assertEquals("Personal", uc.getCalendarName());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMissingNameFails() {

    new UseCalendarCommand("use calendar").getCalendarName();
  }
}
