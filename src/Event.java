import java.security.MessageDigest;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 *
 */

// Documentation + Tests are needed for class and builder.
public class Event {
  public static EventBuilder getBuilder() {
    return new EventBuilder();
  }
  private static final ZoneId est = ZoneId.of("America/New_York");
  public static class EventBuilder {
    private final  String subject;
    private final LocalDate startDate;
    private final LocalTime startTime;
    private final Location location; // optional
    private final LocalDate endDate; // optional
    private final LocalTime endTime; // optional
    private final String description; // optional
    private final Status status; // optional
    private final String message;



    // no public constructor, as the .getBuilder() method is the entry point for construction.
    private EventBuilder() {
      this.subject = null;
      this.startTime = null;
      this.status = null;
      this.message = "";
      description = "";
      endDate = null;
      endTime = null;
      location = null;
      startDate = null;
    }

    private EventBuilder(
            String subject, Location location,
            LocalDate startDate, LocalTime startTime,
            LocalDate endDate, LocalTime endTime,
            String description, Status status, String message) {
      this.subject = subject;
      this.location = location;
      this.startDate = startDate;
      this.startTime = startTime;
      this.endDate = endDate;
      this.endTime = endTime;
      this.description = description;
      this.status = status;
      this.message = message;
    }


    // these setters also need a try catch for the return block,
    // looking for LocalDate and LocalTime exceptions.

    public EventBuilder description(String description) {
      String message = "Description set to: " + description;
      return new EventBuilder(
              this.subject, this.location,
              this.startDate, this.startTime,
              this.endDate, this.endTime, description,
              this.status, message
      );
    }

    public EventBuilder subject(String subject) {
      String message = "Subject set to: " + subject;
      return new EventBuilder(
              subject, this.location,
              this.startDate, this.startTime,
              this.endDate, this.endTime, this.description,
              this.status, message
      );
    }

    public EventBuilder location(Location location) {
      String message;
      message = "location set to: " + location;
      return new EventBuilder(
              this.subject, location,
              this.startDate, this.startTime,
              this.endDate, this.endTime, this.description,
              this.status, message
      );
    }

    public EventBuilder startDate(int day, int month, int year) {
      String message = "Start date set to: " + day + "-" + month + "-" + year;
      if (startTime == null) {
        message += " , but there's no start time";
      }
      return new EventBuilder(
              this.subject, this.location,
              LocalDate.of(day, month, year), this.startTime,
              this.endDate, this.endTime, this.description,
              this.status, message
      );
    }

    public EventBuilder startTime(int hour, int minute) {
      String message = "Start time set to: " + hour + ":" + minute;
      if (startDate == null) {
        message += " , but there's no start Date";
      }
      return new EventBuilder(
              this.subject, this.location,
              this.startDate, LocalTime.of(hour, minute),
              this.endDate, this.endTime, this.description,
              this.status, message
      );
    }

    public EventBuilder endTime(int hour, int minute) {
      String message = "End time set to: " + hour + ":" + minute;
      if (endDate == null) {
        message += " , but there's no end date";
      }
      return new EventBuilder(
              this.subject, this.location,
              this.startDate, this.startTime,
              this.endDate, LocalTime.of(hour, minute), this.description,
              this.status, message
      );
    }

    public EventBuilder endDate(int day, int month, int year) {
      String message = "End date set to: " + day + "-" + month + "-" + year;
      if (endTime == null) {
        message += " , but there's no end time";
      }
      return new EventBuilder(
              this.subject, this.location,
              this.startDate, this.startTime,
              LocalDate.of(day, month, year), this.endTime, this.description,
              this.status, message
      );
    }

    public EventBuilder status(Status status) {
      String message = "Status set to " + status;
      return new EventBuilder(
              this.subject, this.location,
              this.startDate, this.startTime,
              this.endDate, this.endTime, this.description,
              status, message
      );
    }

    public String getMessage() {
      return message;
    }

    public Event build() {
      if (startDate == null || startTime == null || subject == null ) {
        throw new IllegalArgumentException(
                "The start date, time, and the subject of the event must all be set"
        );
      }

      if (endDate == null || endTime == null) {
        int defaultDay = startDate.getDayOfMonth();
        int defaultMonth = startDate.getMonthValue();
        int defaultYear = startDate.getYear();
        return new EventBuilder(
                this.subject, this.location,
                this.startDate, LocalTime.of(8, 0),
                LocalDate.of(defaultYear, defaultMonth, defaultDay), LocalTime.of(5, 0),
                this.description, this.status, this.message
        ).build();
      }

      ZonedDateTime startDateAndTime = ZonedDateTime.of(startDate, startTime, est);
      ZonedDateTime endDateAndTime = ZonedDateTime.of(endDate, endTime, est);

      return new Event(subject, location, status, startDateAndTime, endDateAndTime, description);
    }
  }

  private final String subject;
  private final Location location;
  private final Status status;
  private final ZonedDateTime startDateAndTime;
  private final ZonedDateTime endDateAndTime; // optional
  private final String description; // optional

  public enum Location {
    PHYSICAL,
    ONLINE
  }

  public enum Status {
    PUBLIC,
    PRIVATE,
  }


  private Event(
    String subject, Location location, Status status,
    ZonedDateTime startDateAndTime, ZonedDateTime endDateAndTime,
    String description
  ) {
    this.subject = subject;
    this.location = location;
    this.status = status;
    this.startDateAndTime = startDateAndTime;
    this.endDateAndTime = endDateAndTime;
    this.description = description;
  }



}