package view;

import model.IEvent;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;

public class CalendarGuiView extends JFrame implements IGuiView {
  private final SchedulePanel schedulePanel;
  private IGuiView.Features features;

  private JComboBox<String> calendarBox;
  private JButton newCalBtn, delCalBtn;
  private JSpinner dateSpinner;
  private JButton jumpBtn, newBtn;

  public CalendarGuiView() {
    super("My Calendar");
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setLayout(new BorderLayout());

    // ─── calendar toolbar ─────────────────────────
    JToolBar calBar = new JToolBar();
    calBar.setFloatable(false);
    calendarBox = new JComboBox<>();
    newCalBtn = new JButton("New Calendar");
    delCalBtn = new JButton("Delete");
    calBar.add(new JLabel("Calendar: "));
    calBar.add(calendarBox);
    calBar.addSeparator();
    calBar.add(newCalBtn);
    calBar.add(delCalBtn);
    add(calBar, BorderLayout.NORTH);

    // ─── date toolbar ─────────────────────────
    JToolBar bar = new JToolBar();
    bar.setFloatable(false);
    dateSpinner = new JSpinner(new SpinnerDateModel(
            java.sql.Date.valueOf(LocalDate.now()), null, null, java.util.Calendar.DAY_OF_MONTH));
    dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "dd-MM-yyyy"));
    jumpBtn = new JButton("Go");
    newBtn = new JButton("New Event");
    bar.add(dateSpinner);
    bar.add(jumpBtn);
    bar.addSeparator();
    bar.add(newBtn);
    add(bar, BorderLayout.PAGE_START);

    // ─── schedule panels ───────────────────────
    schedulePanel = new SchedulePanel();
    add(schedulePanel, BorderLayout.CENTER);

    // ─── wire listeners ────────────────────────
    jumpBtn.addActionListener(e ->
            features.jumpTo(toLocal(dateSpinner)));
    newBtn.addActionListener(e -> {
      EventFormDialog dlg = new EventFormDialog(this);
      dlg.setVisible(true);
      if (dlg.isOk()) {
        features.createEvent(
                dlg.getSubject(),
                dlg.getStartTime(), dlg.getStartDate(),
                dlg.getEndTime(), dlg.getEndDate());
      }
    });

    calendarBox.addActionListener(e -> {
      String name = (String) calendarBox.getSelectedItem();
      if (name != null) features.switchToCalendar(name);
    });
    newCalBtn.addActionListener(e -> {
      JPanel panel = new JPanel(new GridLayout(2, 1));
      JTextField nameField = new JTextField();
      JComboBox<String> zoneBox = new JComboBox<>(ZoneId.getAvailableZoneIds().stream()
              .sorted()
              .toArray(String[]::new));
      zoneBox.setSelectedItem(ZoneId.systemDefault().getId());

      panel.add(new JLabel("Calendar Name:"));
      panel.add(nameField);
      panel.add(new JLabel("Time Zone:"));
      panel.add(zoneBox);

      int result = JOptionPane.showConfirmDialog(this, panel,
              "Create New Calendar", JOptionPane.OK_CANCEL_OPTION);

      if (result == JOptionPane.OK_OPTION) {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
          GuiMessages.error(this, "Calendar name cannot be empty.");
          return;
        }
        try {
          ZoneId zone = ZoneId.of((String) zoneBox.getSelectedItem());
          features.createCalendar(name, zone);
        } catch (Exception ex) {
          GuiMessages.error(this, "Invalid time zone.");
        }
      }
    });

    delCalBtn.addActionListener(e -> {
      String name = (String) calendarBox.getSelectedItem();
      if (name != null) {
        int choice = JOptionPane.showConfirmDialog(
                this, "Delete calendar \"" + name + "\"?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
          features.removeCalendar(name);
        }
      }
    });

    setSize(800, 600);
    setLocationRelativeTo(null);
  }

  @Override
  public void setFeatures(Features f) {
    this.features = f;
    schedulePanel.setFeatures(f);
  }

  @Override
  public void refresh() {
    schedulePanel.refresh();
    // update calendar drop-down
    CalendarAppState state = CalendarAppState.get();
    java.util.List<String> names = state.mgr().getCalendars();
    calendarBox.setModel(new DefaultComboBoxModel<>(names.toArray(new String[0])));
    calendarBox.setSelectedItem(state.currentCalendarName());
  }

  private static LocalDate toLocal(JSpinner spin) {
    java.util.Date d = (java.util.Date) spin.getValue();
    return new java.sql.Date(d.getTime()).toLocalDate();
  }
}
