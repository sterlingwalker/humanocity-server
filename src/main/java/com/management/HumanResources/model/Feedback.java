package com.management.HumanResources.model;

import org.json.JSONObject;

public class Feedback {
    private int id;
    private String type;
    private String description;

    public String feedbackData() {
        JSONObject obj = new JSONObject();
        obj.put("id", id);
        obj.put("type", type);
        obj.put("description", description);
        return obj.toString();
    }
}
