# Project 4

* This Calendar project allows for The creation of multiple calendars, events, and recurring events
which the user can interactively modify. Calendars in this project are used by the calendar manager
class so that users can create and modify multiple calendars as they wish.
* Among these calendars, The user may add events by specifying their subjects, and start/end times.
* Events can last multiple days, however series cannot have events that last more than one.
* Calendars can never have more than one event with all the same start/end dates, and subject.
* Our Implementation also comes with a view which is decoupled from any specific output source,
* allowing users to plug and play this program with theirs, and have a seamless IO experience.



changes/additions from part 1: 
* Added a timezone field to Calendar and getTimeZone() to ICalendar. 
This obeys encapsulation by associating a timeZone with the calendar 
that represents it, rather than having to keep track of multiple variables outside
the class.
* We did not add the name of a calendar to the calendar itself for the simple fact that the calendar
doesn't care about its name.
* All classes in this project are immutable, except for the series editor class which was meant 
to act as a mutable go-between for modifying and keeping the structure of an event series.
* There was a bug in the series editor, resulting in a need to modify old code. 
* other than the point above, that was the only place where old code needed to be modified.
