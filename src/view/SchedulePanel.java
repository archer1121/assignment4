package view;

import model.IEvent;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.ArrayList;

/** Panel that shows the day’s schedule and lets you double-click to edit events. */
class SchedulePanel extends JPanel {
  private final DefaultListModel<String> model = new DefaultListModel<>();
  private final JList<String> list = new JList<>(model);
  private final List<IEvent> events = new ArrayList<>();
  private IGuiView.Features features;

  void setFeatures(IGuiView.Features f) {
    this.features = f;
  }

  SchedulePanel() {
    setLayout(new BorderLayout());
    add(new JScrollPane(list), BorderLayout.CENTER);

    list.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2 && features != null) {
          int idx = list.locationToIndex(e.getPoint());
          int evIndex = idx - 2;
          if (evIndex >= 0 && evIndex < events.size()) {
            IEvent selected = events.get(evIndex);
            ModifyEventDialog dlg = new ModifyEventDialog(
                    SwingUtilities.getWindowAncestor(SchedulePanel.this), selected);
            dlg.setVisible(true);
            if (dlg.isOk()) {
              features.modifyEvent(
                      selected,
                      dlg.getSubject(),
                      dlg.getStartTime(), dlg.getStartDate(),
                      dlg.getEndTime(), dlg.getEndDate());
            }
            CalendarAppState.get().features().jumpTo(dlg.getStartDate());

          }
        }
      }
    });
  }

  void refresh() {
    model.clear();
    events.clear();

    CalendarAppState state = CalendarAppState.get();
    LocalDate cursor = state.cursorDate();

    List<IEvent> todays = state.activeCalendar()
            .getScheduleInRange(cursor, cursor.plusDays(1))
            .stream()
            .filter(ev -> ev.getStartDate().equals(cursor))
            .sorted(Comparator.comparing(IEvent::getStartTime))
            .collect(Collectors.toList());
    events.addAll(todays);

    if (events.isEmpty()) {
      model.addElement("No events scheduled.");
    } else {
      model.addElement("Schedule for " + cursor);
      model.addElement("--------------------------");
      events.forEach(ev -> model.addElement(format(ev)));
    }
  }

  private String format(IEvent e) {
    return String.format("%s  %s–%s  %s",
            e.getStartDate(), e.getStartTime(),
            e.getEndTime(), e.getSubject());
  }
}
