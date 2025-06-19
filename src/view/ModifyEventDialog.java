package view;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Calendar;

import model.IEvent;

public class ModifyEventDialog extends JDialog {
  private final JTextField subjectField = new JTextField(20);
  private final JSpinner   startDateSpin;
  private final JSpinner   endDateSpin;
  private final JSpinner   startTimeSpin;
  private final JSpinner   endTimeSpin;
  private boolean ok = false;

  public ModifyEventDialog(Window owner, IEvent event) {
    super(owner, "Modify Event", ModalityType.APPLICATION_MODAL);
    setLayout(new BorderLayout());

    // --- prefill time and date ---
    java.util.Date startDate = java.sql.Date.valueOf(event.getStartDate());
    java.util.Date endDate   = java.sql.Date.valueOf(event.getEndDate());

    java.util.Date startTime = java.util.Date
            .from(event.getStartTime().atDate(event.getStartDate())
                    .atZone(java.time.ZoneId.systemDefault()).toInstant());

    java.util.Date endTime = java.util.Date
            .from(event.getEndTime().atDate(event.getEndDate())
                    .atZone(java.time.ZoneId.systemDefault()).toInstant());

    // --- spinners ---
    startDateSpin = new JSpinner(new SpinnerDateModel(startDate, null, null, Calendar.DAY_OF_MONTH));
    startDateSpin.setEditor(new JSpinner.DateEditor(startDateSpin, "dd-MM-yyyy"));

    endDateSpin = new JSpinner(new SpinnerDateModel(endDate, null, null, Calendar.DAY_OF_MONTH));
    endDateSpin.setEditor(new JSpinner.DateEditor(endDateSpin, "dd-MM-yyyy"));

    startTimeSpin = new JSpinner(new SpinnerDateModel(startTime, null, null, Calendar.MINUTE));
    startTimeSpin.setEditor(new JSpinner.DateEditor(startTimeSpin, "HH:mm"));

    endTimeSpin = new JSpinner(new SpinnerDateModel(endTime, null, null, Calendar.MINUTE));
    endTimeSpin.setEditor(new JSpinner.DateEditor(endTimeSpin, "HH:mm"));

    subjectField.setText(event.getSubject());

    // --- central form ---
    JPanel form = new JPanel(new GridLayout(5, 2, 5, 5));
    form.add(new JLabel("Subject:"));    form.add(subjectField);
    form.add(new JLabel("Start date:")); form.add(startDateSpin);
    form.add(new JLabel("Start time:")); form.add(startTimeSpin);
    form.add(new JLabel("End date:"));   form.add(endDateSpin);
    form.add(new JLabel("End time:"));   form.add(endTimeSpin);
    add(form, BorderLayout.CENTER);

    // --- buttons ---
    JButton okBtn = new JButton("OK");
    JButton cancelBtn = new JButton("Cancel");
    JPanel btnPanel = new JPanel();
    btnPanel.add(okBtn); btnPanel.add(cancelBtn);
    add(btnPanel, BorderLayout.SOUTH);

    okBtn.addActionListener(e -> {
      if (subjectField.getText().trim().isEmpty()) {
        JOptionPane.showMessageDialog(this, "Subject cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
      } else {
        ok = true;
        dispose();
      }
    });

    cancelBtn.addActionListener(e -> dispose());

    pack();
    setLocationRelativeTo(owner);
  }

  private static LocalDate toLocalDate(Object value) {
    java.util.Date util = (java.util.Date) value;
    return util.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
  }

  private static LocalTime toLocalTime(Object value) {
    java.util.Date util = (java.util.Date) value;
    return util.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalTime().withSecond(0).withNano(0);
  }

  public boolean isOk() { return ok; }
  public String getSubject() { return subjectField.getText().trim(); }
  public LocalDate getStartDate() { return toLocalDate(startDateSpin.getValue()); }
  public LocalDate getEndDate()   { return toLocalDate(endDateSpin.getValue()); }
  public LocalTime getStartTime() { return toLocalTime(startTimeSpin.getValue()); }
  public LocalTime getEndTime()   { return toLocalTime(endTimeSpin.getValue()); }
}
