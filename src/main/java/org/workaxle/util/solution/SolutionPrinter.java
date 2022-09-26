package org.workaxle.util.solution;

import org.json.simple.parser.ParseException;
import org.workaxle.bootstrap.Data;
import org.workaxle.domain.Schedule;
import org.workaxle.domain.ShiftAssignment;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class SolutionPrinter {

    public static void printResult(Schedule schedule) throws IOException, ParseException {
        List<ShiftAssignment> shiftAssignmentList = schedule.getShiftAssignmentList();

        final LocalDate startDay = Data.generateStartEndDates()[0];
        final LocalDate endDay = Data.generateStartEndDates()[1];

        new SolutionHandler(schedule.getScore(), shiftAssignmentList)
            .markInvalidDueToByDailyBetween()
            .markInvalidDueToHourlyBetween(schedule.getSettings().getHoursBetweenShifts())
            .markInvalidDueToRequiredRole()
        ;

        System.out.println(
            "Total number of valid shift assignments: " + shiftAssignmentList.size() + "\n"
        );

        LocalDate currentDay = startDay;
        while (currentDay.isBefore(endDay.plusDays(1))) {
            for (ShiftAssignment shiftAssignment : shiftAssignmentList) {
                if (shiftAssignment.getDate().equals(currentDay)) {
                    System.out.println(shiftAssignment);
                }
            }
            currentDay = currentDay.plusDays(1);
            System.out.println();
        }
    }

}
