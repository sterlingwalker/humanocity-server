package com.management.HumanResources.controller;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

import com.management.HumanResources.dao.FirebaseDao;
import com.management.HumanResources.exceptions.*;
import com.management.HumanResources.model.*;
import com.management.HumanResources.service.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@CrossOrigin
@RestController
@RequestMapping(value = "api/v1")
public class ReadController {

    @Autowired private FirebaseDao firebase;
    @Autowired private ParseService parseService;
    @Autowired private ScheduleService scheduleService;
    @Autowired private ReadController readController;

    @GetMapping(path = "/employees")
    public List<Employee> getEmployees() {
        return parseService.jsonToEmployeeList(firebase.getAllEmployees());
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

    @GetMapping(path = "/schedule") // Date format: yyyy-MM-dd (e.g 2020-11-02)
    public List<ScheduleEntry> getSchedule(@RequestParam("monday") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate monday) {
        try {
            return scheduleService.getSchedule(monday);
        } catch (NotMondayException nme) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                nme.getMessage() + " The 'monday' parameter must be a Monday date.", nme);
        }
    }

    @GetMapping(path = "/baseSchedule")
    public List<ScheduleEntry> getBaseSchedule() {
        return scheduleService.getBaseSchedule();
    }

    @GetMapping(path = "/timeoffs")
    public List<EmployeeTimeOff> getEmployeeTimeoffs() {
        List<EmployeeTime> employeeTimes = getEmployeeTimes();
        List<EmployeeTimeOff> employeeTimeOffs = new ArrayList<>();
        Map<Long, Employee> employees = readController.getEmployees().stream().collect(Collectors.toMap(Employee::getId, e -> e));

        for (EmployeeTime employeeTime : employeeTimes) {
            for (TimeOff timeOff : employeeTime.getTimeOffs()) {
                if (!timeOff.isExpired()) {
                    Employee employee = employees.get(employeeTime.getEmployeeId());
                    EmployeeTimeOff employeeTimeOff = new EmployeeTimeOff();
                    employeeTimeOff.setStart(timeOff.getStart());
                    employeeTimeOff.setEnd(timeOff.getEnd());
                    employeeTimeOff.setApproved(timeOff.isApproved());
                    employeeTimeOff.setReviewed(timeOff.isReviewed());
                    employeeTimeOff.setEmployeeId(employeeTime.getEmployeeId());
                    employeeTimeOff.setFirstName(employee.getFirstName());
                    employeeTimeOff.setLastName(employee.getLastName());
                    employeeTimeOffs.add(employeeTimeOff);
                }
            }
        }

        return employeeTimeOffs;
    }

    @GetMapping(path = "/feedbackList")
    public List<Feedback> getFeedbackList() {
        return parseService.jsonToFeedbackList(firebase.getAllFeedback());
    }
}