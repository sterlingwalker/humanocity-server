package com.management.HumanResources.exceptions;

public class InvalidTimeOffNotEnoughWorkersException extends InvalidTimeOffException {
    
    private static final long serialVersionUID = 1L; // Gets rid of a serialization warning.

    public InvalidTimeOffNotEnoughWorkersException() {
        super("Not enough workers will be working during the requested time off.");
    }
}