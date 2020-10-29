package com.management.HumanResources.controller;

import com.management.HumanResources.model.Employee;
import com.management.HumanResources.service.UpdateService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "api/v1")
public class UpdateController {

    @Autowired private UpdateService updateService;

    @PatchMapping(path = "/update/employee")
    public ResponseEntity<String> updateExistingEmployee(@RequestBody Employee employee) {
        return updateService.updateEmployeeInfo(employee);
    }

}