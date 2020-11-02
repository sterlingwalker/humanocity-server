package com.management.HumanResources.service;

import java.time.DayOfWeek;
import java.util.*;

import com.management.HumanResources.exceptions.*;
import com.management.HumanResources.model.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TimeOffService {

    @Autowired ScheduleService scheduleService;
    
    private final int MIN_WORKERS = 3; // Minimum number of employees that work at a given time.

    public void approveTimeOffForEmployee(TimeOff timeOff, EmployeeTime employeeTime) {
        timeOff.setApproved(true);
        timeOff.setReviewed(true);
        employeeTime.setHoursRemaining(employeeTime.getHoursRemaining() - getNumberOfTimeOffHoursForEmployee(timeOff, employeeTime));
    }

    public void declineTimeOff(TimeOff timeOff) {
        timeOff.setApproved(false);
        timeOff.setReviewed(true);
    }

    /**
     * Checks if a time off is valid for an employee and throws an exception if invalid.
     * 
     * @throws InvalidTimeOffException
     */
    public void validateTimeOffForEmployee(TimeOff timeOff, EmployeeTime employeeTime) throws InvalidTimeOffException {
        
        // Check if the time off start and end are in the correct order.
        if (!timeOff.isStartBeforeEnd()) {
            throw new InvalidTimeOffStartAfterEndException();
        }

        // Check if the time off start and end are on the same week.
        if (!timeOff.isSameWeek()) {
            throw new InvalidTimeOffNotSameWeekException();
        }

        if (timeOff.isSameDay()) {
            // Check same day start and end times (must start and/or end at the availability time).
            int timeOffDayOfWeek = timeOff.getStartDayOfWeek();
            DailyAvailability dailyAvailability = employeeTime.getAvailability()[timeOffDayOfWeek];
            if (timeOff.getStart().getHour() != dailyAvailability.getStart() && timeOff.getEnd().getHour() != dailyAvailability.getEnd()) {
                throw new InvalidTimeOffStartEndException();
            }
        }
        else if (!timeOff.isFullDays()) { // Check start and end days (must start and end at 12am).
            throw new InvalidTimeOffMultiDayRangeException();
        }
        
        if (getMinNumberOfWorkersForPossibleTimeOff(timeOff) < MIN_WORKERS) {
            throw new InvalidTimeOffNotEnoughWorkersException();
        }

        // Check if the employee has enough hours
        if (employeeTime.getHoursRemaining() < getNumberOfTimeOffHoursForEmployee(timeOff, employeeTime)) {
            throw new InvalidTimeOffNotEnoughRemainingHoursException();
        }

        // The time off request is valid if got this far.
    }
    
    /**
     * Gets the number of hours for the time off request.
     */
    public int getNumberOfTimeOffHoursForEmployee(TimeOff timeOff, EmployeeTime employeeTime) {
        int hours = 0;

        if (timeOff.isSameDay()) {
            hours = timeOff.getEnd().getHour() - timeOff.getStart().getHour();
        }
        else {
            DailyAvailability[] availability = employeeTime.getAvailability();
            // For time off end we need to take off one day because if the time off ends on next monday,
            // the index would be 0 and the loop will not run. Modulo 7 will convert -1 to 6 which corresponds to Sunday.
            for (int dayOfWeek = timeOff.getStartDayOfWeek(); dayOfWeek <= (timeOff.getEndDayOfWeek() - 1) % 7; dayOfWeek++) {
                hours += availability[dayOfWeek].getHours();
            }
        }

        return hours;
    }

    public int getMinNumberOfWorkersForPossibleTimeOff(TimeOff timeOff) {
        List<ScheduleEntry> schedule = scheduleService.getSchedule(timeOff.getMonday());
        int workersCount = Integer.MAX_VALUE;

        if (timeOff.isSameDay()) {
            // Check how many people work on the time off time range.               
            for (int hour = timeOff.getStart().getHour(); hour < timeOff.getEnd().getHour(); hour++) {
                workersCount = Math.min(workersCount, getNumberOfWorkersAtHourOfDay(hour, timeOff.getStartDayOfWeek(), schedule));
            }
        }
        else {
            // Check how many people work on the time off day range for the office hours. 
            DailyAvailability[] officeHours = getOfficeHours();
            // For time off end we need to take off one day because if the time off ends on next monday,
            // the index would be 0 and the loop will not run. Modulo 7 will convert -1 to 6 which corresponds to Sunday.
            for (int dayOfWeek = timeOff.getStartDayOfWeek(); dayOfWeek <= (timeOff.getEndDayOfWeek() - 1) % 7; dayOfWeek++) {
                DailyAvailability sameDayOfficeHours = officeHours[dayOfWeek];
                for (int hour = sameDayOfficeHours.getStart(); hour < sameDayOfficeHours.getEnd(); hour++) {
                    workersCount = Math.min(workersCount, getNumberOfWorkersAtHourOfDay(hour, timeOff.getStartDayOfWeek(), schedule));
                }
            }
        }

        return workersCount;
    }

    private int getNumberOfWorkersAtHourOfDay(int hour, int dayOfWeek, List<ScheduleEntry> schedule) {
        int workersCount = 0;
        // Loop through all employee actual schedules and count how many work at the specified hour.
        for (ScheduleEntry scheduleEntry : schedule) {
            DailyAvailability availability = scheduleEntry.getAvailability()[dayOfWeek];
            if (!availability.isOff() && availability.getStart() <= hour && hour < availability.getEnd()) {
                workersCount -= -1; // AKA workersCount++; Just goofing around...
            }
        }
        return workersCount - 1; // -1 because of initially counting the time off requester without the applied time off.
    }
    
    public DailyAvailability[] getOfficeHours() {
        DailyAvailability[] availabilities = new DailyAvailability[7];
        for (int dayOfWeek = DayOfWeek.MONDAY.getValue() - 1; dayOfWeek <= DayOfWeek.THURSDAY.getValue(); dayOfWeek++) {
            DailyAvailability availability = new DailyAvailability();
            availability.setStart(9);
            availability.setEnd(5);
            availabilities[dayOfWeek] = availability;
        }

        DailyAvailability fridayAvailability = new DailyAvailability();
        fridayAvailability.setStart(9);
        fridayAvailability.setEnd(3);
        availabilities[DayOfWeek.FRIDAY.getValue() - 1] = fridayAvailability;

        availabilities[DayOfWeek.SATURDAY.getValue() - 1] = new DailyAvailability(); // Closed
        availabilities[DayOfWeek.SUNDAY.getValue() - 1] = new DailyAvailability(); // Closed
        return availabilities;
    }
}