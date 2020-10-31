package com.management.HumanResources.service;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

import com.management.HumanResources.controller.ReadController;
import com.management.HumanResources.exceptions.NotMondayException;
import com.management.HumanResources.model.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ScheduleService {

    @Autowired private ReadController readController;

    /**
     * Gets the base schedule of all employees based on their base availability i.e. without the consideration of time offs.
     */
    public List<ScheduleEntry> getBaseSchedule() {
        List<ScheduleEntry> scheduleEntries = new ArrayList<>();
        for (EmployeeTime employeeTime : readController.getEmployeeTimes()) {
            scheduleEntries.add(getEmployeeBaseSchedule(employeeTime));
        }
        return scheduleEntries;
    }

    /**
     * Gets the base schedule of an employee based on their base availability i.e. without the consideration of time offs.
     */
    public ScheduleEntry getEmployeeBaseSchedule(EmployeeTime employeeTime) {
        ScheduleEntry scheduleEntry = new ScheduleEntry();
        scheduleEntry.setAvailability(employeeTime.getAvailability());
        scheduleEntry.setEmployeeId(employeeTime.getEmployeeId());
        return scheduleEntry;
    }

    /**
     * Gets the actual schedule for the specified week staring on Monday
     * based on the employee approved time off requests for all employees.
     * 
     * @param monday Monday of the requested week
     * @throws NotMondayException
     */
    public List<ScheduleEntry> getSchedule(LocalDate monday) throws NotMondayException {
        if (!monday.getDayOfWeek().equals(DayOfWeek.MONDAY)) {
            throw new NotMondayException(monday);
        }

        List<ScheduleEntry> scheduleEntries = new ArrayList<>();
        for (EmployeeTime employeeTime : readController.getEmployeeTimes()) {
            scheduleEntries.add(getEmployeeSchedule(monday, employeeTime));
        }
        return scheduleEntries;
    }

    /**
     * Gets the actual schedule for the specified week staring on Monday
     * based on the employee approved time off requests for a specific employee.
     * 
     * @param monday Monday of the requested week
     * @throws NotMondayException
     */
    public ScheduleEntry getEmployeeSchedule(LocalDate monday, EmployeeTime employeeTime) throws NotMondayException {
        if (!monday.getDayOfWeek().equals(DayOfWeek.MONDAY)) {
            throw new NotMondayException(monday);
        }
        
        DailyAvailability[] employeeAvailability = employeeTime.getAvailability();
        List<TimeOff> employeeTimeOffs = employeeTime.getTimeOffs()
            .stream().filter(timeOff -> timeOff.isApproved() && isTimeOffInWeek(timeOff, monday)).collect(Collectors.toList());
        
        for (TimeOff timeOff : employeeTimeOffs) {
            if (timeOff.isSameDay()) {
                // -1 because Mon = 1 and is the first day of the week.
                int dayOfWeek = timeOff.getStart().getDayOfWeek().getValue() - 1;
                DailyAvailability sameDayAvailability = employeeAvailability[dayOfWeek];

                if (!sameDayAvailability.isOff()) {
                    if(timeOff.getStart().getHour() == sameDayAvailability.getStart()) {
                        sameDayAvailability.setStart(timeOff.getEnd().getHour());
                    }
                    if(timeOff.getEnd().getHour() == sameDayAvailability.getEnd()) {
                        sameDayAvailability.setEnd(timeOff.getStart().getHour());
                    }
                }
            }
            else {
                // Note: Java week starts on Monday (index 1) and ends on Sunday (index 7).

                // For timeOff end we need to take off one day because if the time off ends on next monday,
                // the index would be 1 and the loop will not run.
                for (int i = timeOff.getStart().getDayOfWeek().getValue(); 
                    i <= timeOff.getEnd().plusDays(-1).getDayOfWeek().getValue(); i++) {
                    employeeAvailability[i - 1] = new DailyAvailability();
                }
            }
        }
        ScheduleEntry scheduleEntry = new ScheduleEntry();
        scheduleEntry.setEmployeeId(employeeTime.getEmployeeId());
        scheduleEntry.setAvailability(employeeAvailability);
        return scheduleEntry;
    }

    private boolean isTimeOffInWeek(TimeOff timeOff, LocalDate monday) {
        LocalDate nextMonday = monday.plusDays(7);
        //       Good: monday-------------timeOff------------nextMonday
        //        Bad: timeOff------------monday-------------nextMonday
        //        Bad: monday-------------nextMonday---------timeOff
        // Apocalypse: nextMonday---------timeOff------------monday
        return monday.atStartOfDay().compareTo(timeOff.getStart()) <= 0 && nextMonday.atStartOfDay().compareTo(timeOff.getEnd()) >= 0;
    }
}