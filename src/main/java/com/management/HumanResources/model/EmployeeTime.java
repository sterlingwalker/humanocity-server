package com.management.HumanResources.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import org.json.JSONObject;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeeTime {

    private long employeeId;
    private double hoursRemaining;
    private double totalHours;
    private List<TimeOff> timeOffs;
    private String[] availability;
    private String csvTimeOff;
    private String csvAvailability;

    public String toJson() {
        JSONObject obj = new JSONObject();
        obj.put("employeeId", employeeId);
        obj.put("hoursRemaining", hoursRemaining);
        obj.put("totalHours", totalHours);
        obj.put("timeOffs", csvTimeOff);
        obj.put("availability", csvAvailability);
        return obj.toString();
    }
}