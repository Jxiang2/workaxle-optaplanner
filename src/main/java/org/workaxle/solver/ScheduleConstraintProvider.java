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
            atLeast12HoursBetweenTwoShifts(constraintFactory),
            atMostOneShiftPerDay(constraintFactory),
            evenlyShiftsDistribution(constraintFactory),
            onlyRequiredRole(constraintFactory)
        };
    }

    public Constraint atLeast12HoursBetweenTwoShifts(ConstraintFactory constraintFactory) {
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
            ).toHours() < 12)
            .penalize(
                "atLeast12HoursBetweenTwoShifts",
                HardSoftScore.ONE_HARD,
                (first, second) -> {
                    int breakLength = (int) Duration.between(
                        first.getShift().getEndAt(),
                        second.getShift().getStartAt()
                    ).toHours();
                    return 12 - breakLength;
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
                "atMostOneShiftPerDay",
                HardSoftScore.ONE_HARD
            );
    }

    public Constraint evenlyShiftsDistribution(ConstraintFactory constraintFactory) {
        // try to distribute the shifts evenly to employees

        return constraintFactory.forEach(ShiftAssignment.class)
            .groupBy(ShiftAssignment::getEmployee, ConstraintCollectors.count())
            .penalize(
                "evenlyShiftsDistribution",
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
                "onlyRequiredRole",
                HardSoftScore.ONE_HARD,
                (shiftEmployee) -> 12
            );
    }

}
