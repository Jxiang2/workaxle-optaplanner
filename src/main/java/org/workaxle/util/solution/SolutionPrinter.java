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

        final LocalDate shiftSStartDay = Data.generateStartEndDates()[0];
        final LocalDate shiftsEndDay = Data.generateStartEndDates()[1];

        int duration = 12;
        SolutionHandler.markInvalidByDailyBetween(schedule.getScore(), shiftAssignmentList, shiftSStartDay, shiftsEndDay);
        SolutionHandler.markInvalidByHourlyBetween(schedule.getScore(), shiftAssignmentList, duration);

        System.out.println("Total number of valid shift assignments: " + shiftAssignmentList.size() + "\n");

        LocalDate currentDay = shiftSStartDay;
        while (currentDay.isBefore(shiftsEndDay.plusDays(1))) {
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
