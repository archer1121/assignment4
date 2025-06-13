package controller;

import controller.command.Command;
import controller.command.CopyEventCommand;
import controller.command.CopyEventsBetweenCommand;
import controller.command.CopyEventsOnDateCommand;
import controller.command.CreateCalendarCommand;
import controller.command.CreateCommand;
import controller.command.EditCalendarCommand;
import controller.command.EditCommand;
import controller.command.PrintCommand;
import controller.command.ShowCommand;
import controller.command.UseCalendarCommand;
import model.ICalendar;
import model.ICalendarManager;
import view.ITextView;
import view.TextView;

import java.io.IOException;
import java.io.Reader;
import java.util.Scanner;

/**
 * Controller.
 */
public class CalendarController {
  private final Scanner         scanner;
  private final ICalendarManager mgr;
  private String                 currentCalendarName;
  private final ITextView       view;

  /**
   * Controller.
   *
   * @param mgr  the calendar‐of‐calendars
   * @param in   where to read user commands
   * @param out  where to send output
   */
  public CalendarController(ICalendarManager mgr, Reader in, Appendable out) {
    this.mgr                  = mgr;
    this.scanner              = new Scanner(in);
    this.view                 = new TextView(out);
    this.currentCalendarName  = null;
  }

  /**
   * Main loop: read lines, dispatch to the right Command, render via TextView.
   */
  public void goo() throws IOException {
    while (scanner.hasNextLine()) {
      String line = scanner.nextLine().trim();
      view.clearTextBuffer();

      // ───────── Manager‐level commands ─────────
      if (line.startsWith("create calendar")) {
        new CreateCalendarCommand(line).execute(mgr, view);
        view.displayTextInBuffer();
        continue;
      }
      if (line.startsWith("edit calendar")) {
        new EditCalendarCommand(line).execute(mgr, view);
        view.displayTextInBuffer();
        continue;
      }
      if (line.startsWith("use calendar")) {
        UseCalendarCommand uc = new UseCalendarCommand(line);
        String name = uc.getCalendarName();
        // assert it exists
        mgr.getCalendar(name);
        currentCalendarName = name;
        view.takeMessage("Using calendar: " + name);
        view.displayTextInBuffer();
        continue;
      }

      // ───────── Copy commands (need a calendar selected) ─────────
      if (line.startsWith("copy event ")) {
        if (currentCalendarName == null) {
          view.takeMessage("ERROR: no calendar selected. Use \"use calendar --name <name>\"");
          view.displayTextInBuffer();
          continue;
        }
        Command c = new CopyEventCommand(line, mgr, currentCalendarName);
        try {
          c.execute(mgr.getCalendar(currentCalendarName), view);
        } catch (IllegalArgumentException iae) {
          view.takeMessage(iae.getMessage());
        }
        view.displayTextInBuffer();
        continue;
      }

      if (line.startsWith("copy events on ")) {
        if (currentCalendarName == null) {
          view.takeMessage("ERROR: no calendar selected. Use \"use calendar --name <name>\"");
          view.displayTextInBuffer();
          continue;
        }
        Command c = new CopyEventsOnDateCommand(line, mgr, currentCalendarName);
        try {
          c.execute(mgr.getCalendar(currentCalendarName), view);
        } catch (IllegalArgumentException iae) {
          view.takeMessage(iae.getMessage());
        }
        view.displayTextInBuffer();
        continue;
      }

      if (line.startsWith("copy events between ")) {
        if (currentCalendarName == null) {
          view.takeMessage("ERROR: no calendar selected. Use \"use calendar --name <name>\"");
          view.displayTextInBuffer();
          continue;
        }
        Command c = new CopyEventsBetweenCommand(line, mgr, currentCalendarName);
        try {
          c.execute(mgr.getCalendar(currentCalendarName), view);
        } catch (IllegalArgumentException iae) {
          view.takeMessage(iae.getMessage());
        }
        view.displayTextInBuffer();
        continue;
      }

      // ─────── All remaining commands are calendar‐scoped
      if (currentCalendarName == null) {
        view.takeMessage("ERROR: no calendar selected. Use \"use calendar --name <name>\"");
        view.displayTextInBuffer();
        continue;
      }
      ICalendar cal = mgr.getCalendar(currentCalendarName);

      Command cmd;
      if (line.startsWith("create event ")) {
        cmd = new CreateCommand(line);
      } else if (line.startsWith("edit event ")
              || line.startsWith("edit events ")
              || line.startsWith("edit series ")) {
        cmd = new EditCommand(line);
      } else if (line.startsWith("print events ")) {
        cmd = new PrintCommand(line);
      } else if (line.startsWith("show status on ")) {
        cmd = new ShowCommand(line);
      } else {
        view.takeMessage("Unknown command: " + line);
        view.displayTextInBuffer();
        continue;
      }

      // Run the calendar‐scoped command
      try {
        cmd.execute(cal, view);
      } catch (IllegalArgumentException iae) {
        // report malformed commands or lookup failures
        view.takeMessage(iae.getMessage());
      }
      view.displayTextInBuffer();
    }
  }
}
