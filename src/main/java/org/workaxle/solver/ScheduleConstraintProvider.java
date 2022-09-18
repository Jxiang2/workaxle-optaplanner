package org.workaxle.solver;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.Joiners;
import org.workaxle.domain.ShiftAssignment;

import java.time.Duration;
import java.time.LocalDateTime;

public class ScheduleConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[]{
            oneShiftPerEmployeeGroupPerDay(constraintFactory),
            atLeast12HoursBetweenTwoShifts(constraintFactory),
            onOverlappingShifts(constraintFactory)
        };
    }

    public Constraint oneShiftPerEmployeeGroupPerDay(ConstraintFactory constraintFactory) {
        // an employee group can be assigned to at most 1 shiftAssignment at the same day

        return constraintFactory
            // select a shiftAssignment
            .forEach(ShiftAssignment.class)
            // and pair it with another shiftAssignment
            .join(
                ShiftAssignment.class,
                Joiners.equal(ShiftAssignment::getEmployeeGroup), // same employee group
                Joiners.equal(ShiftAssignment::getDate), // the same day
                Joiners.lessThan(ShiftAssignment::getId) // pair uniquely
            )
            // then penalize each pair with a hard weight
            .penalize("oneShiftPerEmployeeGroupPerDay", HardSoftScore.ONE_HARD);
    }

    public Constraint atLeast12HoursBetweenTwoShifts(ConstraintFactory constraintFactory) {
        // any employee group can only work 1 shift in 12 hours

        return constraintFactory
            // select a shiftAssignment
            .forEach(ShiftAssignment.class)
            // and pair it with another shiftAssignment
            .join(
                ShiftAssignment.class,
                Joiners.equal(ShiftAssignment::getEmployeeGroup), // same employee group
                Joiners.lessThanOrEqual( // endDatetime of first < startDatetime of second
                    ShiftAssignment::getEndDatetime,
                    ShiftAssignment::getStartDatetime
                ),
                Joiners.lessThan(ShiftAssignment::getId) // pair uniquely
            )
            .filter((firstShift, secondShift) -> Duration.between(
                firstShift.getEndDatetime(),
                secondShift.getStartDatetime()
            ).toHours() < 12)
            // then penalize each pair with a hard weight
            .penalize("atLeast12HoursBetweenTwoShifts", HardSoftScore.ONE_HARD, (first, second) -> {
                int breakLength = (int) Duration.between(first.getEndDatetime(), second.getStartDatetime()).toMinutes();
                return 12 * 60 - breakLength;
            });
    }

    public Constraint onOverlappingShifts(ConstraintFactory constraintFactory) {
        // an employee group can't work on 2 overlapping shifts
        return constraintFactory
            .forEach(ShiftAssignment.class)
            .join(
                ShiftAssignment.class,
                Joiners.equal(ShiftAssignment::getEmployeeGroup),
                Joiners.overlapping(ShiftAssignment::getStartDatetime, ShiftAssignment::getEndDatetime),
                Joiners.lessThan(ShiftAssignment::getId) // pair uniquely
            )
            .penalize(
                "onOverlappingShifts",
                HardSoftScore.ONE_HARD,
                ScheduleConstraintProvider::getMinuteOverlap
            );
    }

    private static int getMinuteOverlap(ShiftAssignment sa1, ShiftAssignment sa2) {
        // The overlap of two timeslot occurs in the range common to both timeslots.
        // Both timeslots are active after the higher of their two start times,
        // and before the lower of their two end times.
        LocalDateTime shift1Start = sa1.getStartDatetime();
        LocalDateTime shift1End = sa1.getEndDatetime();
        LocalDateTime shift2Start = sa2.getStartDatetime();
        LocalDateTime shift2End = sa2.getEndDatetime();
        return (int) Duration.between((shift1Start.compareTo(shift2Start) > 0) ? shift1Start : shift2Start,
            (shift1End.compareTo(shift2End) < 0) ? shift1End : shift2End).toMinutes();
    }

}
