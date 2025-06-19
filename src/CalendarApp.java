
import java.io.FileReader;


import controller.CalendarController;
import controller.GuiController;
import model.CalendarManager;
import view.CalendarGuiView;

/**
 * The main program and entry point.
 */
public class CalendarApp {

  /**
   * The enetry point.
   * @param args command line args
   */
  public static void main(String[] args) throws Exception {
    CalendarManager mgr = new CalendarManager();

    if (args.length == 0) {                       // GUI
      CalendarGuiView gui = new CalendarGuiView();
      new GuiController(mgr, gui);                // controller wires itself up
      gui.setVisible(true);
      return;
    }

    if ("--mode".equals(args[0]) && args.length >= 2) {
      switch (args[1]) {
        case "interactive":
          new CalendarController(mgr, new java.io.InputStreamReader(System.in),
                  System.out).goo();
          return;

        case "headless":
          if (args.length != 3) {
            throw new IllegalArgumentException(
                    "Usage: --mode headless <script-file>");
          }
          try (FileReader r = new FileReader(args[2])) {
            new CalendarController(mgr, r, System.out).goo();
          }
          return;

        default:
          break;
      }
    }
    System.err.println("Invalid arguments. "
            + "Usage:\n  (no args) GUI\n  --mode interactive\n"
            + "  --mode headless <script>");
    System.exit(1);
  }
}
