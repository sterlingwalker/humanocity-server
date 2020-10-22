package com.management.HumanResources.model;

import lombok.Data;

@Data
public class ScheduleEntry {
    private long employeeId;
    private String[] availability;
}