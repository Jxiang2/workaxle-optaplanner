package org.workaxle.solver;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.Joiners;
import org.workaxle.domain.ShiftAssignment;

import java.time.Duration;
import java.util.Map;

public class ScheduleConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[]{
            oneShiftPerEmployeePerDay(constraintFactory),
            atLeast12HoursBetweenTwoShifts(constraintFactory),
            evenlyShiftsDistribution(constraintFactory),
            requiredRoles(constraintFactory)
        };
    }

    public Constraint oneShiftPerEmployeePerDay(ConstraintFactory constraintFactory) {
        // an employee can be assigned to at most 1 shiftAssignment at the same day

        return constraintFactory
            .forEachUniquePair(
                ShiftAssignment.class,
                Joiners.equal(ShiftAssignment::getEmployeeGroup),
                Joiners.equal(ShiftAssignment::getDate)
            )
            .penalize("oneShiftPerEmployeeGroupPerDay", HardSoftScore.ONE_HARD);
    }

    public Constraint atLeast12HoursBetweenTwoShifts(ConstraintFactory constraintFactory) {
        // any employee can only work 1 shiftAssignment in 12 hours

        return constraintFactory
            .forEachUniquePair(
                ShiftAssignment.class,
                Joiners.equal(ShiftAssignment::getEmployeeGroup),
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
                ).toHours();
                return 12 - breakLength;
            });
    }

    public Constraint evenlyShiftsDistribution(ConstraintFactory constraintFactory) {
        // try to distribute the shifts evenly to employees

        return constraintFactory
            // select a shiftAssignment
            .forEach(ShiftAssignment.class)
            // and pair it with another shiftAssignment
            .join(
                ShiftAssignment.class,
                Joiners.equal(ShiftAssignment::getShift),
                Joiners.equal(ShiftAssignment::getEmployeeGroup),
                Joiners.lessThan(ShiftAssignment::getId)
            )
            .penalize("evenlyShiftsDistribution", HardSoftScore.ONE_SOFT);
    }

    public Constraint requiredRoles(ConstraintFactory constraintFactory) {
        // a shiftAssignment can only take employees with roles it needs

        return constraintFactory
            .forEach(ShiftAssignment.class)
            .filter(shiftAssignment -> {
                Map<String, Integer> requiredRoles = shiftAssignment.getShift().getRequiredRoles();
                Map<String, Integer> providedRoles = shiftAssignment.getEmployeeGroup().getRoles();

                // check if requiredRoles is a subset of providedRoles
                return !requiredRoles.equals(providedRoles);
            })
            .penalize(
                "requiredRoles",
                HardSoftScore.ONE_HARD,
                (shiftAssignment) -> 10
            );
    }

}
