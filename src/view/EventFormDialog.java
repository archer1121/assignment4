package view;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.Calendar;

public class EventFormDialog extends JDialog {
  private final JTextField subjectField = new JTextField(20);
  private final JSpinner   startSpin;
  private final JSpinner   endSpin;
  private boolean ok = false;            // set to true if user hit OK

  public EventFormDialog(Frame owner) {
    super(owner, "New Event", true);
    setLayout(new BorderLayout());

    // ---------- date spinners ----------
    java.sql.Date today = java.sql.Date.valueOf(LocalDate.now());
    startSpin = new JSpinner(new SpinnerDateModel(today, null, null,
            Calendar.DAY_OF_MONTH));
    startSpin.setEditor(new JSpinner.DateEditor(startSpin, "yyyy-MM-dd"));
    endSpin   = new JSpinner(new SpinnerDateModel(today, null, null,
            Calendar.DAY_OF_MONTH));
    endSpin.setEditor(new JSpinner.DateEditor(endSpin, "yyyy-MM-dd"));

    // ---------- central form ----------
    JPanel form = new JPanel(new GridLayout(3, 2, 5, 5));
    form.add(new JLabel("Subject:"));    form.add(subjectField);
    form.add(new JLabel("Start date:")); form.add(startSpin);
    form.add(new JLabel("End date:"));   form.add(endSpin);
    add(form, BorderLayout.CENTER);

    // ---------- buttons ----------
    JButton okBtn  = new JButton("OK");
    JButton cancel = new JButton("Cancel");
    JPanel btnBox  = new JPanel();
    btnBox.add(okBtn); btnBox.add(cancel);
    add(btnBox, BorderLayout.SOUTH);

    okBtn.addActionListener(e -> { ok = true; dispose(); });
    cancel.addActionListener(e -> dispose());

    pack();
    setLocationRelativeTo(owner);
  }

  /* ----- getters the controller needs ----- */
  public boolean isOk()      { return ok; }

  public String getSubject() { return subjectField.getText().trim(); }

  public LocalDate getStart() {
    java.sql.Date d = ((java.sql.Date) startSpin.getValue());
    return d.toLocalDate();
  }

  public LocalDate getEnd()   {
    java.sql.Date d = ((java.sql.Date) endSpin.getValue());
    return d.toLocalDate();
  }
}
