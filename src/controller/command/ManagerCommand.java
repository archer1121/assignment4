package controller.command;

import model.ICalendarManager;
import view.ITextView;

/** Commands that operate on the calendar‐registry (ICalendarManager). */
public interface ManagerCommand {
  void execute(ICalendarManager mgr, ITextView view);
}
