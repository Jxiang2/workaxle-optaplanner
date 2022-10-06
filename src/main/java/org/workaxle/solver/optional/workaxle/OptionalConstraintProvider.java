package org.workaxle.solver.optional.workaxle;

import static org.workaxle.util.common.TimeUtil.isWeekend;
import java.time.Duration;
import java.time.LocalDateTime;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintCollectors;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.Joiners;
import org.workaxle.constants.Conflict;
import org.workaxle.domain.Settings;
import org.workaxle.domain.ShiftAssignment;

public class OptionalConstraintProvider implements ConstraintProvider {

  public Constraint[] exportConstraints(ConstraintFactory constraintFactory) {
    return new Constraint[]{
        atLeastNHoursBetweenTwoShifts(constraintFactory),
        noShiftOnWeekends(constraintFactory),
        atMostNHoursWork(constraintFactory),
    };
  }

  @Override
  public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
    // for test only

    return new Constraint[]{
        atLeastNHoursBetweenTwoShifts(constraintFactory),
        noShiftOnWeekends(constraintFactory),
        atMostNHoursWork(constraintFactory),
    };
  }

  Constraint atMostNHoursWork(ConstraintFactory constraintFactory) {
    // no employee work more than X hours during a Y-day period

    return constraintFactory
        .forEach(ShiftAssignment.class)
        .groupBy(
            ShiftAssignment::getEmployee,
            ConstraintCollectors.sum(
                ShiftAssignment::getShiftDurationInHours)
        )
        .join(Settings.class)
        .filter((employee, totalHours, settings) ->
            settings.getMaxHoursOfWork() != null &&
                totalHours > settings.getMaxHoursOfWork()
        )
        .penalize(
            Conflict.AT_MOST_N_HOURS.getName(),
            HardSoftScore.ONE_HARD,
            (employee, totalHours, settings) -> totalHours -
                settings.getMaxHoursOfWork()
        );
  }

  Constraint noShiftOnWeekends(ConstraintFactory constraintFactory) {
    // no shifts should be scheduled on weekends

    return constraintFactory
        .forEach(ShiftAssignment.class)
        .join(Settings.class)
        .filter((shiftAssignment, settings) ->
            !settings.getAllowWeekendShifts() &&
                isWeekend(shiftAssignment.getDate())
        )
        .penalize(
            Conflict.NO_SHIFT_ON_WEEKENDS.getName(),
            HardSoftScore.ONE_HARD,
            (shiftAssignment, settings) -> shiftAssignment.getShiftDurationInHours()
        );
  }

  Constraint atLeastNHoursBetweenTwoShifts(
      ConstraintFactory constraintFactory) {
    // any employee can only work 1 shift in N hours

    return constraintFactory
        .forEachUniquePair(
            ShiftAssignment.class,
            Joiners.equal(ShiftAssignment::getEmployee)
        )
        .join(Settings.class)
        .filter((firstShift, secondShift, settings) -> {
              final Integer gap = settings.getHoursBetweenShifts();
              if (gap != null) {
                final LocalDateTime firstStartAt = firstShift.getShift().getStartAt();
                final LocalDateTime firstEndAt = firstShift.getShift().getEndAt();
                final LocalDateTime secondStartAt = secondShift.getShift().getStartAt();
                final LocalDateTime secondEndAt = secondShift.getShift().getEndAt();

                return Math.abs(Duration.between(firstEndAt, secondStartAt).toHours()) < gap
                    || Math.abs(Duration.between(secondEndAt, firstStartAt).toHours()) < gap;
              }
              return false;
            }
        )
        .penalize(
            Conflict.AT_LEAST_N_HOURS_BETWEEN_TWO_SHIFTS.getName(),
            HardSoftScore.ONE_HARD,
            OptionalConstraintPenalty::atLeastNHoursBetweenTwoShiftsPenalty
        );
  }

}
