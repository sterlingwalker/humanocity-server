package com.management.HumanResources.service;

import java.time.*;

import com.management.HumanResources.dao.FirebaseDao;
import com.management.HumanResources.model.TimeOff;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TimeOffService {

    @Autowired private FirebaseDao firebase;
    
    // Max time off hours that can be used per day.
    private final double DAILY_HOURS = 8;

    //TODO: This class will hold the logic that checks for time off conflicts in the schedule
    
    /**
     * Gets the number of hours for the time off.
     */
    /*public double getNumberOfTimeOffHours(TimeOff timeOff) {
        /*
        // Calculate the seconds difference between the time off end and start times.
        long millisDiff = timeOff.getEnd().atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli() 
            - timeOff.getStart().atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
        
        // Return the hour difference.
        return millisDiff / 1000.0 / 3600;
    }*/
}