package view;

import java.awt.BorderLayout;          // specific, not *
import java.time.LocalDate;
import java.util.Comparator;
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
    LocalDate cursor = state.cursorDate();
    List<IEvent> events = state.activeCalendar()
            .getScheduleInRange(cursor, cursor.plusDays(1))
            .stream()
            .filter(ev -> ev.getStartDate().equals(cursor))
            .sorted(Comparator.comparing(IEvent::getStartTime))
            .collect(java.util.stream.Collectors.toList());




    if (events.isEmpty()) {
      model.addElement("No events scheduled.");
      return;
    }

    model.addElement("Schedule for " + state.cursorDate().toString());
    model.addElement("--------------------------");

    events.stream()
            .sorted(Comparator.comparing(IEvent::getStartTime))
            .forEach(ev -> model.addElement(format(ev)));

    System.out.println(" Cursor date: " + state.cursorDate());

    List<IEvent> all = state.activeCalendar().getEvents();
    System.out.println("Total events in calendar: " + all.size());
    for (IEvent ev : all) {
      System.out.println(" - " + ev.getSubject() + " at " + ev.getStartDate());
    }

  }


  private String format(IEvent e) {
    return String.format("%s  %s–%s  %s",
            e.getStartDate(), e.getStartTime(),
            e.getEndTime(), e.getSubject());
  }

}
