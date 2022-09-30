package org.workaxle.solver.optional;

import org.workaxle.domain.Employee;
import org.workaxle.domain.Settings;
import org.workaxle.domain.ShiftAssignment;

import java.time.Duration;

import static org.workaxle.util.common.TimeUtil.convertMinutesToHours;

public class OptionalConstraintPenalty {

    static int atMostNHoursWorkPenalty(Employee employee, int totalHours, Settings settings) {
        return totalHours - settings.getMaxHoursOfWork();
    }

    static int noShiftOnWeekendsPenalty(ShiftAssignment shiftAssignment, Settings settings) {
        return shiftAssignment.getShiftDurationInHours();
    }

    static int atLeastNHoursBetweenTwoShiftsPenalty(
        ShiftAssignment first,
        ShiftAssignment second,
        Settings settings
    ) {
        final long breakLength = Math.abs(Duration.between(
            first.getShift().getEndAt(),
            second.getShift().getStartAt()
        ).toMinutes());

        return Math.abs(settings.getHoursBetweenShifts() - convertMinutesToHours(breakLength));
    }

}
