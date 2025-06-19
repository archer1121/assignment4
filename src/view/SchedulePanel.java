package view;

import java.awt.BorderLayout;          // specific, not *
import java.time.LocalDate;
import java.util.List;                 // ← pulls in java.util.List
import javax.swing.*;

import model.IEvent; // whatever package you put it in

class SchedulePanel extends JPanel {
  private final DefaultListModel<String> model = new DefaultListModel<>();
  private final JList<String> list = new JList<>(model);

  SchedulePanel() {
    setLayout(new BorderLayout());
    add(new JScrollPane(list), BorderLayout.CENTER);
  }

  /** Controller (via view.refresh()) calls this after model changes. */
  void refresh() {
    model.clear();
    CalendarAppState state = CalendarAppState.get();
    List<IEvent> events = state.activeCalendar()
            .getScheduleInRange(state.cursorDate(),
                    state.cursorDate().plusDays(9));

    events.stream()
            .limit(10)
            .forEach(ev -> model.addElement(format(ev)));
  }

  private String format(IEvent e) {
    return String.format("%s  %s–%s  %s",
            e.getStartDate(), e.getStartTime(),
            e.getEndTime(), e.getSubject());
  }
}
