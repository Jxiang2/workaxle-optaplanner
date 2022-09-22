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
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class SolutionHandler {

    static Random r = new Random();

    public static void printResult(Schedule schedule) throws IOException, ParseException {
        List<ShiftAssignment> shiftAssignmentList = schedule.getShiftAssignmentList();

        final LocalDate shiftSStartDay = Data.generateStartEndDates()[0];
        final LocalDate shiftsEndDay = Data.generateStartEndDates()[1];

        int duration = 12;
        markInvalidDailyShiftGap(schedule.getScore(), shiftAssignmentList, shiftSStartDay, shiftsEndDay);
        markInvalidHourlyShiftGap(duration, shiftAssignmentList);
        shiftAssignmentList = getValidShiftAssignmentList(shiftAssignmentList);

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

    public static void markInvalidDailyShiftGap(
        HardSoftScore score,
        List<ShiftAssignment> shiftAssignmentList,
        LocalDate shiftsStartDay,
        LocalDate shiftsEndDay

    ) {
        if (score.getHardScore() < 0) {
            while (shiftsStartDay.isBefore(shiftsEndDay.plusDays(1))) {
                final LocalDate shiftsStartDayCopy = shiftsStartDay;
                List<ShiftAssignment> currentDateShiftAssignments = shiftAssignmentList
                    .stream()
                    .filter(shiftAssignment -> shiftAssignment.getDate().equals(shiftsStartDayCopy))
                    .collect(Collectors.toList());

                for (int i = 0; i < currentDateShiftAssignments.size(); i++) {
                    ShiftAssignment first = currentDateShiftAssignments.get(i);
                    for (int j = 0; j < currentDateShiftAssignments.size(); j++) {
                        ShiftAssignment second = currentDateShiftAssignments.get(j);
                        if (i != j &&
                            first.getEmployee().equals(second.getEmployee())
                        ) {
                            float chance = r.nextFloat();
                            if (chance <= 0.5f)
                                shiftAssignmentList
                                    .get(shiftAssignmentList.indexOf(first))
                                    .getConflicts()
                                    .put("dailyGap", true);
                            else
                                shiftAssignmentList
                                    .get(shiftAssignmentList.indexOf(second))
                                    .getConflicts()
                                    .put("dailyGap", true);
                        }
                    }
                }

                shiftsStartDay = shiftsStartDay.plusDays(1);
            }
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
                    float chance = r.nextFloat();
                    if (chance <= 0.5f)
                        first.getConflicts().put("hourlyGap", true);
                    else
                        second.getConflicts().put("hourlyGap", true);
                }
            }
        }
    }

    private static List<ShiftAssignment> getValidShiftAssignmentList(List<ShiftAssignment> shiftAssignmentList) {
        shiftAssignmentList = shiftAssignmentList
            .stream()
            .filter(shiftAssignment -> {
                Map<String, Boolean> conflicts = shiftAssignment.getConflicts();
                for (String type : conflicts.keySet()) {
                    if (conflicts.get(type))
                        return false;
                }
                return true;
            })
            .collect(Collectors.toList());
        return shiftAssignmentList;
    }

}
