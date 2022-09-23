package org.workaxle.solver;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.*;
import org.workaxle.constants.Conflict;
import org.workaxle.domain.ShiftAssignment;

import java.time.Duration;
import java.util.Set;

public class ScheduleConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[]{
            atLeastNHoursBetweenTwoShifts(constraintFactory),
            atMostOneShiftPerDay(constraintFactory),
            evenlyShiftsDistribution(constraintFactory),
            onlyRequiredRole(constraintFactory)
        };
    }

    public Constraint atLeastNHoursBetweenTwoShifts(ConstraintFactory constraintFactory) {
        // any employee can only work 1 shift in 12 hours

        int n = 12;

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
            ).toHours() < n)
            .penalize(
                Conflict.AT_LEAST_N_HOURS_BETWEEN_TWO_SHIFTS.getCodeName(),
                HardSoftScore.ONE_HARD,
                (first, second) -> {
                    int breakLength = (int) Duration.between(
                        first.getShift().getEndAt(),
                        second.getShift().getStartAt()
                    ).toHours();
                    return n - breakLength;
                });
    }

    public Constraint atMostOneShiftPerDay(ConstraintFactory constraintFactory) {
        // an employee can only work 1 shift per day

        return constraintFactory
            .forEachUniquePair(
                ShiftAssignment.class,
                Joiners.equal(ShiftAssignment::getEmployee),
                Joiners.equal(ShiftAssignment::getDate)
            )
            .penalize(
                Conflict.AT_MOST_ONE_SHIFT_PER_DAY.getCodeName(),
                HardSoftScore.ONE_HARD
            );
    }

    public Constraint evenlyShiftsDistribution(ConstraintFactory constraintFactory) {
        // try to distribute the shifts evenly to employees

        return constraintFactory.forEach(ShiftAssignment.class)
            .groupBy(ShiftAssignment::getEmployee, ConstraintCollectors.count())
            .penalize(
                Conflict.EVENLY_SHIFT_DISTRIBUTION.getCodeName(),
                HardSoftScore.ONE_SOFT,
                (employee, shifts) -> shifts * shifts);
    }

    public Constraint onlyRequiredRole(ConstraintFactory constraintFactory) {
        // a shift can only be assigned to employees with roles it needs

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
                Conflict.ONLY_REQUIRED_ROLES.getCodeName(),
                HardSoftScore.ONE_HARD,
                (shiftEmployee) -> 12
            );
    }

}
