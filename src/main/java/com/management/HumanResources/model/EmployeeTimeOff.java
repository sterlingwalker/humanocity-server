package com.management.HumanResources.model;

import lombok.Data;

@Data
public class EmployeeTimeOff extends TimeOff {
    private long employeeId;
    private String firstName;
    private String lastName;

    public int getTimeOffId() {
        return (employeeId + start.toString() + end.toString()).hashCode(); // Problematic on a large scale but will work for us.
    }
}