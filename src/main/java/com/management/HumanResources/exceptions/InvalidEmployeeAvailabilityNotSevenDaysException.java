package com.management.HumanResources.exceptions;

public class InvalidEmployeeAvailabilityNotSevenDaysException extends InvalidEmployeeAvailabilityException {
    
    private static final long serialVersionUID = 1L; // Gets rid of a serialization warning.

    public InvalidEmployeeAvailabilityNotSevenDaysException() {
        super("Not seven days of availability were given. Availability must be given for all seven days of the week.");
    }

    public InvalidEmployeeAvailabilityNotSevenDaysException(int givenDaysCount) {
        super(givenDaysCount + " days of availability were given. Availability must be given for all seven days of the week.");
    }
}