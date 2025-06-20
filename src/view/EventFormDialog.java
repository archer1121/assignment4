package view;

import java.awt.Frame;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Calendar;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.JButton;




public class EventFormDialog extends JDialog {
  private final JTextField subjectField = new JTextField(20);
  private final JSpinner   startSpin;
  private final JSpinner   endSpin;

  private final JSpinner startTimeSpin;
  private final JSpinner endTimeSpin;
  private boolean ok = false;            // set to true if user hit OK

  public EventFormDialog(Frame owner) {
    super(owner, "New Event", true);
    setLayout(new BorderLayout());

    //time spin
    // Create today’s date at 08:00 and 17:00
    java.util.Calendar cal = java.util.Calendar.getInstance();

    cal.set(Calendar.HOUR_OF_DAY, 8);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);
    java.util.Date startDefault = cal.getTime();

    cal.set(Calendar.HOUR_OF_DAY, 17);
    java.util.Date endDefault = cal.getTime();

    // Set spinners
    startTimeSpin = new JSpinner(new SpinnerDateModel(startDefault, null, null, Calendar.MINUTE));
    startTimeSpin.setEditor(new JSpinner.DateEditor(startTimeSpin, "HH:mm"));

    endTimeSpin = new JSpinner(new SpinnerDateModel(endDefault, null, null, Calendar.MINUTE));
    endTimeSpin.setEditor(new JSpinner.DateEditor(endTimeSpin, "HH:mm"));



    //date spinners
    java.sql.Date today = java.sql.Date.valueOf(LocalDate.now());
    startSpin = new JSpinner(new SpinnerDateModel(today, null, null,
            Calendar.DAY_OF_MONTH));
    endSpin   = new JSpinner(new SpinnerDateModel(today, null, null,
            Calendar.DAY_OF_MONTH));

    startSpin.setEditor(new JSpinner.DateEditor(startSpin, "dd-MM-yyyy"));
    endSpin.setEditor(new JSpinner.DateEditor(endSpin, "dd-MM-yyyy"));


    //central form
    JPanel form = new JPanel(new GridLayout(5, 2, 5, 5));
    form.add(new JLabel("Subject:"));    form.add(subjectField);
    form.add(new JLabel("Start date:")); form.add(startSpin);
    form.add(new JLabel("Start time:")); form.add(startTimeSpin);
    form.add(new JLabel("End date:"));   form.add(endSpin);
    form.add(new JLabel("End time:"));   form.add(endTimeSpin);
    add(form, BorderLayout.CENTER);



    JButton okBtn  = new JButton("OK");
    JButton cancel = new JButton("Cancel");
    JPanel btnBox  = new JPanel();
    btnBox.add(okBtn); btnBox.add(cancel);
    add(btnBox, BorderLayout.SOUTH);

    okBtn.addActionListener(e -> {
      if (getSubject().isEmpty()) {
        JOptionPane.showMessageDialog(this,
                "Subject cannot be empty.",
                "Input Error",
                JOptionPane.ERROR_MESSAGE);
        return;
      }
      ok = true;
      dispose();
    });

    cancel.addActionListener(e -> dispose());

    pack();
    setLocationRelativeTo(owner);
  }

  public boolean isOk()      { return ok; }






  private static LocalDate toLocalDate(Object value) {
    java.util.Date util = (java.util.Date) value;
    return util.toInstant()
            .atZone(java.time.ZoneId.systemDefault())
            .toLocalDate();
  }

  private static java.time.LocalTime toLocalTime(Object value) {
    java.util.Date util = (java.util.Date) value;
    return util.toInstant()
            .atZone(java.time.ZoneId.systemDefault())
            .toLocalTime()
            .withSecond(0).withNano(0);
  }
  // --- getters the controller uses ---
  public String getSubject()      { return subjectField.getText().trim(); }
  // These return LocalDate
  public LocalDate getStartDate() { return toLocalDate(startSpin.getValue()); }
  public LocalDate getEndDate()   { return toLocalDate(endSpin.getValue());   }

  // These return LocalTime (once you add time spinners, if needed)
  public LocalTime getStartTime() { return toLocalTime(startTimeSpin.getValue()); }
  public LocalTime getEndTime()   { return toLocalTime(endTimeSpin.getValue()); }




}
