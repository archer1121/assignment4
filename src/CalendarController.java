import java.util.Scanner;

public class CalendarController {

  Readable in;
  Appendable out;

  public CalendarController(Readable readable, Appendable appendable) {
    this.in = readable;
    this.out = appendable;
  }
  public void go(Calendar calendarModel) {

    Scanner s = new Scanner(this.in);
    //Calendar calendar = new EventCalendar();
    while (s.hasNext()) {
      String in = s.next().trim();
      int firstSpace = in.indexOf(' ');
      String startingWord = in.substring(0, firstSpace);
      switch(startingWord) {
        case "create":
          System.out.println("create");
          break;

        case "edit":
          System.out.println("edit");
          break;
        case "print":
          System.out.println("print");
          break;
        case "show":
          System.out.println("show");
          break;

        default:
          System.out.println(String.format("Unknown command %s", in));
          break;
      }
    }
  }
}
