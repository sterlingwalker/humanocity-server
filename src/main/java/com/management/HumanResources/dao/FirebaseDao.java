package com.management.HumanResources.dao;

import java.util.List;

import javax.annotation.PostConstruct;

import com.management.HumanResources.model.Employee;
import com.management.HumanResources.model.EmployeeTime;
import com.management.HumanResources.model.TimeOff;

import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;

@Repository
public class FirebaseDao extends Dao {

  @Value("${firebase.link}")
  public String firebaseLink;

  @PostConstruct
  private void init() {
    webClient =
        WebClient.builder()
            .baseUrl(firebaseLink)
            .build();
  }

  public String getAllEmployees() {
      return getSingleObject(String.class, "/employees.json");
  }

  public Employee getEmployee(long id) {
      return getSingleObject(Employee.class, "/employees/{employeeId}.json", id);
  }

  public String updateEmployee(Employee employee) {
      return patchSingleObject(String.class, "/employees/{employeeId}.json", employee, employee.getId());
  }

  public String addEmployee(Employee employee) {
      return putSingleObject(String.class, "/employees/{employeeId}.json", employee, employee.getId());
  }

    public String getAllEmployeeTimes() {
        return getSingleObject(String.class, "/time.json");
    }

    public EmployeeTime getEmployeeTime(long employeeId) {
        return getSingleObject(EmployeeTime.class, "/time/{employeeId}.json", employeeId);
    }
  
    public String updateEmployeeTime(EmployeeTime employeeTime) {
        return patchSingleObject(String.class, "/time/{employeeId}.json", employeeTime.toDatabaseString(), employeeTime.getEmployeeId());
    }
  
    public String addEmployeeTime(EmployeeTime employeeTime) {
        return putSingleObject(String.class, "/time/{employeeId}.json", employeeTime.toDatabaseString(), employeeTime.getEmployeeId());
    }
}