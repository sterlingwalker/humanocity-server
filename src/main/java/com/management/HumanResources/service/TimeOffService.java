package com.management.HumanResources.service;

import java.util.List;

import com.management.HumanResources.dao.FirebaseDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TimeOffService {

    @Autowired private FirebaseDao firebase;

    //TODO: This class will hold the logic that checks for time off conflicts in the schedule

}