import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class CalendarApp {

  public static void main(String[] args) {

    String usage = "Usage: java CalendarApp.java --mode <interactive|headless> [commandsFile]";
    // 1. Make sure we have at least two tokens: "--mode" and its value
    if (args.length < 2) {
      System.err.println("ERROR: Not enough arguments.");
      System.err.println(usage);
      System.exit(1);
    }

    // 2. Check that args[0] is "--mode" (case‐insensitive)
    if (!args[0].equalsIgnoreCase("--mode")) {
      System.err.println("ERROR: First argument must be --mode");
      System.err.println(usage);
      System.exit(1);
    }

    // 3. Grab the mode value from args[1]
    String modeValue = args[1].toLowerCase();  // either "interactive" or "headless"

    if (modeValue.equals("interactive")) { //-------------------------------------------------------

      System.out.println("interactive mode bruh"); // run it here
    }
    else if (modeValue.equals("headless")) { // need to intake a command file ----------------------
      // In headless mode, we expect a third argument: the path to commands.txt
      if (args.length < 3) {
        System.err.println("ERROR: Headless mode requires a commands file after --mode headless");
        System.err.println(usage);
        System.exit(1);
      }
      String commandsFile = args[2];
      System.out.println("headless mode bruh" + commandsFile); //run it here

      try (BufferedReader reader = new BufferedReader(new FileReader(commandsFile))) {

        String line;
        while ((line = reader.readLine()) != null) {
          // 'line' is one command string from the file
          System.out.println("Read command: " + line);


        }
      }
      catch (FileNotFoundException e) {
        System.err.println("File not found: " + commandsFile);
        System.exit(1);
      }
      catch (IOException e) {
        System.err.println("I/O error reading file: " + e.getMessage());
        System.exit(1);
      }
    }
    else {
      System.err.println("ERROR: Unknown mode \"" + args[1] + "\". Must be either \"interactive\" or \"headless\".");
      System.err.println(usage);
      System.exit(1);
    }

  }

}
