package org.workaxle.util;

import org.json.simple.parser.ParseException;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.workaxle.dao.Data;
import org.workaxle.domain.Schedule;
import org.workaxle.domain.ShiftAssignment;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class SolutionHandler {

    public static void printResult(Schedule schedule) throws IOException, ParseException {
        List<ShiftAssignment> shiftAssignmentList = schedule.getShiftAssignmentList();

        final LocalDate shiftSStartDay = Data.generateStartEndDates()[0];
        final LocalDate shiftsEndDay = Data.generateStartEndDates()[1];

        int duration = 12;
        markInvalidHourlyShiftGap(duration, shiftAssignmentList);
        markInvalidDailyShiftGap(schedule.getScore(), shiftAssignmentList, shiftSStartDay, shiftsEndDay);

        System.out.println("Total number of shift assignments: " + shiftAssignmentList.size() + "\n");

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

    public static void markInvalidHourlyShiftGap(
        int duration,
        List<ShiftAssignment> shiftAssignmentList
    ) {
        for (ShiftAssignment first : shiftAssignmentList) {
            LocalDateTime firstEndAt = first.getShift().getEndAt();
            for (ShiftAssignment second : shiftAssignmentList) {
                LocalDateTime secondStartAt = second.getShift().getStartAt();
                if (first.getEmployee().equals(second.getEmployee()) &&
                    secondStartAt.isAfter(firstEndAt) &&
                    Math.abs(Duration.between(firstEndAt, secondStartAt).toHours()) < duration
                ) {
                    first.getConflicts().put("hourlyGap", false);
                    second.getConflicts().put("hourlyGap", false);
                }
            }
        }
    }

    public static void markInvalidDailyShiftGap(
        HardSoftScore score,
        List<ShiftAssignment> shiftAssignmentList,
        LocalDate shiftsStartDay,
        LocalDate shiftsEndDay

    ) {
        if (score.getHardScore() < 0) {
            while (shiftsStartDay.isBefore(shiftsEndDay.plusDays(1))) {
                LocalDate finalShiftSStartDay = shiftsStartDay;
                List<ShiftAssignment> currentDateShiftAssignments = shiftAssignmentList
                    .stream()
                    .filter(shiftAssignment -> shiftAssignment.getDate().equals(finalShiftSStartDay))
                    .collect(Collectors.toList());

                for (int i = 0; i < currentDateShiftAssignments.size(); i++) {
                    ShiftAssignment first = currentDateShiftAssignments.get(i);
                    for (int j = 0; j < currentDateShiftAssignments.size(); j++) {
                        ShiftAssignment second = currentDateShiftAssignments.get(j);
                        if (i != j &&
                            first.equals(second)
                        ) {
                            first.getConflicts().put("dailyGap", false);
                            second.getConflicts().put("dailyGap", false);
                        }
                    }
                }

                shiftsStartDay = shiftsStartDay.plusDays(1);
            }
        }
    }

}
