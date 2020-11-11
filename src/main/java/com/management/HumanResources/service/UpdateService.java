package com.management.HumanResources.service;

import java.util.List;

import com.management.HumanResources.dao.FirebaseDao;
import com.management.HumanResources.exceptions.InvalidTimeOffException;
import com.management.HumanResources.model.*;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class UpdateService {

    @Autowired private FirebaseDao firebase;
    @Autowired private ParseService parseService;
    @Autowired private TimeOffService timeOffService;

    public ResponseEntity<String> updateEmployeeInfo(Employee updatedEmployee) {
        if (updatedEmployee.getId()==0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Employee ID invalid");
        }
        Employee master = firebase.getEmployee(updatedEmployee.getId());
        if(master == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee not found");
        }
        master.merge(updatedEmployee);
        firebase.updateEmployee(master);
        return ResponseEntity.status(HttpStatus.OK).body("Employee Updated");
    }

    public ResponseEntity<String> terminateEmployee(long id) {
        List<Employee> employees = parseService.jsonToEmployeeList(firebase.getAllEmployees());
        for(Employee employee : employees) {
            if (employee.getId() == id) {
                eraseAllEmployeeData(id);
                return ResponseEntity.status(HttpStatus.OK).body("Employee Terminated");
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee not found");
    }

    public void eraseAllEmployeeData(long id) {
        firebase.eraseRecord("/employees/" + id + ".json");
        firebase.eraseRecord("/time/" + id + ".json");
    }

    public String approveTimeOff(EmployeeTimeOff time) {
        EmployeeTime employee = parseService.jsonToEmployeeTime(new JSONObject(firebase.getEmployeeTime(time.getEmployeeId())));
        if (employee == null) {
            return "Employee not found";
        }

        try {
            timeOffService.validateTimeOffForEmployee(time, employee);
            employee = timeOffService.approveTimeOffForEmployee(time, employee);
            firebase.updateTimeOff(parseService.timeOffToCsv(employee.getTimeOffs()), employee.getEmployeeId());
            firebase.updateHoursRemaining(employee.getHoursRemaining(), employee.getEmployeeId());
            return "Time Off approved successfully";
        } catch (InvalidTimeOffException e) {
            return e.getMessage();
        }
    }

    public String denyTimeOff(EmployeeTimeOff time) {
        EmployeeTime employee = parseService.jsonToEmployeeTime(new JSONObject(firebase.getEmployeeTime(time.getEmployeeId())));
        if (employee == null) {
            return "Employee not found";
        }

        try {
            timeOffService.validateTimeOffForEmployee(time, employee);
            employee = timeOffService.declineTimeOff(time, employee);
            firebase.updateTimeOff(parseService.timeOffToCsv(employee.getTimeOffs()), employee.getEmployeeId());
            return "Time Off denied successfully";
        } catch (InvalidTimeOffException e) {
            return e.getMessage();
        }
    }
}