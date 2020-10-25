package com.management.HumanResources.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.management.HumanResources.dao.FirebaseDao;
import com.management.HumanResources.model.Employee;
import com.management.HumanResources.model.EmployeeTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CreateService {
    public static final double INITIAL_TIME_OFF = 160;
    public static final String DEFAULT_AVAILABILITY= "9am-5pm,9am-5pm,9am-5pm,9am-5pm,9am-5pm,null,null";

    @Autowired private FirebaseDao firebase;
    @Autowired private ParseService parseService;
    @Autowired private TimeOffService timeOffService;

    public boolean createNewEmployee(Employee employee) {
        Random r = new Random();
        long number = (long)(r.nextDouble()*(Math.pow(10, 10)));
        if(uniqueInfo(number, employee)) {
            employee.setId(number);
            firebase.addEmployee(employee);
            return true;
        }
        return false;
    }

    public boolean uniqueInfo(long id, Employee emp) {
        List<Employee> allEmployees = parseService.jsonToEmployeeList(firebase.getAllEmployees());
        return allEmployees.stream().anyMatch(employee -> employee.getId() != id &&
         !employee.uniqueData().equals(emp.uniqueData()));

    }

    public void initializeDefaultTime(Employee emp) {
        EmployeeTime time = new EmployeeTime();
        time.setTotalHours(INITIAL_TIME_OFF);
        time.setHoursRemaining(INITIAL_TIME_OFF);
        time.setEmployeeId(emp.getId());
        time.setCsvTimeOff(parseService.timeOffToCSV(new ArrayList<>()));
        time.setCsvAvailability(DEFAULT_AVAILABILITY);
        firebase.addEmployeeTime(time);
    }

}