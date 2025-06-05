package view;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class TextView implements ITextView {
  private final List<String> textBuffer;
  private final PrintStream out;

  public TextView(PrintStream out) {
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
  public void displayTextInBuffer() {
    out.println(textBuffer);
  }
}
