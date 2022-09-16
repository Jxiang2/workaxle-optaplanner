package org.workaxle.example.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Data
@AllArgsConstructor
public class Timeslot {

    private DayOfWeek dayOfWeek;

    private LocalTime startTime;

    private LocalTime endTime;

    @Override
    public String toString() {
        return dayOfWeek + " " + startTime;
    }

}
