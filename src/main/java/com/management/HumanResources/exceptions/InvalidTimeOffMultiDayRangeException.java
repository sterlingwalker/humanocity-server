package com.management.HumanResources.exceptions;

public class InvalidTimeOffMultiDayRangeException extends InvalidTimeOffException {
    
    private static final long serialVersionUID = 1L; // Gets rid of a serialization warning.

    public InvalidTimeOffMultiDayRangeException() {
        super("Time off must start and end at 12am.");
    }
}