package com.management.HumanResources.service;

import java.util.List;

import com.management.HumanResources.dao.FirebaseDao;
import com.management.HumanResources.model.Employee;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class UpdateService {

    @Autowired private FirebaseDao firebase;
    @Autowired private ParseService parseService;

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
}