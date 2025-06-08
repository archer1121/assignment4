package view;

import java.io.IOException;
import java.util.List;

/**
 * A simple TextView Interface which allows printing messages to a text output.
 */
public interface ITextView {

  /**
   * Takes and stores a message.
   *
   * @param message The message to be stored.
   */
  void takeMessage(String message);

  /**
   * Clears all stored messages.
   */
  void clearTextBuffer();

  /**
   * Displays all stored messages.
   */
  void displayTextInBuffer() throws IOException;

  /**
   * Returns the text currently stored in this View
   *
   * @return List of all stored messages.
   */
  List<String> getTextInBuffer();

}
