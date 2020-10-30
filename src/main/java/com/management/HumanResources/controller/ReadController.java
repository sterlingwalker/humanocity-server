package com.management.HumanResources.controller;

import java.time.*;
import java.util.*;

import com.management.HumanResources.dao.FirebaseDao;
import com.management.HumanResources.model.*;
import com.management.HumanResources.service.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "api/v1")
public class ReadController {

    @Autowired private FirebaseDao firebase;
    @Autowired private ParseService parseService;
    @Autowired private ScheduleService scheduleService;

    @GetMapping(path = "/employees")
    public List<Employee> getEmployees() {
        return parseService.jsonToEmployeeList(firebase.getAllEmployees()); //Need to parse for all employees due to the structure of firebase
    }

    @GetMapping(path = "/employee/{id}")
    public Employee getEmployee(@PathVariable long id) {
        return firebase.getEmployee(id);
    }

    @GetMapping(path = "/employeeTimes")
    public List<EmployeeTime> getEmployeeTimes() {
        return parseService.jsonToEmployeeTimesList(firebase.getAllEmployeeTimes());
    }

    @GetMapping(path = "/employeeTime/{id}")
    public EmployeeTime getEmployeeTime(@PathVariable long id) {
        return firebase.getEmployeeTime(id);
    }

    @GetMapping(path = "/schedule")
    @ExceptionHandler({ Exception.class }) // TODO: Create a NotMondayException https://www.baeldung.com/exception-handling-for-rest-with-spring
    public List<ScheduleEntry> getSchedule(@RequestParam("monday")@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate monday)
        throws Exception { // Date format: yyyy-MM-dd (e.g 2020-11-02)
        return scheduleService.getSchedule(monday);
    }

    @GetMapping(path = "/baseSchedule")
    public List<ScheduleEntry> getBaseSchedule() {
        return scheduleService.getBaseSchedule();
    }

    @GetMapping(path = "/timeoffs")
    public List<EmployeeTimeOff> getEmployeeTimeoffs() {
        List<EmployeeTime> employeeTimes = getEmployeeTimes();
        List<EmployeeTimeOff> employeeTimeOffs = new ArrayList<>();

        for (EmployeeTime employeeTime : employeeTimes) {
            for (TimeOff timeOff : employeeTime.getTimeOffs()) {
                // If the time off ends in the future. i.e. if it has not expired.
                if (timeOff.getEnd().compareTo(LocalDateTime.now()) > 0) {
                    EmployeeTimeOff employeeTimeOff = new EmployeeTimeOff();
                    employeeTimeOff.setStart(timeOff.getStart());
                    employeeTimeOff.setEnd(timeOff.getEnd());
                    employeeTimeOff.setApproved(timeOff.isApproved());
                    employeeTimeOff.setReviewed(timeOff.isReviewed());
                    employeeTimeOff.setEmployeeId(employeeTime.getEmployeeId());
                    employeeTimeOffs.add(employeeTimeOff);
                }
            }
        }

        return employeeTimeOffs;
    }
}