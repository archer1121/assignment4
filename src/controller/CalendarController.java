package controller;

import controller.command.Command;
import controller.command.CreateCommand;
import controller.command.EditCommand;
import controller.command.PrintCommand;
import controller.command.ShowCommand;
import model.ICalendar;
import view.ITextView;
import view.TextView;

import java.io.IOException;
import java.io.Reader;
import java.util.Scanner;

public class CalendarController {
  private final Scanner      scanner;
  private final ICalendar    model;
  private final ITextView    view;

  /**
   * @param model  your calendar model
   * @param in     where to read user commands (e.g. new InputStreamReader(System.in))
   * @param out    where to send output (e.g. System.out)
   */
  public CalendarController(ICalendar model, Reader in, Appendable out) {
    this.model   = model;
    this.scanner = new Scanner(in);
    this.view    = new TextView(out);
  }

  /**
   * Main loop: read lines, dispatch to the right Command, render via TextView
   */
  public void go() throws IOException {
    while (scanner.hasNextLine()) {
      String line = scanner.nextLine().trim();
      // clear previous output
      view.clearTextBuffer();

      Command cmd;
      if (line.startsWith("create ")) {
        cmd = new CreateCommand(line);
      } else if (line.startsWith("edit ")) {
        cmd = new EditCommand(line);
      } else if (line.startsWith("print ")) {
        cmd = new PrintCommand(line);
      } else if (line.startsWith("show ")) {
        cmd = new ShowCommand(line);
      } else {
        // unknown command
        view.takeMessage("Unknown command: " + line);
        view.displayTextInBuffer();
        continue;
      }

      // execute into the view
      cmd.execute(model, view);
      // flush what the command wrote
      view.displayTextInBuffer();
    }
  }
}
