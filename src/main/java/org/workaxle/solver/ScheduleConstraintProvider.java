package org.workaxle.solver;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.*;
import org.workaxle.constants.Conflict;
import org.workaxle.domain.ShiftAssignment;

import java.time.Duration;
import java.util.Set;

public class ScheduleConstraintProvider implements ConstraintProvider {

    int n = 12;

    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[]{
            atLeastNHoursBetweenTwoShifts(constraintFactory),
            atMostOneShiftPerDay(constraintFactory),
            evenlyShiftsDistribution(constraintFactory),
            onlyRequiredRole(constraintFactory)
        };
    }

    Constraint atLeastNHoursBetweenTwoShifts(ConstraintFactory constraintFactory) {
        // any employee can only work 1 shift in 12 hours

        return constraintFactory
            .forEachUniquePair(
                ShiftAssignment.class,
                Joiners.equal(ShiftAssignment::getEmployee),
                Joiners.lessThanOrEqual(
                    (shiftAssignment1) -> shiftAssignment1.getShift().getEndAt(),
                    (shiftAssignment2) -> shiftAssignment2.getShift().getStartAt()
                )
            )
            .filter((firstShift, secondShift) -> Duration.between(
                    firstShift.getShift().getEndAt(),
                    secondShift.getShift().getStartAt()
                ).toHours() < n
            )
            .penalize(
                Conflict.AT_LEAST_N_HOURS_BETWEEN_TWO_SHIFTS.getName(),
                HardSoftScore.ONE_HARD,
                (first, second) -> {
                    int breakLength = (int) Duration.between(
                        first.getShift().getEndAt(),
                        second.getShift().getStartAt()
                    ).toMinutes();
                    return n * 60 - breakLength;
                }
            );
    }

    Constraint atMostOneShiftPerDay(ConstraintFactory constraintFactory) {
        // an employee can only work 1 shift per day

        return constraintFactory
            .forEachUniquePair(
                ShiftAssignment.class,
                Joiners.equal(ShiftAssignment::getEmployee),
                Joiners.equal(ShiftAssignment::getDate)
            )
            .penalize(
                Conflict.AT_MOST_ONE_SHIFT_PER_DAY.getName(),
                HardSoftScore.ONE_HARD,
                (shiftAssignment1, shiftAssignment2) -> n * 60
            );
    }

    Constraint evenlyShiftsDistribution(ConstraintFactory constraintFactory) {
        // try to distribute the shifts evenly to employees

        return constraintFactory.forEach(ShiftAssignment.class)
            .groupBy(ShiftAssignment::getEmployee, ConstraintCollectors.count())
            .penalize(
                Conflict.EVENLY_SHIFT_DISTRIBUTION.getName(),
                HardSoftScore.ONE_SOFT,
                (employee, shifts) -> shifts * shifts
            );
    }

    Constraint onlyRequiredRole(ConstraintFactory constraintFactory) {
        // a shift can only be assigned to employees with roles it needs

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
                Conflict.ONLY_REQUIRED_ROLES.getName(),
                HardSoftScore.ONE_HARD,
                (shiftEmployee) -> (n * 60) * 10
            );
    }

}
