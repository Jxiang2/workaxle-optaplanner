package org.workaxle.solver.optional;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.*;
import org.workaxle.constants.Conflict;
import org.workaxle.domain.Settings;
import org.workaxle.domain.ShiftAssignment;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.workaxle.solver.ConstraintUtil.convertMinutesToHours;
import static org.workaxle.solver.ConstraintUtil.isWeekend;

public class OptionalConstraintProvider implements ConstraintProvider {

    public Constraint[] exportConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[]{
            atLeastNHoursBetweenTwoShifts(constraintFactory),
            noShiftOnWeekends(constraintFactory),
            atMostNHours(constraintFactory),
        };
    }

    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        // for test only

        return new Constraint[]{
            atLeastNHoursBetweenTwoShifts(constraintFactory),
            noShiftOnWeekends(constraintFactory),
            atMostNHours(constraintFactory),
        };
    }

    Constraint atMostNHours(ConstraintFactory constraintFactory) {
        // no employee work more than X hours during a Y-day period

        return constraintFactory
            .forEach(ShiftAssignment.class)
            .groupBy(
                ShiftAssignment::getEmployee,
                ConstraintCollectors.sum(ShiftAssignment::getShiftDurationInHours)
            )
            .join(Settings.class)
            .filter((employee, totalHours, settings) -> {
                return totalHours > settings.getMaxHours();
            })
            .penalize(
                Conflict.AT_MOST_N_HOURS.getName(),
                HardSoftScore.ONE_HARD,
                (employee, totalHours, settings) -> totalHours - settings.getMaxHours()
            );
    }

    Constraint noShiftOnWeekends(ConstraintFactory constraintFactory) {
        // no shifts should be scheduled on weekends

        return constraintFactory
            .forEach(ShiftAssignment.class)
            .join(Settings.class)
            .filter((shiftAssignment, settings) ->
                !settings.isWeekendShifts() && isWeekend(shiftAssignment.getDate())
            )
            .penalize(
                Conflict.NO_SHIFT_ON_WEEKENDS.getName(),
                HardSoftScore.ONE_HARD,
                (shiftAssignment, settings) -> shiftAssignment.getShiftDurationInHours()
            );
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
                    final int gap = settings.getShiftsBetween();
                    final LocalDateTime firstStartAt = firstShift.getShift().getStartAt();
                    final LocalDateTime firstEndAt = firstShift.getShift().getEndAt();
                    final LocalDateTime secondStartAt = secondShift.getShift().getStartAt();
                    final LocalDateTime secondEndAt = secondShift.getShift().getEndAt();

                    return Math.abs(Duration.between(firstEndAt, secondStartAt).toHours()) < gap
                        || Math.abs(Duration.between(secondEndAt, firstStartAt).toHours()) < gap;
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

                    return Math.abs(settings.getShiftsBetween() - convertMinutesToHours(breakLength));
                }
            );
    }

}
