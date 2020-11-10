package com.management.HumanResources.exceptions;

public class InvalidEmployeeAvailabilityException extends IllegalArgumentException {

    private static final long serialVersionUID = 1L; // Gets rid of a serialization warning.

    public InvalidEmployeeAvailabilityException() {
        super("Invalid employee availability.");
    }

    public InvalidEmployeeAvailabilityException(String message) {
        super("Invalid employee availability: " + message);
    }
}