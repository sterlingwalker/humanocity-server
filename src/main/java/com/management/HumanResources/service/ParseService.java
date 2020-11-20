package com.management.HumanResources.service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import com.management.HumanResources.exceptions.*;
import com.management.HumanResources.model.*;

import org.json.JSONObject;

import org.springframework.stereotype.Service;

@Service
public class ParseService {

    public List<Employee> jsonToEmployeeList(String json) {
        JSONObject jsonList = new JSONObject(json);
        List<Employee> employees = new ArrayList<Employee>();
        jsonList.keySet().forEach(key -> 
            employees.add(jsonToEmployee(jsonList.getJSONObject(key))));
        return employees;
    }

    public Employee jsonToEmployee(JSONObject obj) {
        Employee emp = new Employee();
        emp.setFirstName(obj.getString("firstName"));
        emp.setLastName(obj.getString("lastName"));
        emp.setEmail(obj.getString("email"));
        emp.setPosition(obj.getString("position"));
        emp.setSalary(obj.getInt("salary"));
        emp.setDept(obj.getString("dept"));
        emp.setManagerID(obj.getLong("managerID"));
        emp.setId(obj.getLong("id"));
        emp.setAddress(jsonToAddress(obj.getJSONObject("address")));
        emp.setPhoneNumber(obj.getString("phoneNumber"));
        emp.setEmergencyName(obj.getString("emergencyName"));
        emp.setEmergencyNumber(obj.getString("emergencyNumber"));
        return emp;
    }

    public Address jsonToAddress(JSONObject obj) {
        Address a = new Address();
        a.setStreet(obj.getString("street"));
        a.setCity(obj.getString("city"));
        a.setState(obj.getString("state"));
        a.setZipcode(obj.getString("zipcode"));
        return a;
    }

	public List<EmployeeTime> jsonToEmployeeTimesList(String json) {
		JSONObject jsonList = new JSONObject(json);
        List<EmployeeTime> employeeTimes = new ArrayList<EmployeeTime>();
        jsonList.keySet().forEach(key -> 
            employeeTimes.add(jsonToEmployeeTime(jsonList.getJSONObject(key))));
        return employeeTimes;
    }
    
    public EmployeeTime jsonToEmployeeTime(JSONObject obj) {
        EmployeeTime employeeTime = new EmployeeTime();
        employeeTime.setEmployeeId(obj.getLong("employeeId"));
        employeeTime.setTotalHours(obj.getInt("totalHours"));
        employeeTime.setHoursRemaining(obj.getInt("hoursRemaining"));
        employeeTime.setAvailability(csvToEmployeeAvailabilityArray(obj.getString("availability")));
        employeeTime.setTimeOffs(psvToEmployeeTimeOffsList(obj.getString("timeOffs")));
        return employeeTime;
    }

    public DailyAvailability[] csvToEmployeeAvailabilityArray(String csv) {
        final int daysPerWeek = 7;
        String[] dailyAvailabilityStrings = csv.toUpperCase().split(",");

        // Check if employee daily availabilities were provided for all 7 week days.
        if (dailyAvailabilityStrings.length != daysPerWeek) {
            throw new InvalidEmployeeAvailabilityNotSevenDaysException(dailyAvailabilityStrings.length);
        }

        DailyAvailability[] dailyAvailabilities = new DailyAvailability[daysPerWeek];
        for (int i = 0; i < daysPerWeek; i++) {
            dailyAvailabilities[i] = parseDailyAvailability(dailyAvailabilityStrings[i]);
        }
        
        return dailyAvailabilities;
    }

    public DailyAvailability parseDailyAvailability(String dailyAvailabilityString) {
        DailyAvailability dailyAvailability = new DailyAvailability();
        
        // If null, then the employee is off that day.
        if (!dailyAvailabilityString.equals("NULL")) {
            String[] dailyAvailabilityRange = dailyAvailabilityString.split("-");

            // "ha" means an hour(1-12) that is directly followed by an AM/PM marker (e.g 5PM)
            // More info here: https://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ha");
            int availabilityStartHour = LocalTime.parse(dailyAvailabilityRange[0], formatter).getHour();
            int availabilityEndHour = LocalTime.parse(dailyAvailabilityRange[1], formatter).getHour();

            dailyAvailability.setStart(availabilityStartHour);
            dailyAvailability.setEnd(availabilityEndHour);
            if (!dailyAvailability.isLegal()) {
                throw new InvalidEmployeeAvailabilityStartAfterEndException(dailyAvailability);
            }
        }
        
        return dailyAvailability;
    }

    public List<TimeOff> psvToEmployeeTimeOffsList(String psv) { // PSV = Pipe | separated value I guess...
        if (psv.equals("null")) {
            return new ArrayList<>();
        }
        String[] employeeTimeOffCsvs = psv.split("\\|");
        List<TimeOff> timeOffs = new ArrayList<TimeOff>();
        for (String employeeTimeOffCsv : employeeTimeOffCsvs) {
            timeOffs.add(csvToEmployeeTimeOff(employeeTimeOffCsv));
        }
        return timeOffs;
    }

    public TimeOff csvToEmployeeTimeOff(String csv) {
        // Expected format: yyyy-MM-dd HH:mm,yyyy-MM-dd HH:mm,boolean i.e. start,end,approved
        String[] employeeTimeOffTokens = csv.split(",");
        TimeOff timeOff = new TimeOff();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        timeOff.setStart(LocalDateTime.parse(employeeTimeOffTokens[0], formatter));
        timeOff.setEnd(LocalDateTime.parse(employeeTimeOffTokens[1], formatter));

        String approvedToken = employeeTimeOffTokens[2];
        if (!approvedToken.equals("null")) {
            timeOff.setReviewed(true);
            timeOff.setApproved(Boolean.parseBoolean(approvedToken));
        }
        
        return timeOff;
    }

    public String timeOffToCsv(List<TimeOff> timeOffs) {
        if (timeOffs.isEmpty()) {
            return "null";
        }
        return timeOffs.stream()
                      .map(to -> csvGenerator(
                          to.getStart().toString(),
                          to.getEnd().toString(),
                          to.isReviewed() ? String.valueOf(to.isApproved()) : "null")
                        )
                      .collect(Collectors.joining("|")).replace("T", " ");
    }

    public String csvGenerator(String ...stringArr) {
        return String.join(",", stringArr);
    }

    public List<Feedback> jsonToFeedbackList(String json) {
        JSONObject jsonFeedbackList = new JSONObject(json);
        List<Feedback> feedback = new ArrayList<Feedback>();
        jsonFeedbackList.keySet().forEach(key -> 
            feedback.add(jsonToFeedback(jsonFeedbackList.getJSONObject(key))));
        return feedback;
    }

    public Feedback jsonToFeedback(JSONObject obj) {
        Feedback fback = new Feedback();
        fback.setEmployeeId(obj.getLong("employeeId"));
        fback.setFeedbackId(obj.getLong("feedbackId"));
        fback.setType(obj.getString("type"));
        fback.setDescription(obj.getString("description"));
        return fback;
    }
}