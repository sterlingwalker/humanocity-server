package com.management.HumanResources.exceptions;

public class InvalidTimeOffNotEnoughRemainingHoursException extends InvalidTimeOffException {
    
    private static final long serialVersionUID = 1L; // Gets rid of a serialization warning.

    public InvalidTimeOffNotEnoughRemainingHoursException() {
        super("The requesting employee does not have enough remaining time off hours for this request.");
    }
}