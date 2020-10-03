package com.management.HumanResources.service;

import com.management.HumanResources.dao.FirebaseDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UpdateService {

    @Autowired private FirebaseDao firebase;
    @Autowired private TimeOffService timeOffService;

    //TODO Here will be the logic to check if an employee exists
    // along with other checks before it is pushed to the database

}