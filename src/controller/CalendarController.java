package controller;

import java.util.Scanner;

import controller.command.Command;
import controller.command.CreateCommand;
import model.ICalendar;

public class CalendarController {

  Readable in;
  Appendable out;

  public CalendarController(Readable readable, Appendable appendable) {
    this.in = readable;
    this.out = appendable;
  }
  public void go(ICalendar calendarModel) {

    Scanner s = new Scanner(this.in);
    //model.Calendar calendar = new EventCalendar();
    while (s.hasNext()) {
      String in = s.nextLine().trim();
      int firstSpace = in.indexOf(' ');
      if (firstSpace == -1) {
        System.out.println(String.format("Unknown command %s", in));
        continue;//very good
      }
      String startingWord = in.substring(0, firstSpace);
      switch(startingWord) {
        case "create":
          System.out.println("create");
          Command createCommand = new CreateCommand(in);
          createCommand.execute(calendarModel);

          break;

        case "edit":
          System.out.println("edit");
          Command editCommand = new CreateCommand(in);
          editCommand.execute(calendarModel);

          break;

        case "print":
          System.out.println("print");
          Command printCommand = new CreateCommand(in);
          printCommand.execute(calendarModel);

          break;

        case "show":
          System.out.println("show");
          Command showCommand = new CreateCommand(in);
          showCommand.execute(calendarModel);

          break;

        default:
          System.out.println(String.format("Unknown command %s", in));
          break;
      }
    }
  }
}
