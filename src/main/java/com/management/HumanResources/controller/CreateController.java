package com.management.HumanResources.controller;

import com.management.HumanResources.model.*;
import com.management.HumanResources.service.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "api/v1/new")
public class CreateController {

    @Autowired private CreateService createService;

    @PostMapping(path = "/employee")
    public ResponseEntity<String> addNewEmployee(@RequestBody Employee employee) {
        boolean success = createService.createNewEmployee(employee);
        if (success) {
            createService.initDefaultEmployeeTime(employee);
            return ResponseEntity.status(HttpStatus.CREATED).body("Employee added");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unable to add employee");
        }
    }

    @PostMapping(path = "/feedback")
    public ResponseEntity<String> addNewFeedback(@RequestBody Feedback feedback) {
        boolean success = createService.enterNewFeedback(feedback);
        if (success) {
            return ResponseEntity.status(HttpStatus.CREATED).body("Feedback added");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unable to add feedback");
        }
    }
}