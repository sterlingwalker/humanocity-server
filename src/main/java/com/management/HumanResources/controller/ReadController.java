package com.management.HumanResources.controller;

import java.util.ArrayList;
import java.util.List;

import com.management.HumanResources.dao.FirebaseDao;
import com.management.HumanResources.model.*;
import com.management.HumanResources.service.ParseService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "api/v1")
public class ReadController {

    @Autowired private FirebaseDao firebase;
    @Autowired private ParseService parseService;

    @GetMapping(path = "/employees")
    public List<Employee> getEmployees() {
        return parseService.toEmployeeList(firebase.getAllEmployees()); //Need to parse for all employees due to the structure of firebase
    }

    @GetMapping(path = "/employee/{id}")
    public Employee getEmployee(@PathVariable long id) {
        return firebase.getEmployee(id);
    }

    @GetMapping(path = "/employeeTimes")
    public List<EmployeeTime> getEmployeeTimes() {
        return parseService.toEmployeeTimesList(firebase.getAllEmployeeTimes());
    }

    @GetMapping(path = "/schedule")
    public List<ScheduleEntry> getSchedule() {
        List<EmployeeTime> employeeTimes = getEmployeeTimes();
        List<ScheduleEntry> scheduleEntries = new ArrayList<>();

        for (EmployeeTime employeeTime : employeeTimes) {
            ScheduleEntry employeeTimeOff = new ScheduleEntry();
            employeeTimeOff.setAvailability(employeeTime.getAvailability());
            employeeTimeOff.setEmployeeId(employeeTime.getEmployeeId());
            scheduleEntries.add(employeeTimeOff);
        }

        return scheduleEntries;
    }

    @GetMapping(path = "/timeoffs")
    public List<EmployeeTimeOff> getEmployeeTimeoffs() {
        List<EmployeeTime> employeeTimes = getEmployeeTimes();
        List<EmployeeTimeOff> employeeTimeOffs = new ArrayList<>();

        for (EmployeeTime employeeTime : employeeTimes) {
            for (TimeOff timeOff : employeeTime.getTimeOffs()) {
                EmployeeTimeOff employeeTimeOff = new EmployeeTimeOff();
                employeeTimeOff.setStart(timeOff.getStart());
                employeeTimeOff.setEnd(timeOff.getEnd());
                employeeTimeOff.setApproved(timeOff.isApproved());
                employeeTimeOff.setEmployeeId(employeeTime.getEmployeeId());
                employeeTimeOffs.add(employeeTimeOff);
            }
        }

        return employeeTimeOffs;
    }
}