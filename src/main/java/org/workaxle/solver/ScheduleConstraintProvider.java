package org.workaxle.solver;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.*;
import org.workaxle.domain.ShiftAssignment;

import java.time.Duration;
import java.util.Set;

public class ScheduleConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[]{
            // any employee can only work 1 shift in 12 hours
            atLeast12HoursBetweenTwoShifts(constraintFactory),

            // an employee can only work 1 shift per day
            atMostOneShiftPerDay(constraintFactory),

            // try to distribute the shifts evenly to employees
            evenlyShiftsDistribution(constraintFactory),

            // a shift can only be assigned to employees with roles it needs
            requiredRole(constraintFactory)
        };
    }

    private Constraint atLeast12HoursBetweenTwoShifts(ConstraintFactory constraintFactory) {
        return constraintFactory
            .forEachUniquePair(
                ShiftAssignment.class,
                Joiners.equal(ShiftAssignment::getEmployee),
                Joiners.lessThanOrEqual(ShiftAssignment::getEndDatetime, ShiftAssignment::getStartDatetime)
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

    private Constraint evenlyShiftsDistribution(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(ShiftAssignment.class)
            .groupBy(ShiftAssignment::getEmployee, ConstraintCollectors.count())
            .penalize(
                "evenlyShiftsDistribution",
                HardSoftScore.ONE_SOFT,
                (employee, shifts) -> (int) (shifts * Math.log(shifts)));
    }

    private Constraint requiredRole(ConstraintFactory constraintFactory) {
        return constraintFactory
            .forEach(ShiftAssignment.class)
            .filter(
                shiftEmployee -> {
                    String requiredRole = shiftEmployee.getRole();
                    Set<String> providedRoles = shiftEmployee.getEmployee().getRoleSet();

                    return !providedRoles.contains(requiredRole);
                }
            )
            .penalize(
                "requiredRoles",
                HardSoftScore.ONE_HARD,
                (shiftEmployee) -> 12
            );
    }

}
