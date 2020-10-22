package com.management.HumanResources.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.management.HumanResources.dao.FirebaseDao;
import com.management.HumanResources.model.*;

import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ParseService {

    @Autowired private FirebaseDao firebase;

    public List<Employee> toEmployeeList(String json) {
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
        emp.setManagerID(Long.parseLong(obj.getString("managerID")));
        emp.setId(Long.parseLong(obj.getString("id")));
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

	public List<EmployeeTime> toEmployeeTimesList(String json) {
		JSONObject jsonList = new JSONObject(json);
        List<EmployeeTime> employeeTimes = new ArrayList<EmployeeTime>();
        jsonList.keySet().forEach(key -> 
            employeeTimes.add(jsonToEmployeeTime(jsonList.getJSONObject(key))));
        return employeeTimes;
    }
    
    public EmployeeTime jsonToEmployeeTime(JSONObject obj) {
        EmployeeTime et = new EmployeeTime();
        et.setEmployeeId(obj.getLong("employeeId"));
        et.setTotalHours(obj.getInt("totalHours"));
        et.setHoursRemaining(obj.getInt("hoursRemaining"));
        et.setAvailability(toEmployeeAvailabilityArray(obj.getString("availability")));
        et.setTimeOffs(toEmployeeTimeOffsList(obj.getString("timeOffs")));
        return et;
    }

    public String[] toEmployeeAvailabilityArray(String json) {
		JSONObject jsonList = new JSONObject(json);
        List<String> availabilityDays = new ArrayList<String>();
        jsonList.keySet().forEach(key -> 
            availabilityDays.add(jsonList.getString(key)));
        String[] availabilityArray = new String[availabilityDays.size()];
        return availabilityDays.toArray(availabilityArray);
    }

    public List<TimeOff> toEmployeeTimeOffsList(String json) {
		JSONObject jsonList = new JSONObject(json);
        List<TimeOff> timeOffs = new ArrayList<TimeOff>();
        jsonList.keySet().forEach(key -> 
            timeOffs.add(jsonToEmployeeTimeOff(jsonList.getJSONObject(key))));
        return timeOffs;
    }

    public TimeOff jsonToEmployeeTimeOff(JSONObject obj) {
        TimeOff to = new TimeOff();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        to.setStart(LocalDateTime.parse(obj.getString("start"), formatter));
        to.setEnd(LocalDateTime.parse(obj.getString("end"), formatter));
        to.setApproved(obj.getBoolean("approved"));
        return to;
    }
}