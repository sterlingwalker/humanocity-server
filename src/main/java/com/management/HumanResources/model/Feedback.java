package com.management.HumanResources.model;

import lombok.Data;

@Data
public class Feedback {
    private long employeeId;
    private long feedbackId;
    private String type;
    private String description;
}