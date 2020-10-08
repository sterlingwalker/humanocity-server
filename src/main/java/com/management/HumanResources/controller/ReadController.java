package com.management.HumanResources.controller;

import java.util.List;

import com.management.HumanResources.dao.FirebaseDao;
import com.management.HumanResources.model.Employee;
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
    
    /*
    TODO: add rest of read methods to controller
    */
}