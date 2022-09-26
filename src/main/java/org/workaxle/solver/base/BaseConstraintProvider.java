package org.workaxle.solver.base;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.*;
import org.workaxle.constants.Conflict;
import org.workaxle.domain.Settings;
import org.workaxle.domain.ShiftAssignment;
import org.workaxle.solver.ConstraintProviderUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Set;

import static org.workaxle.solver.ConstraintProviderUtils.convertMinutesToHours;

public class BaseConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[]{
            atLeastNHoursBetweenTwoShifts(constraintFactory),
            atMostOneShiftPerDay(constraintFactory),
            evenlyShiftsDistribution(constraintFactory),
            onlyRequiredRole(constraintFactory),
            noOverlappingShifts(constraintFactory),
        };
    }

    Constraint atLeastNHoursBetweenTwoShifts(ConstraintFactory constraintFactory) {
        // any employee can only work 1 shift in N hours

        return constraintFactory
            .forEachUniquePair(
                ShiftAssignment.class,
                Joiners.equal(ShiftAssignment::getEmployee)
            )
            .join(Settings.class)
            .filter((firstShift, secondShift, settings) -> {
                    final int constraint = settings.getHoursBetweenShifts();
                    final LocalDateTime firstStartAt = firstShift.getShift().getStartAt();
                    final LocalDateTime firstEndAt = firstShift.getShift().getEndAt();
                    final LocalDateTime secondStartAt = secondShift.getShift().getStartAt();
                    final LocalDateTime secondEndAt = secondShift.getShift().getEndAt();

                    return Math.abs(Duration.between(firstEndAt, secondStartAt).toHours()) < constraint
                        || Math.abs(Duration.between(secondEndAt, firstStartAt).toHours()) < constraint;
                }
            )
            .penalize(
                Conflict.AT_LEAST_N_HOURS_BETWEEN_TWO_SHIFTS.getName(),
                HardSoftScore.ONE_HARD,
                (first, second, settings) -> {
                    final long breakLength = Math.abs(Duration.between(
                        first.getShift().getEndAt(),
                        second.getShift().getStartAt()
                    ).toMinutes());

                    return Math.abs(
                        settings.getHoursBetweenShifts() - convertMinutesToHours(breakLength)
                    );
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
                (shiftAssignment1, shiftAssignment2) -> 24
            );
    }

    Constraint evenlyShiftsDistribution(ConstraintFactory constraintFactory) {
        // try to distribute the shifts evenly to employees
        // activate only if there are more employees than positions

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
                (shiftAssignment) -> {
                    final String requiredRole = shiftAssignment.getRole();
                    final Set<String> providedRoles = shiftAssignment.getEmployee().getRoleSet();

                    return !providedRoles.contains(requiredRole);
                }
            )
            .penalize(
                Conflict.ONLY_REQUIRED_ROLES.getName(),
                HardSoftScore.ONE_HARD,
                (shiftEmployee) -> 100
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
