package com.management.HumanResources.exceptions;

public class InvalidTimeOffExpiredException extends InvalidTimeOffException {
    
    private static final long serialVersionUID = 1L; // Gets rid of a serialization warning.

    public InvalidTimeOffExpiredException() {
        super("The requested time off end was in the past. A time off must end in the future to be valid.");
    }
}