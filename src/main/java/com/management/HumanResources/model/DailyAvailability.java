package com.management.HumanResources.model;

import lombok.Data;

@Data
public class DailyAvailability {
    
    private int start;
    private int end;
    private boolean isModified;

    public boolean isOff() {
        return start == 0 && end == 0;
    }

    /**
     * Return true if the time off start and end are in the correct order and on the same week.
     */
	public boolean isLegal() {
        return isOff() || start < end;
    }

    /**
     * Returns the number of availability hours.
     */
    public int getHours() {
        return end - start;
    }

    @Override
    public String toString() {
        if (isOff())
            return "null";
        return hourToAmPmString(start) + "-" + hourToAmPmString(end);
    }

    /**
     * Converts an hour value to AM/PM no-space string. (e.g. 23 -> 11PM)
     * @param hour An hour value between 0 and 23
     */
    private String hourToAmPmString(int hour) {
        return String.valueOf(hour % 12) + (hour < 12 ? "A" : "P") + "M";
    }
}