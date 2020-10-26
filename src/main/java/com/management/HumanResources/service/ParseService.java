package com.management.HumanResources.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    public String[] csvToEmployeeAvailabilityArray(String csv) {
        return csv.split(",");
    }

    public List<TimeOff> psvToEmployeeTimeOffsList(String psv) { // PSV = Pipe | separated value I guess...
        if(psv.equals("null")){
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
        if(!approvedToken.equals("null"))
        {
            timeOff.setReviewed(true);
            timeOff.setApproved(Boolean.parseBoolean(approvedToken));
        }
        
        return timeOff;
    }

    public String timeOffToCsv(List<TimeOff> timeOff) {
        if(timeOff.isEmpty()){
            return "null";
        }
        return timeOff.stream()
                      .map(to -> csvGenerator(to.getStart().toString(), to.getEnd().toString(), String.valueOf(to.isApproved())))
                      .collect(Collectors.joining("|"));
    }

    public String csvGenerator(String ...stringArr) {
        return String.join(",", stringArr);
    }
}