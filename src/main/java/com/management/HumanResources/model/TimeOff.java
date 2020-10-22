package com.management.HumanResources.model;

import java.time.*;

import lombok.Data;

@Data
public class TimeOff {
    private LocalDateTime start;
    private LocalDateTime end;
    private boolean approved;
}