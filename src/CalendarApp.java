import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import controller.CalendarController;
import model.CalendarManager;

public class CalendarApp {

  public static void main(String[] args) {
    try {
      run(args, System.in, System.out);
    } catch (IllegalArgumentException | IOException e) {
      System.err.println("ERROR: " + e.getMessage());
      System.exit(1);
    }
  }

  /**
   * Core entry-point. Throws instead of exiting on bad args, so tests can catch failures.
   */
  static void run(String[] args, InputStream in, Appendable out)
          throws IOException {
    String usage = "Usage: java CalendarApp --mode <interactive|headless> [commandsFile]";

    if (args.length < 2) {
      throw new IllegalArgumentException("Not enough arguments.\n" + usage);
    }
    if (!args[0].equalsIgnoreCase("--mode")) {
      throw new IllegalArgumentException("First argument must be --mode\n" + usage);
    }

    String mode = args[1].toLowerCase();
    CalendarManager mgr = new CalendarManager();

    if ("interactive".equals(mode)) {
      new CalendarController(mgr, new InputStreamReader(in), out).go();

    } else if ("headless".equals(mode)) {
      if (args.length < 3) {
        throw new IllegalArgumentException("Headless mode requires a commands file\n" + usage);
      }
      String file = args[2];
      try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
        new CalendarController(mgr, reader, out).go();
      } catch (FileNotFoundException e) {
        throw new IllegalArgumentException("File not found: " + file);
      }

    } else {
      throw new IllegalArgumentException("Unknown mode \"" + args[1] + "\"\n" + usage);
    }
  }
}
