package controller.command;

import model.ICalendar;
import view.ITextView;

/**
 * Command.
 */
public interface Command {
  public void execute(ICalendar model, ITextView view);

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

  /**
   * Gets next word.
   * @param wordBefore before word
   * @param command command
   * @return string
   * @throws IllegalArgumentException uhoh
   */
  static String getWordAfter(String wordBefore, String command) throws IllegalArgumentException {
    if (wordBefore == null || wordBefore.isEmpty()) {
      throw new IllegalArgumentException("wordBefore must be non‐empty");
    }
    if (command == null) {
      throw new IllegalArgumentException("command must not be null");
    }

    // 1) Find the index of the first occurrence of wordBefore
    int beforeIdx = command.indexOf(wordBefore);
    if (beforeIdx < 0) {
      throw new IllegalArgumentException(
              String.format("The token \"%s\" was not found in: \"%s\"", wordBefore, command)
      );
    }

    // 2) Advance past `wordBefore`
    int cursor = beforeIdx + wordBefore.length();

    // 3) Skip any whitespace after wordBefore
    while (cursor < command.length() && Character.isWhitespace(command.charAt(cursor))) {
      cursor++;
    }

    // 4) If we've reached the end of the string, there's no word after
    if (cursor >= command.length()) {
      throw new IllegalArgumentException(
              String.format("No word found after \"%s\" in: \"%s\"", wordBefore, command)
      );
    }

    // 5) Now `cursor` is at the start of the next word; find its end
    int startOfNextWord = cursor;
    while (cursor < command.length() && !Character.isWhitespace(command.charAt(cursor))) {
      cursor++;
    }
    int endOfNextWord = cursor;

    // 6) Extract and return the word
    return command.substring(startOfNextWord, endOfNextWord);
  }
}
