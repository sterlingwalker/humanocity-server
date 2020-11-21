package com.management.HumanResources.service;

import java.util.*;

import com.management.HumanResources.dao.FirebaseDao;
import com.management.HumanResources.exceptions.InvalidTimeOffException;
import com.management.HumanResources.model.*;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CreateService {
    public static final double INITIAL_TIME_OFF = 160;
    public static final String DEFAULT_AVAILABILITY= "9am-5pm,9am-5pm,9am-5pm,9am-5pm,9am-5pm,null,null";
    public static final long EXEC_MANAGER_ID = 1000000000;

    @Autowired private FirebaseDao firebase;
    @Autowired private ParseService parseService;
    @Autowired private TimeOffService timeOffService;

    public boolean createNewEmployee(Employee employee) {
        Random r = new Random();
        long futureId = (long)(r.nextDouble()*(Math.pow(10, 10)));
        if(isUniqueFutureEmployee(futureId, employee)) {
            employee.setId(futureId);
            firebase.addEmployee(employee);
            return true;
        }
        return false;
    }

    public boolean isUniqueFutureEmployee(long id, Employee emp) {
        List<Employee> allEmployees = parseService.jsonToEmployeeList(firebase.getAllEmployees());
        return allEmployees.stream().anyMatch(employee -> 
            employee.getId() != id 
            && !employee.getUniqueData().equals(emp.getUniqueData())
            && employee.getManagerID() != EXEC_MANAGER_ID);
    }

    public void initDefaultEmployeeTime(Employee emp) {
        EmployeeTime time = new EmployeeTime();
        time.setTotalHours(INITIAL_TIME_OFF);
        time.setHoursRemaining(INITIAL_TIME_OFF);
        time.setEmployeeId(emp.getId());
        time.setCsvTimeOff(parseService.timeOffToCsv(new ArrayList<>()));
        time.setCsvAvailability(DEFAULT_AVAILABILITY);
        firebase.addEmployeeTime(time);
    }

    public boolean enterNewFeedback(Feedback feedback) {
        Employee employee = firebase.getEmployee(feedback.getEmployeeId());
        if(employee == null) {
            return false;
        }
        Random rand = new Random(System.currentTimeMillis()); //Time based to ensure a unique id is created
        feedback.setFeedbackId((long)(rand.nextDouble() * (Math.pow(10, 10))));
        firebase.addFeedback(feedback);
        return true;
    }

    public String enterNewTimeOff(EmployeeTimeOff timeOff) {
        EmployeeTime employee = parseService.jsonToEmployeeTime(new JSONObject(firebase.getEmployeeTime(timeOff.getEmployeeId())));
        if (employee == null) {
            return "Employee not found";
        }

        try {
            if (!timeOff.isSameDay()) {
                timeOff.setEnd(timeOff.getEnd().plusDays(1)); // Adding one day to include the last day as a full off day.
            }
            timeOffService.validateTimeOffForEmployee(timeOff, employee);
            List<TimeOff> allTimeOff = employee.getTimeOffs();
            allTimeOff.add(timeOff);
            firebase.updateTimeOff(parseService.timeOffToCsv(allTimeOff), timeOff.getEmployeeId());
            return "Time off submitted successfully";
        } catch (InvalidTimeOffException e) {
            return e.getMessage();
        }
    }
}