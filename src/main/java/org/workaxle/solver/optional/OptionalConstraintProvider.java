package org.workaxle.solver.optional;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.Joiners;
import org.workaxle.constants.Conflict;
import org.workaxle.domain.Settings;
import org.workaxle.domain.ShiftAssignment;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.workaxle.solver.ConstraintUtil.convertMinutesToHours;

public class OptionalConstraintProvider implements ConstraintProvider {

    public Constraint[] exportConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[]{
            atLeastNHoursBetweenTwoShifts(constraintFactory),
        };
    }

    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[]{
            atLeastNHoursBetweenTwoShifts(constraintFactory)
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

}
