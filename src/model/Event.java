package model;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 *
 */

// Documentation + Tests are needed for class and builder.
public class Event implements IEvent {

  // private static final ZoneId est = ZoneId.of("America/New_York");
  private static final IDateTimeFacade facade = new DateTimeFacade();

  public static class EventBuilder {
    private final String subject;
    private final LocalDate startDate;
    private final LocalTime startTime;
    private final EventLocation location; // optional
    private final LocalDate endDate; // optional
    private final LocalTime endTime; // optional
    private final String description; // optional
    private final EventStatus status; // optional
    private final String message;

    // no public constructor, as the .getBuilder() method is the entry point for construction.
    // if extra time, handle daylight savings
    private EventBuilder() {
      subject = null;
      startTime = null;
      status = null;
      message = null;
      description = null;
      endDate = null;
      endTime = null;
      location = null;
      startDate = null;
    }

    private EventBuilder(
            String subject, EventLocation location,
            LocalDate startDate, LocalTime startTime,
            LocalDate endDate, LocalTime endTime,
            String description, EventStatus status, String message) {
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

    public EventBuilder location(EventLocation location) {
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
      try {
        return new EventBuilder(
                this.subject, this.location,
                facade.dateOf(day, month, year), this.startTime,
                this.endDate, this.endTime, this.description,
                this.status, message
        );
      } catch (DateTimeException e) {
        throw new IllegalArgumentException(
                "Cannot set start date to: " + day + "-" + month + "-" + year
        );
      }

    }

    public EventBuilder startTime(int hour, int minute) {
      String message = "Start time set to: " + hour + ":" + minute;
      if (startDate == null) {
        message += " , but there's no start Date";
      }
      try {
        return new EventBuilder(
                this.subject, this.location,
                this.startDate, facade.timeOf(hour, minute),
                this.endDate, this.endTime, this.description,
                this.status, message
        );
      } catch (DateTimeException e) {
        throw new IllegalArgumentException("Cannot set start time to: " + hour + ":" + minute);
      }

    }

    public EventBuilder endTime(int hour, int minute) {
      String message = "End time set to: " + hour + ":" + minute;
      if (endDate == null) {
        message += " , but there's no end date";
      }
      try {
        return new EventBuilder(
                this.subject, this.location,
                this.startDate, this.startTime,
                this.endDate, facade.timeOf(hour, minute),
                this.description, this.status, message
        );
      } catch (DateTimeException e) {
        throw new IllegalArgumentException("cannot set end time to: " + hour + ":" + minute);
      }

    }

    public EventBuilder endDate(int day, int month, int year) {
      String message = "End date set to: " + day + "-" + month + "-" + year;
      if (endTime == null) {
        message += " , but there's no end time";
      }
      try {
        return new EventBuilder(
                this.subject, this.location,
                this.startDate, this.startTime,
                facade.dateOf(day, month, year), this.endTime, this.description,
                this.status, message
        );
      } catch (DateTimeException e) {
        throw new IllegalArgumentException(
                "Cannot set end date to: " + day + "-" + month + "-" + year
        );
      }

    }

    public EventBuilder status(EventStatus status) {
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
      if (startDate == null || startTime == null || subject == null) {
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
                LocalDate.of(defaultYear, defaultMonth, defaultDay), LocalTime.of(17, 0),
                this.description, this.status, this.message
        ).build();
      }

      if (startDate.equals(endDate)) {
        if (facade.isAfter(startTime, endTime)) {
          throw new IllegalArgumentException(
                  "Start time cannot be after end time when their dates are the same"
          );
        }
      } else if (facade.isAfter(startDate, endDate)) {
        throw new IllegalArgumentException(
                "Invalid Start and end dates: " + startDate + ", " + endDate
        );
      }

      return new Event(subject, location, status,
              startDate, startTime, endDate, endTime, description);
    }
  }

  private final String subject;
  private final LocalDate startDate;
  private final LocalTime startTime;
  private final LocalDate endDate; // optional
  private final LocalTime endTime; // optional
  private final EventLocation location; // optional
  private final EventStatus status; // optional
  private final String description; // optional

  private Event(
          String subject,
          EventLocation location,
          EventStatus status,
          LocalDate startDate,
          LocalTime startTime,
          LocalDate endDate,
          LocalTime endTime,
          String description
  ) {
    this.subject = subject;
    this.location = location;
    this.status = status;
    this.startDate = startDate;
    this.startTime = startTime;
    this.endDate = endDate;
    this.endTime = endTime;
    this.description = description;
  }

  public static EventBuilder getBuilder() {
    return new EventBuilder();
  }

  @Override
  public String getSubject() {
    return subject;
  }

  @Override
  public LocalDate getStartDate() {
    return startDate;
  }

  @Override
  public LocalTime getStartTime() {
    return startTime;
  }

  @Override
  public LocalDate getEndDate() {
    return endDate;
  }

  @Override
  public LocalTime getEndTime() {
    return endTime;
  }

  @Override
  public EventLocation getLocation() {
    return location;
  }

  @Override
  public EventStatus getStatus() {
    return status;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public boolean isAllDayEvent() {
    return startDate.equals(endDate) && startTime.getHour() <= 8 && endTime.getHour() >= 17;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o instanceof Event) {
      Event other = (Event) o;
      return (subject.equals(other.subject)
              && facade.dateEquals(startDate, other.startDate)
              && facade.timeEquals(startTime, other.startTime)
              && facade.dateEquals(endDate, other.endDate)
              && facade.timeEquals(endTime, other.endTime));
    }
    return false;
  }

  @Override
  public int hashCode() {
    return subject.hashCode() + startDate.hashCode() + startTime.hashCode()
            + endDate.hashCode() + endTime.hashCode();
  }
}