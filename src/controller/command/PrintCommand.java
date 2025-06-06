package controller.command;

import model.ICalendar;

public class PrintCommand implements Command{

  private String command;

  public PrintCommand(String command) {
    this.command = command;
  }
  @Override
  public void execute(ICalendar model) {
    String dateStringOnly;
    String dateTimeStringStarting;
    String dateTimeStringEnding;

    // case 1: print events on <dateString>
    if (command.contains("print events on ")
            && !command.contains(" to ")) {

      dateStringOnly = Command.getWordAfter("on", command);
//      LocalDate date = LocalDate.parse(dateStringOnly);
//
//      List<CalendarEvent> events = model.getEventsOn(date);
//      for (CalendarEvent ev : events) {
//        StringBuilder line = new StringBuilder("• ");
//        line.append(ev.getSubject());
//        if (!ev.isAllDay()) {
//          line.append(" ")
//                  .append(ev.getStart().toLocalTime())
//                  .append("–")
//                  .append(ev.getEnd().toLocalTime());
//        } else {
//          line.append(" (All day)");
//        }
//        if (ev.getLocation() != null && !ev.getLocation().isEmpty()) {
//          line.append(" @ ").append(ev.getLocation());
//        }
//        System.out.println(line.toString());
//      }
//      return;
//    }
//
//    // case 2: print events from <dateTime> to <dateTime>
//    else if (command.contains("print events from ")
//            && command.contains(" to ")) {
//
//      dateTimeStringStarting = Command.getWordAfter("from", command);
//      dateTimeStringEnding   = Command.getWordAfter("to", command);
//
//      LocalDateTime startDT = LocalDateTime.parse(dateTimeStringStarting);
//      LocalDateTime endDT   = LocalDateTime.parse(dateTimeStringEnding);
//
//      List<CalendarEvent> events = model.getEventsBetween(startDT, endDT);
//      for (CalendarEvent ev : events) {
//        StringBuilder line = new StringBuilder("• ");
//        line.append(ev.getSubject());
//        if (!ev.isAllDay()) {
//          line.append(" ")
//                  .append(ev.getStart().toLocalDateTime().toLocalTime())
//                  .append("–")
//                  .append(ev.getEnd().toLocalDateTime().toLocalTime());
//        } else {
//          line.append(" (All day)");
//        }
//        if (ev.getLocation() != null && !ev.getLocation().isEmpty()) {
//          line.append(" @ ").append(ev.getLocation());
//        }
//        System.out.println(line.toString());
//      }
//      return;
//    }

    throw new IllegalArgumentException("Malformed print command: \"" + command + "\"");
  }
  }
}
