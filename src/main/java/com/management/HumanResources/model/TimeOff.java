package com.management.HumanResources.model;

import java.time.*;
import java.time.temporal.ChronoUnit;

import lombok.Data;

@Data
public class TimeOff {
    private LocalDateTime start;
    private LocalDateTime end;
    private boolean approved;
    private boolean reviewed;

    public boolean isSameDay() {
        Instant startDay = start.toInstant(ZoneOffset.UTC).truncatedTo(ChronoUnit.DAYS);
        Instant endDay = end.toInstant(ZoneOffset.UTC).truncatedTo(ChronoUnit.DAYS);
        return startDay.equals(endDay);
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