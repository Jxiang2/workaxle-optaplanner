package org.workaxle.newSolver;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.Joiners;
import org.workaxle.newDomain.ShiftAssignment;

import java.time.Duration;
import java.util.Set;

public class ShiftScheduleConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[]{
            // an employee can only work 1 shift per day
            atMostOneShiftPerDay(constraintFactory),

            // any employee can only work 1 shift in 12 hours
            atLeast12HoursBetweenTwoShifts(constraintFactory),

            // try to distribute the shifts evenly to employees
            evenlyShiftsDistribution(constraintFactory),

            // a shift can only be assigned to employees with roles it needs
            requiredRole(constraintFactory)
        };
    }

    private Constraint atMostOneShiftPerDay(ConstraintFactory constraintFactory) {
        return constraintFactory
            .forEachUniquePair(
                ShiftAssignment.class,
                Joiners.equal(ShiftAssignment::getEmployee),
                Joiners.equal(ShiftAssignment::getDate)
            )
            .penalize(
                "atMostOneShiftPerDay",
                HardSoftScore.ONE_HARD
            );
    }

    private Constraint atLeast12HoursBetweenTwoShifts(ConstraintFactory constraintFactory) {
        return constraintFactory
            .forEachUniquePair(
                ShiftAssignment.class,
                Joiners.equal(ShiftAssignment::getEmployee),
                Joiners.lessThanOrEqual(
                    ShiftAssignment::getEndDatetime,
                    ShiftAssignment::getStartDatetime
                )
            )
            .filter((first, second) -> Duration.between(
                first.getEndDatetime(),
                second.getStartDatetime()
            ).toHours() < 12)
            .penalize(
                "atLeast12HoursBetweenTwoShifts",
                HardSoftScore.ONE_HARD,
                (first, second) -> {
                    int breakLength = (int) Duration.between(
                        first.getEndDatetime(),
                        second.getStartDatetime()
                    ).toHours();
                    return 12 - breakLength;
                }
            );
    }

    private Constraint evenlyShiftsDistribution(ConstraintFactory constraintFactory) {
        return constraintFactory
            .forEachUniquePair(
                ShiftAssignment.class,
                Joiners.equal(ShiftAssignment::getShift),
                Joiners.equal(ShiftAssignment::getEmployee)
            )
            .penalize(
                "evenlyShiftsDistribution",
                HardSoftScore.ONE_SOFT
            );
    }

    private Constraint requiredRole(ConstraintFactory constraintFactory) {
        return constraintFactory
            .forEach(ShiftAssignment.class)
            .filter(
                shiftAssignment -> {
                    String requiredRole = shiftAssignment.getRole();
                    Set<String> providedRoles = shiftAssignment.getEmployee().getRoleSet();

                    return !providedRoles.contains(requiredRole);
                }
            )
            .penalize(
                "requiredRoles",
                HardSoftScore.ONE_HARD,
                (shiftAssignment) -> 10
            );
    }

}
