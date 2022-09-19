package org.workaxle.solver;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.*;
import org.workaxle.domain.ShiftAssignment;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

public class ScheduleConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[]{
            oneShiftPerEmployeePerDay(constraintFactory),
            atLeast12HoursBetweenTwoShifts(constraintFactory),
            onOverlappingShifts(constraintFactory),
            evenlyShiftsDistribution(constraintFactory),
            requiredRoles(constraintFactory)
        };
    }

    public Constraint oneShiftPerEmployeePerDay(ConstraintFactory constraintFactory) {
        // an employee can be assigned to at most 1 shiftAssignment at the same day

        return constraintFactory
            .forEachUniquePair(
                ShiftAssignment.class,
                Joiners.equal(ShiftAssignment::getEmployee),
                Joiners.equal(ShiftAssignment::getDate)
            )
            .penalize("oneShiftPerEmployeeGroupPerDay", HardSoftScore.ONE_HARD);
    }

    public Constraint atLeast12HoursBetweenTwoShifts(ConstraintFactory constraintFactory) {
        // any employee can only work 1 shiftAssignment in 12 hours

        return constraintFactory
            .forEachUniquePair(
                ShiftAssignment.class,
                Joiners.equal(ShiftAssignment::getEmployee),
                Joiners.lessThanOrEqual(
                    ShiftAssignment::getEndDateTime,
                    ShiftAssignment::getStartDatetime
                )
            )
            .filter((firstShift, secondShift) -> Duration.between(
                firstShift.getEndDateTime(),
                secondShift.getStartDatetime()
            ).toHours() < 12)
            .penalize("atLeast12HoursBetweenTwoShifts", HardSoftScore.ONE_HARD, (first, second) -> {
                int breakLength = (int) Duration.between(
                    first.getEndDateTime(),
                    second.getStartDatetime()
                ).toMinutes();
                return 12 * 60 - breakLength;
            });
    }

    public Constraint onOverlappingShifts(ConstraintFactory constraintFactory) {
        // an employee can't work on 2 overlapping shiftAssignments

        return constraintFactory
            .forEachUniquePair(
                ShiftAssignment.class,
                Joiners.equal(ShiftAssignment::getEmployee),
                Joiners.overlapping(ShiftAssignment::getStartDatetime, ShiftAssignment::getEndDateTime)
            )
            .penalize(
                "onOverlappingShifts",
                HardSoftScore.ONE_HARD,
                ScheduleConstraintProvider::getMinuteOverlap
            );
    }

    public Constraint evenlyShiftsDistribution(ConstraintFactory constraintFactory) {
        // try to distribute the shifts evenly to employees

        return constraintFactory.forEach(ShiftAssignment.class)
            .groupBy(ShiftAssignment::getEmployee, ConstraintCollectors.count())
            .penalize(
                "evenlyShiftsDistribution",
                HardSoftScore.ONE_SOFT,
                (employeeGroup, shifts) -> shifts * shifts);
    }

    private Constraint requiredRoles(ConstraintFactory constraintFactory) {
        // a shiftAssignment can only take employees with roles it needs

        return constraintFactory
            .forEach(ShiftAssignment.class)
            .filter(shiftAssignment -> {
                Map<String, Integer> requiredRoles = shiftAssignment.getShift().getRequiredRoles();
                Map<String, Integer> providedRoles = shiftAssignment.getEmployee().getRoles();

                return !requiredRoles.equals(providedRoles);
            })
            .penalize("requiredRoles", HardSoftScore.ONE_HARD);
    }

    private static int getMinuteOverlap(ShiftAssignment sa1, ShiftAssignment sa2) {
        // The overlap of two timeslot occurs in the range common to both timeslots.
        // Both timeslots are active after the higher of their two start times,
        // and before the lower of their two end times.
        LocalDateTime shift1Start = sa1.getStartDatetime();
        LocalDateTime shift1End = sa1.getEndDateTime();
        LocalDateTime shift2Start = sa2.getStartDatetime();
        LocalDateTime shift2End = sa2.getEndDateTime();
        return (int) Duration.between((shift1Start.compareTo(shift2Start) > 0) ? shift1Start : shift2Start,
            (shift1End.compareTo(shift2End) < 0) ? shift1End : shift2End).toMinutes();
    }

}
