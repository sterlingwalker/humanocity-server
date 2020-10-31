package com.management.HumanResources.exceptions;

import java.time.LocalDate;

public class NotMondayException extends IllegalArgumentException {

    private static final long serialVersionUID = 1L; // Gets rid of serialization warning.

    public NotMondayException(LocalDate date) {
        super(date + " is not a Monday.");
	}
}