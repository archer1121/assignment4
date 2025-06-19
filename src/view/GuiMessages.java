package view;

import javax.swing.*;
import java.awt.Component;

public final class GuiMessages {
  public static void error(Component parent, String msg) {
    JOptionPane.showMessageDialog(parent, msg,
            "Error", JOptionPane.ERROR_MESSAGE);
  }

  private GuiMessages() { }  // prevent instantiation
}
