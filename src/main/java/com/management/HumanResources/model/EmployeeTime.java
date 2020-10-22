package com.management.HumanResources.model;

import java.util.List;
import lombok.Data;

@Data
public class EmployeeTime {

    private long employeeId;
    private double hoursRemaining;
    private double totalHours;
    private List<TimeOff> timeOffs;
    private String[] availability;
}