package com.management.HumanResources.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import com.management.HumanResources.controller.ReadController;
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
        List<EmployeeTime> employeeTimes = readController.getEmployeeTimes();
        List<ScheduleEntry> scheduleEntries = new ArrayList<>();

        for (EmployeeTime employeeTime : employeeTimes) {
            ScheduleEntry scheduleEntry = new ScheduleEntry();
            scheduleEntry.setAvailability(employeeTime.getAvailability());
            scheduleEntry.setEmployeeId(employeeTime.getEmployeeId());
            scheduleEntries.add(scheduleEntry);
        }

        return scheduleEntries;
    }

    /**
     * Gets the actual schedule for the specified date range based on the employee
     * approved time off requests.
     * 
     * @param monday Monday of the requested week
     * @throws Exception
     */
    public List<ScheduleEntry> getSchedule(LocalDate monday) throws Exception {
        if(!monday.getDayOfWeek().equals(DayOfWeek.MONDAY))
            throw new Exception(monday + " is not a Monday.");
        List<ScheduleEntry> scheduleEntries = new ArrayList<>();
        Map<Long, ScheduleEntry> baseScheduleEntries = getBaseSchedule()
            .stream().collect(Collectors.toMap(ScheduleEntry::getEmployeeId, item -> item));

        for (EmployeeTime employeeTime : readController.getEmployeeTimes()) {
            System.out.println("Employee ID: " + employeeTime.getEmployeeId());
            String[] employeeAvailability = baseScheduleEntries.get(employeeTime.getEmployeeId()).getAvailability();
            List<TimeOff> employeeTimeOffs = employeeTime.getTimeOffs()
                .stream().filter(timeOff -> timeOff.isApproved() && isTimeOffInWeek(timeOff, monday)).collect(Collectors.toList());
            
            for (TimeOff timeOff : employeeTimeOffs) {
                System.out.println("Time off: " + timeOff);
                if (timeOff.isSameDay()) {
                    int dayOfWeek = timeOff.getStart().getDayOfWeek().getValue() - 1; // -1 because Mon = 1 and is the first day of the week.
                    System.out.println("Same day time off: " + dayOfWeek);
                    String sameDayAvailability = employeeAvailability[dayOfWeek].toUpperCase();
                    System.out.println("Same day availability: " + sameDayAvailability);

                    // Check if the employee is available the day of the time off:
                    if (!sameDayAvailability.toLowerCase().equals("null"))
                    {
                        String[] sameDayAvailabilityRange = sameDayAvailability.split("-");

                        // "ha" means hour(1-12) that is directly followed by AM/PM marker (e.g 5PM) https://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html
                        int availabilityStartHour = LocalTime.parse(sameDayAvailabilityRange[0], DateTimeFormatter.ofPattern("ha")).getHour();
                        int availabilityEndHour = LocalTime.parse(sameDayAvailabilityRange[1], DateTimeFormatter.ofPattern("ha")).getHour();
                        System.out.println("Original availability: " + availabilityStartHour + " " + availabilityEndHour);
                        if(timeOff.getStart().getHour() == availabilityStartHour) {
                            availabilityStartHour = timeOff.getEnd().getHour();
                        }
                        else if(timeOff.getEnd().getHour() == availabilityEndHour) {
                            availabilityEndHour = timeOff.getStart().getHour();
                        }
                        else {
                            System.out.println("That would be too much to implement exception...");
                        }
                        employeeAvailability[dayOfWeek] = 
                            hourToAmPmString(availabilityStartHour) + "-" + hourToAmPmString(availabilityEndHour);
                    }
                }
                else {
                    System.out.println("NOT Same day time off");
                    // Note: Java week starts on Monday (index 1) and ends on Sunday (index 7).

                    // For timeOff end we need to take off one day because if the time off ends on next monday,
                    // the index would be 1 and the loop will not run.
                    for (int i = timeOff.getStart().getDayOfWeek().getValue(); 
                        i <= timeOff.getEnd().plusDays(-1).getDayOfWeek().getValue(); i++) {
                        System.out.println("Time off day: " + (i - 1));
                        employeeAvailability[i - 1] = "null";
                    }
                }
            }
            ScheduleEntry scheduleEntry = new ScheduleEntry();
            scheduleEntry.setEmployeeId(employeeTime.getEmployeeId());
            scheduleEntry.setAvailability(employeeAvailability);
            scheduleEntries.add(scheduleEntry);
        }
        return scheduleEntries;
    }

    private boolean isTimeOffInWeek(TimeOff timeOff, LocalDate monday) {
        LocalDate nextMonday = monday.plusDays(7);
        //       Good: monday-------------timeOff------------nextMonday
        //        Bad: timeOff------------monday-------------nextMonday
        //        Bad: monday-------------nextMonday---------timeOff
        // Apocalypse: nextMonday---------timeOff------------monday
        return monday.atStartOfDay().compareTo(timeOff.getStart()) <= 0 && nextMonday.atStartOfDay().compareTo(timeOff.getEnd()) >= 0;
    }

    /**
     * Converts an hour value to AM/PM no-space string. (e.g. 23 -> 11PM)
     * @param hour An hour value between 0 and 23
     */
    private String hourToAmPmString(int hour) {
        return String.valueOf(hour % 12) + (hour < 12 ? "A" : "P") + "M";
    }
}