package com.management.HumanResources.model;

import java.util.Arrays;

import lombok.Data;

@Data
public class ScheduleEntry {
    private long employeeId;
    private String[] availability; // String[] because that is what is sent to the client.

    public void setAvailability(DailyAvailability[] availability) {
        // Convert DailyAvailability[] to String[]
        this.availability = Arrays.stream(availability).map(a -> a.toString()).toArray(String[]::new);
    }
}