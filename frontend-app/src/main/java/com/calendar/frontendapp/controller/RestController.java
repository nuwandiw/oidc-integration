package com.calendar.frontendapp.controller;

import com.calendar.frontendapp.util.CalendarGenerator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@org.springframework.web.bind.annotation.RestController
public class RestController {

    @GetMapping("/api/calendar")
    @ResponseBody
    public Map<String, Object> calendarData() {
        return CalendarGenerator.generateWeekCalendar();
    }
}
