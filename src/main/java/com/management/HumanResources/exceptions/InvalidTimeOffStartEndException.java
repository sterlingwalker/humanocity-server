package com.management.HumanResources.exceptions;

public class InvalidTimeOffStartEndException extends InvalidTimeOffException {

    private static final long serialVersionUID = 1L; // Gets rid of a serialization warning.
    
    public InvalidTimeOffStartEndException() {
        super("Time off must start and/or end at the employee availability start and/or end time.");
    }
}