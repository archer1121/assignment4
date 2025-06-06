package controller.command;

import model.ICalendar;

public interface Command {
  public void execute(ICalendar model);

  /**
   * Returns the remainder of the string after removing the first word and any
   * subsequent spaces. If there is no space (i.e. only one word), returns an empty string.
   */
  static String removeFirstWord(String input) {
    if (input == null) {
      return "";
    }
    String trimmed = input.trim();
    int spaceIdx = trimmed.indexOf(' ');
    if (spaceIdx < 0) {
      return "";                   // no spaces → nothing left after first word
    }
    // skip over that first space and any additional spaces that follow
    int nextIdx = spaceIdx + 1;
    while (nextIdx < trimmed.length() && trimmed.charAt(nextIdx) == ' ') {
      nextIdx++;
    }
    return trimmed.substring(nextIdx);
  }

  /**
   * Returns the first word of the given string.
   * A “word” is defined as everything up to (but not including) the first space.
   * If there is no space, the entire string is returned.
   */
  static String getFirstWord(String input) {
    if (input == null) {
      return "";
    }
    String trimmed = input.trim();
    int spaceIdx = trimmed.indexOf(' ');
    if (spaceIdx < 0) {
      return trimmed;              // no spaces → whole string is one word
    }
    return trimmed.substring(0, spaceIdx);
  }
}
