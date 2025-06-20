package view;

import javax.swing.*;
import java.awt.Component;

/**
 * A class used for sending messages to the GuiVIew implementations.
 */
public final class GuiMessages {
  /**
   * Shows an error message on the GUI.
   * @param parent the view which should receive the error
   * @param msg the message to be displayed.
   */
  public static void error(Component parent, String msg) {
    JOptionPane.showMessageDialog(parent, msg,
            "Error", JOptionPane.ERROR_MESSAGE);
  }

  private GuiMessages() { }  //prevent instantiation
}
