package org.workaxle.solver.base;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintCollectors;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.Joiners;
import org.workaxle.constants.Conflict;
import org.workaxle.domain.ShiftAssignment;
import org.workaxle.solver.ConstraintProviderUtils;

import java.time.Duration;
import java.util.Set;

import static org.workaxle.solver.ConstraintProviderUtils.convertMinutesToHours;

public class BaseConstraintProvider implements org.optaplanner.core.api.score.stream.ConstraintProvider {

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
                    long breakLength = Duration.between(
                        first.getShift().getEndAt(),
                        second.getShift().getStartAt()
                    ).toMinutes();

                    return n - convertMinutesToHours(breakLength);
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
                (shiftAssignment1, shiftAssignment2) -> n
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
                (shiftEmployee) -> n * 10
            );
    }


    Constraint noOverlappingShifts(ConstraintFactory constraintFactory) {
        // no employee takes 2 or more shifts at the same time

        return constraintFactory
            .forEachUniquePair(ShiftAssignment.class,
                Joiners.equal(ShiftAssignment::getEmployee),
                Joiners.overlapping(
                    (shiftAssignment) -> shiftAssignment.getShift().getStartAt(),
                    (shiftAssignment) -> shiftAssignment.getShift().getEndAt()
                )
            )
            .penalize(
                Conflict.No_OVERLAPPING_SHIFTS.getName(),
                HardSoftScore.ONE_HARD,
                ConstraintProviderUtils::getHourlyOverlap
            );
    }

}
