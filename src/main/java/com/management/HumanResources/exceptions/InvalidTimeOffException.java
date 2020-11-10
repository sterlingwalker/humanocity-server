package com.management.HumanResources.exceptions;

public class InvalidTimeOffException extends Exception {

    private static final long serialVersionUID = 1L; // Gets rid of a serialization warning.

    public InvalidTimeOffException() {
        super("Invalid time off.");
    }

    public InvalidTimeOffException(String message) {
        super("Invalid time off: " + message);
    }
}