package com.calendar.frontendapp.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class for generating dummy calendar data for a one-week view.
 */
public class CalendarGenerator {

    /**
     * Generates a dummy one-week calendar with hardcoded values.
     *
     * @return a Map containing week calendar data
     */
    public static Map<String, Object> generateWeekCalendar() {
        Map<String, Object> weekCalendar = new HashMap<>();
        weekCalendar.put("weekStartDate", "2025-12-08");
        weekCalendar.put("weekEndDate", "2025-12-14");
        weekCalendar.put("weekNumber", 50);
        weekCalendar.put("year", 2025);

        // Generate dummy days for the week
        List<Map<String, Object>> days = new ArrayList<>();

        String[] dayNames = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        int[] dayOfMonths = {8, 9, 10, 11, 12, 13, 14};

        for (int i = 0; i < 7; i++) {
            Map<String, Object> dayData = new HashMap<>();
            dayData.put("date", "2025-12-" + String.format("%02d", dayOfMonths[i]));
            dayData.put("dayName", dayNames[i]);
            dayData.put("dayOfMonth", dayOfMonths[i]);
            dayData.put("isToday", i == 3); // Thursday is today
            dayData.put("isWeekend", i >= 5); // Saturday and Sunday are weekends

            days.add(dayData);
        }

        weekCalendar.put("days", days);
        return weekCalendar;
    }
}
