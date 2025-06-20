package view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A Simple Implementation of the ITextView Interface. This class allows output to be decoupled from
 * one particular source.
 */
public class TextView implements ITextView {
  private final List<String> textBuffer;
  private final Appendable out;

  /**
   * Creates a new TextView object.
   *
   * @param out The output where messages will be printed.
   */
  public TextView(Appendable out) {
    this.textBuffer = new ArrayList<>();
    this.out = out;
  }

  @Override
  public void takeMessage(String message) {
    textBuffer.add(message);
  }

  @Override
  public void clearTextBuffer() {
    textBuffer.clear();
  }

  @Override
  public void displayTextInBuffer() throws IOException {
    for (String text : textBuffer) {
      out.append(text).append("\n");
    }
  }

  @Override
  public List<String> getTextInBuffer() {
    return List.copyOf(textBuffer);
  }
}
