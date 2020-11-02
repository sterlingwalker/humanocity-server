package com.management.HumanResources.exceptions;

import com.management.HumanResources.model.DailyAvailability;

public class InvalidEmployeeAvailabilityStartAfterEndException extends InvalidEmployeeAvailabilityException {
    
    private static final long serialVersionUID = 1L; // Gets rid of a serialization warning.

    public InvalidEmployeeAvailabilityStartAfterEndException() {
        super("Availability start time was after the end time. Availability start time must be before the end time.");
    }

    public InvalidEmployeeAvailabilityStartAfterEndException(DailyAvailability givenDailyAvailability) {
        super("Availability start time was after the end time for the given availability: " + givenDailyAvailability 
        + ". Availability start time must be before the end time.");
    }
}