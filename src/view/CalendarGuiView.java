package view;

import java.awt.*;
import java.time.LocalDate;
import java.util.Calendar;
import java.sql.Date;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JSpinner;
import javax.swing.JToolBar;
import javax.swing.SpinnerDateModel;

public class CalendarGuiView extends JFrame implements IGuiView {
  private final SchedulePanel schedulePanel;
  private Features features;

  public CalendarGuiView() {
    super("My Calendar");
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setLayout(new BorderLayout());

    // top toolbar: pick date + “New Event” button
    JToolBar bar = new JToolBar();
    JSpinner dateSpinner = new JSpinner(
            new SpinnerDateModel(java.sql.Date.valueOf(LocalDate.now()),
                    null, null, Calendar.DAY_OF_MONTH));
    dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "dd-MM-yyyy"));
    JButton jumpBtn = new JButton("Go");
    JButton newBtn  = new JButton("New Event");
    bar.add(dateSpinner); bar.add(jumpBtn); bar.addSeparator(); bar.add(newBtn);
    add(bar, BorderLayout.NORTH);

    // center: schedule view (card layout allows future week/day views)
    schedulePanel = new SchedulePanel();
    add(schedulePanel, BorderLayout.CENTER);

    // listeners -> Features
    jumpBtn.addActionListener(e ->
            features.jumpTo(toLocal(dateSpinner)));
    newBtn.addActionListener(e -> {
      EventFormDialog dlg = new EventFormDialog(this);
      dlg.setVisible(true);
      if (dlg.isOk()) {
        features.createEvent(
                dlg.getSubject(),
                dlg.getStartTime(),dlg.getStartDate(),
                dlg.getEndTime(), dlg.getEndDate()
        );
      }
    });

    setSize(600, 500);
    setLocationRelativeTo(null);
  }

  // ----- IGuiView -----
  @Override public void setFeatures(Features f) { this.features = f; }
  @Override public void refresh() { schedulePanel.refresh(); }

  private static LocalDate toLocal(JSpinner spin) {
    java.util.Date d = (java.util.Date) spin.getValue();
    return d.toInstant()
            .atZone(java.time.ZoneId.systemDefault())
            .toLocalDate();
  }

}
