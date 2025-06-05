import java.util.Scanner;

import jdk.jfr.Event;

public class CalendarController {

  Readable in;
  Appendable out;

  public CalendarController(Readable readable, Appendable appendable) {
    this.in = readable;
    this.out = appendable;
  }
  public void go() {

    Scanner s = new Scanner(this.in);
    Calendar calendar = new EventCalendar();
    while (s.hasNext()) {
      String in = s.next();
      switch(in) {
        case "q":
        case "quit":
          return;
        case "show":
          for (Line l : m.getLines()) {
            System.out.println(l);
          }
          break;
        case "move":
          try {
            double d = s.nextDouble();
            m.move(d);
          } catch (InputMismatchException ime) {
            ...
          }
          break;
        case "trace":
          try {
            double d = s.nextDouble();
            m.trace(d);
          } catch (InputMismatchException ime) {
            ...
          }
          break;
        case "turn":
          try {
            double d = s.nextDouble();
            m.turn(d);
          } catch (InputMismatchException ime) {
            ...
          }
          break;
        default:
          System.out.println(String.format("Unknown command %s", in));
          break;
      }
    }
  }
}
