package com.management.HumanResources.controller;
import java.util.ArrayList;
import java.util.List;

import com.management.HumanResources.dao.FirebaseDao;
import com.management.HumanResources.model.*;
import com.management.HumanResources.service.CreateService;
import com.management.HumanResources.service.ParseService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "api/v1")
public class CreateController {

    @Autowired private FirebaseDao firebase;
    @Autowired private CreateService createService;

    @PostMapping(path = "/new/employee")
    public ResponseEntity<String> addNewEmployee(@RequestBody Employee employee) {
        boolean success = createService.createNewEmployee(employee);
        if (success) {
            createService.initializeDefaultTime(employee);
            return ResponseEntity.status(HttpStatus.CREATED).body("Employee added");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unable to add employee");
        }
    }

}