package com.management.HumanResources.model;

import java.time.*;
import java.time.temporal.*;
import java.util.Locale;

import lombok.Data;

@Data
public class TimeOff {
    private LocalDateTime start;
    private LocalDateTime end;
    private boolean approved;
    private boolean reviewed;

    /**
     * Returns the zero-based time off start day of the week where zero is Monday.
     */
    public int getStartDayOfWeek() {
        return start.getDayOfWeek().getValue() - 1; // -1 because Mon = 1 and is the first day of the week.
    }

    /**
     * Returns the zero-based time off end day of the week where zero is Monday.
     */
    public int getEndDayOfWeek() {
        return end.getDayOfWeek().getValue() - 1; // -1 because Mon = 1 and is the first day of the week.
    }

    public boolean isSameDay() {
        Instant startDay = start.toInstant(ZoneOffset.UTC).truncatedTo(ChronoUnit.DAYS);
        Instant endDay = end.toInstant(ZoneOffset.UTC).truncatedTo(ChronoUnit.DAYS);
        return startDay.equals(endDay);
    }

    /**
     * Return true if the time off start and end are in the correct order and on the same week.
     */
	public boolean isValid() {
        return isStartBeforeEnd() && isSameWeek();
    }

    public boolean isStartBeforeEnd() {
        return start.compareTo(end) < 0;
    }

    /**
     * Returns true if the time off is at most Monday to next Monday 12am.
     */
    public boolean isSameWeek() {
        if (isSameDay()) {
            return true;
        }

        if (end.getDayOfWeek().equals(DayOfWeek.MONDAY) && (end.getHour() != 0 || end.getMinute() != 0)) {
            return false;
        }

        TemporalField weekOfYear = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();
        LocalDateTime almostEnd = end.minusSeconds(1); // Would bring Monday back to Sunday if end == Monday 12am.

        // Since weeks of year are cyclical, we need to check also the day difference between the time off start and end.
        return start.get(weekOfYear) == almostEnd.get(weekOfYear) && start.until(almostEnd, ChronoUnit.DAYS) <= 7;
    }

    public boolean isFullDays() {
        return start.getHour() == 0 && start.getMinute() == 0 && end.getHour() == 0 && end.getMinute() == 0;
    }

    /**
     * Return true if the time off is expired.
     */
	public boolean isExpired() {
		return getEnd().compareTo(LocalDateTime.now()) < 0;
    }

    /**
     * Return the Monday of the time off week.
     */
    public LocalDate getMonday() {
        return start.toLocalDate().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }
    
    @Override
    public String toString() { // Helpful in debugging.
        return getStart() + " to " + getEnd();
    }
}


/**
 * Some rules for a time off:
 * 
 * 1. A time off can either be partial day or multi-day.
 * 
 * 2. Partial day time off must either begin at the same time the employee is available that day, 
 *    or end at the last hour he/she is available. For example: if I work from 9am-5pm, valid 
 *    partial day time offs would be 9am-12pm or 3pm-5pm but not 1pm-4pm.
 * 
 * 3. Full day time off must begin at 12am the day of the time off and end 12am next day.
 * 
 * 4. Time offs must be given on weekly blocks. That is, Monday to Sunday and not more,
 *    meaning that a time off must start at least on Monday and end at most on Monday of
 *    the following week for a total of 7 days.
 */