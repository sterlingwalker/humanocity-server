package com.management.HumanResources.service;

import java.util.ArrayList;
import java.util.List;

import com.management.HumanResources.dao.FirebaseDao;
import com.management.HumanResources.model.Employee;
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
        //emp.setAddress(address); TODO
        return emp;
    }

    //TODO: Add parsing for TimeOff list

}