package com.management.HumanResources.exceptions;

public class InvalidTimeOffDuplicateException extends InvalidTimeOffException {
    
    private static final long serialVersionUID = 1L; // Gets rid of a serialization warning.

    public InvalidTimeOffDuplicateException() {
        super("The requested time off is a duplicate of an already requested time off.");
    }
}