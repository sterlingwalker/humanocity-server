package com.management.HumanResources.exceptions;

public class InvalidTimeOffNotSameWeekException extends InvalidTimeOffException {
    
    private static final long serialVersionUID = 1L; // Gets rid of a serialization warning.

    public InvalidTimeOffNotSameWeekException() {
        super("Time off start and end must occur on the same week or end on at most 12 am on next Monday.");
    }
}