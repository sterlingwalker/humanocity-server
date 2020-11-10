package com.management.HumanResources.exceptions;

public class InvalidTimeOffStartAfterEndException extends InvalidTimeOffException {
    
    private static final long serialVersionUID = 1L; // Gets rid of a serialization warning.

    public InvalidTimeOffStartAfterEndException() {
        super("Time off start date and time must occur before time off end date and time.");
    }
}