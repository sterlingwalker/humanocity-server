package com.management.HumanResources.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Address {
    private String city;
    private String state;
    private String street;
    private String zipcode;
}