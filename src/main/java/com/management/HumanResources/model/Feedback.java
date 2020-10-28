package com.management.HumanResources.model;

import lombok.Data;

@Data
public class Feedback {
    private long id;
    private String type;
    private String description;
}