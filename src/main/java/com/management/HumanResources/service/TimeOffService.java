package com.management.HumanResources.service;

import java.time.*;
import java.time.temporal.ChronoUnit;

import com.management.HumanResources.model.*;

import org.springframework.stereotype.Service;

@Service
public class TimeOffService {
    
    private final double DAILY_WORK_HOURS = 8; // Max time off hours that can be used per day.
    private final double WEEKLY_WORK_DAYS = 5; // Max time off days that can be used per week.

    //TODO: This class will hold the logic that checks for time off conflicts in the schedule

    public void approveTimeOffForEmployee(TimeOff timeOff, EmployeeTime employeeTime)
    {
        timeOff.setApproved(true);
        timeOff.setReviewed(true);
        employeeTime.setHoursRemaining(employeeTime.getHoursRemaining() - getRealNumberOfTimeOffHours(timeOff));
    }

    public void declineTimeOff(TimeOff timeOff)
    {
        timeOff.setApproved(false);
        timeOff.setReviewed(true);
    }

    /**
     * Checks if the employee has enough remaining time off hours for the requested time off.
     */
    public boolean isValidTimeOffForEmployee(TimeOff timeOff, EmployeeTime employeeTime) {
        return employeeTime.getHoursRemaining() >= getRealNumberOfTimeOffHours(timeOff);
    }
    
    /**
     * Gets the real number of hours for the time off.
     */
    public double getRealNumberOfTimeOffHours(TimeOff timeOff) {

        Instant startDay = timeOff.getStart().toInstant(ZoneOffset.UTC).truncatedTo(ChronoUnit.DAYS);
        Instant endDay = timeOff.getEnd().toInstant(ZoneOffset.UTC).truncatedTo(ChronoUnit.DAYS);
        
        // If the time off is one day or less...
        if (startDay.equals(endDay)) {
            return Math.min(getNumberOfTimeOffHours(timeOff), DAILY_WORK_HOURS); // Return at most DAILY_HOURS.
        }
        return Math.round(getNumberOfTimeOffDays(timeOff) * (DAILY_WORK_HOURS / 7.0)) * 24; // Good for now...
    }

    /**
     * Gets the number of hours for the time off.
     */
    public double getNumberOfTimeOffHours(TimeOff timeOff) {

        // Calculate the seconds difference between the time off end and start times.
        long millisDiff = timeOff.getEnd().toInstant(ZoneOffset.UTC).toEpochMilli() 
            - timeOff.getStart().toInstant(ZoneOffset.UTC).toEpochMilli();
        
        // Return the hour difference.
        return millisDiff / 3600000.0; // Convert milliseconds to hours (1000 Milliseconds * 60 Seconds * 60 Minutes).
    }

    /**
     * Gets the number of days for the time off.
     */
    public int getNumberOfTimeOffDays(TimeOff timeOff) {
        return (int)Math.round(getNumberOfTimeOffHours(timeOff) / 24); // Convert hours to days and round to the nearest day.
    }
}