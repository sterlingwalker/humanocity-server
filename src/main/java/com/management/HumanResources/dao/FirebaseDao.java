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
            .baseUrl("TBD")
            .defaultHeaders(
                httpHeaders ->
                    httpHeaders.setBasicAuth(
                        "TODO databaseUsername", "TODO databasePassword"))
            .build();
  }

  //Database paths are just placeholders until the actual ones are created

  public List<Employee> getAllEmployees() {
      return getListOfObjects(Employee.class, "path");
  }

  public Employee getEmployee(int id) {
      return getSingleObject(Employee.class, "path/{employeeId}", id);
  }

  public String updateEmployee(Employee employee) {
    return patchSingleObject(String.class, "path", employee);
  }

  public String addEmployee(Employee employee) {
      return postSingleObject(String.class, "path", employee);
  }

  public List<TimeOff> getAllTimeOffs() {
      return getListOfObjects(TimeOff.class, "path");
  }

  public TimeOff getTimeOff(int id) {
    return getSingleObject(TimeOff.class, "path/{employeeId}", id);
  }

  public String updateTimeOff(TimeOff time) {
      return postSingleObject(String.class, "path", time);
  }
}

