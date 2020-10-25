package com.management.HumanResources.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

    private long id;
    private String firstName;
    private String lastName;
    private String email;
    private int salary;
    private String position;
    private Address address;
    private long managerID;
    private String dept;

    public String uniqueData() {
        return (new String(firstName+lastName+email)).toLowerCase();
    }

}