package model;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;


/**
 * An Implementation of IEvent. Events Are Immutable, final structures. All "modifying"
 * methods called in this class return a new object reflecting the changes.
 */
public class Event implements IEvent {
  private static final IDateTimeFacade facade = new DateTimeFacade();

  public static class EventComparator implements Comparator<IEvent> {
    @Override
    public int compare(IEvent o1, IEvent o2) {
      return o1.getStartDate().compareTo(o2.getStartDate());
    }
  }

  /**
   * A Builder class for constructing Events and making the construction process less error-prone.
   */
  public static class EventBuilder {
    private final String subject;
    private final LocalDate startDate;
    private final LocalTime startTime;
    private final EventLocation location; // optional
    private final LocalDate endDate; // optional
    private final LocalTime endTime; // optional
    private final String description; // optional
    private final EventStatus status; // optional

    private EventBuilder() {
      subject = null;
      startTime = null;
      status = null;
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
            String description, EventStatus status) {
      this.subject = subject;
      this.location = location;
      this.startDate = startDate;
      this.startTime = startTime;
      this.endDate = endDate;
      this.endTime = endTime;
      this.description = description;
      this.status = status;
    }


    // these setters also need a try catch for the return block,
    // looking for LocalDate and LocalTime exceptions.

    /**
     * Sets the event's description.
     * @param description the description.
     * @return EventBuilder
     */
    public EventBuilder description(String description) {
      String message = "Description set to: " + description;
      return new EventBuilder(
              this.subject, this.location,
              this.startDate, this.startTime,
              this.endDate, this.endTime, description,
              this.status
      );
    }
    /**
     * Sets the event's subject.
     * @param subject the subject.
     * @return EventBuilder
     */
    public EventBuilder subject(String subject) {
      return new EventBuilder(
              subject, this.location,
              this.startDate, this.startTime,
              this.endDate, this.endTime, this.description,
              this.status
      );
    }
    /**
     * Sets the event's location.
     * @param location the location.
     * @return EventBuilder
     */
    public EventBuilder location(EventLocation location) {
      return new EventBuilder(
              this.subject, location,
              this.startDate, this.startTime,
              this.endDate, this.endTime, this.description,
              this.status
      );
    }
    /**
     * Sets the events start date.
     * @param day The day.
     * @param month The month.
     * @param year The year.
     * @return EventBuilder
     */
    public EventBuilder startDate(int day, int month, int year) {
      try {
        return new EventBuilder(
                this.subject, this.location,
                facade.dateOf(day, month, year), this.startTime,
                this.endDate, this.endTime, this.description,
                this.status
        );
      } catch (DateTimeException e) {
        throw new IllegalArgumentException(
                "Cannot set start date to: " + day + "-" + month + "-" + year
        );
      }

    }
    /**
     * Sets the event's start time.
     * @param hour The hour.
     * @param minute The minute.
     * @return EventBuilder
     */
    public EventBuilder startTime(int hour, int minute) {

      try {
        return new EventBuilder(
                this.subject, this.location,
                this.startDate, facade.timeOf(hour, minute),
                this.endDate, this.endTime, this.description,
                this.status
        );
      } catch (DateTimeException e) {
        throw new IllegalArgumentException("Cannot set start time to: " + hour + ":" + minute);
      }

    }
    /**
     * Sets the event's end time.
     * @param hour The hour.
     * @param minute The minute.
     * @return EventBuilder
     */
    public EventBuilder endTime(int hour, int minute) {

      try {
        return new EventBuilder(
                this.subject, this.location,
                this.startDate, this.startTime,
                this.endDate, facade.timeOf(hour, minute),
                this.description, this.status
        );
      } catch (DateTimeException e) {
        throw new IllegalArgumentException("cannot set end time to: " + hour + ":" + minute);
      }

    }
    /**
     * Sets the event's end date.
     * @param day The day.
     * @param month The month.
     * @param year The year.
     * @return EventBuilder
     */
    public EventBuilder endDate(int day, int month, int year) {
      try {
        return new EventBuilder(
                this.subject, this.location,
                this.startDate, this.startTime,
                facade.dateOf(day, month, year), this.endTime, this.description,
                this.status
        );
      } catch (DateTimeException e) {
        throw new IllegalArgumentException(
                "Cannot set end date to: " + day + "-" + month + "-" + year
        );
      }

    }
    /**
     * Sets the event's status.
     * @param status the status.
     * @return EventBuilder
     */
    public EventBuilder status(EventStatus status) {
      return new EventBuilder(
              this.subject, this.location,
              this.startDate, this.startTime,
              this.endDate, this.endTime, this.description,
              status
      );
    }

    /**
     * Constructs a new Event.
     * @return Event
     */
    public Event buildEvent() {
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
                this.description, this.status
        ).buildEvent();
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

  /**
   * Gets a new EventBuilder for safer construction of an event.
   * @return EventBuilder
   */
  public static EventBuilder getBuilder() {
    return new EventBuilder();
  }

  /**
   * Gets an EventBuilder which copies over the properties held by the given event.
   * @param event the event to be edited.
   * @return A new EventBuilder with fields set to those held by the given event.
   */
  public static EventBuilder editEvent(Event event) {
    return new EventBuilder(
            event.subject, event.location,
            event.startDate, event.startTime,
            event.endDate, event.endTime, event.description,
            event.status
    );
  }

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
  public String toString() {
    return String.format(
            "subject=%s_location=%s_status=%s_startDate=%s_startTime=%s_endDate=%s_endTime=%s",
            subject, location, status, startDate, startTime, endDate, endTime
    );
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