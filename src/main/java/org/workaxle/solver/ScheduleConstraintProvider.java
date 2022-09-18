package org.workaxle.solver;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.Joiners;
import org.workaxle.domain.ShiftAssignment;

import java.time.Duration;

public class ScheduleConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[]{
            oneShiftPerEmployeeGroupPerDay(constraintFactory),
            atLeast10HoursBetweenTwoShifts(constraintFactory),
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

    public Constraint atLeast10HoursBetweenTwoShifts(ConstraintFactory constraintFactory) {
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
            .penalize("atLeast10HoursBetweenTwoShifts", HardSoftScore.ONE_HARD);
    }

    public Constraint onOverlappingShifts(ConstraintFactory constraintFactory) {
        // an employee group can't work on 2 overlapping shifts
        return constraintFactory
            .forEach(ShiftAssignment.class)
            .join(
                ShiftAssignment.class,
                Joiners.equal(ShiftAssignment::getEmployeeGroup),
                Joiners.overlapping(ShiftAssignment::getStartDatetime, ShiftAssignment::getEndDatetime)
            )
            .penalize("onOverlappingShifts", HardSoftScore.ONE_HARD);
    }

}
