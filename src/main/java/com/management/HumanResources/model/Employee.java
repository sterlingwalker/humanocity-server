package com.management.HumanResources.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private int salary;
    private String position;
    private Address address;
    private int managerID;
    private String department;

}