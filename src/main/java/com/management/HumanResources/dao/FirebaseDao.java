package com.management.HumanResources.dao;

import java.util.List;

import javax.annotation.PostConstruct;

import com.management.HumanResources.model.Employee;
import com.management.HumanResources.model.TimeOff;

import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;

@Repository
public class FirebaseDao extends Dao {
  @PostConstruct
  private void init() {
    webClient =
        WebClient.builder()
            .baseUrl("https://humanocity-{key}.firebaseio.com")
            .build();
  }
  //Replace {key} with our database id when you are testing

  //Database paths are just placeholders until the actual ones are created

  public List<Employee> getAllEmployees() {
      return getListOfObjects(Employee.class, "/employees.json");
  }

  public Employee getEmployee(int id) {
      return getSingleObject(Employee.class, "/employees/{employeeId}.json", id);
  }

  public String updateEmployee(Employee employee) {
    return patchSingleObject(String.class, "/employees/{employeeId}.json", employee, employee.getId());
  }

  public String addEmployee(Employee employee) {
      return putSingleObject(String.class, "/employees/{employeeId}.json", employee, employee.getId());
  }

  public List<TimeOff> getAllTimeOffs() {
      return getListOfObjects(TimeOff.class, "/time.json");
  }

  public TimeOff getTimeOff(int id) {
    return getSingleObject(TimeOff.class, "/time/{employeeId}.json", id);
  }

  public String updateTimeOff(TimeOff time) {
      return patchSingleObject(String.class, "/time/{employeeId}.json", time, time.getId());
  }
}

