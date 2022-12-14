package org.workaxle.solver.base.workaxle;

import java.util.Set;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintCollectors;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.Joiners;
import org.workaxle.constants.Conflict;
import org.workaxle.domain.ShiftAssignment;

public class BaseConstraintProvider implements ConstraintProvider {

  @Override
  public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
    // for test only

    return new Constraint[]{
        atMostOneShiftPerDay(constraintFactory),
        evenlyShiftsDistribution(constraintFactory),
        onlyRequiredRole(constraintFactory),
        noOverlappingShifts(constraintFactory),
        // employeeAvailable(constraintFactory),
    };
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
            BaseConstraintPenalty::atMostOneShiftPerDayPenalty
        );
  }

  Constraint evenlyShiftsDistribution(ConstraintFactory constraintFactory) {
    // try to distribute the shifts evenly to employees
    // activate only if there are more employees than positions

    return constraintFactory
        .forEach(ShiftAssignment.class)
        .groupBy(ShiftAssignment::getEmployee, ConstraintCollectors.count())
        .penalize(
            Conflict.EVENLY_SHIFT_DISTRIBUTION.getName(),
            HardSoftScore.ONE_SOFT,
            BaseConstraintPenalty::evenlyShiftsDistributionPenalty
        );
  }

  Constraint onlyRequiredRole(ConstraintFactory constraintFactory) {
    // a shift can only be assigned to employees with roles it needs

    return constraintFactory
        .forEach(ShiftAssignment.class)
        .filter(
            (shiftAssignment) -> {
              final String requiredRole = shiftAssignment.getRole();
              final Set<String> providedRoles =
                  shiftAssignment.getEmployee().getRoleSet();

              return !providedRoles.contains(requiredRole);
            }
        )
        .penalize(
            Conflict.ONLY_REQUIRED_ROLES.getName(),
            HardSoftScore.ONE_HARD,
            BaseConstraintPenalty::onlyRequiredRolePenalty
        );
  }

  Constraint noOverlappingShifts(ConstraintFactory constraintFactory) {
    // no employee takes 2 or more shifts at the same time

    return constraintFactory
        .forEachUniquePair(ShiftAssignment.class,
            Joiners.equal(ShiftAssignment::getEmployee),
            Joiners.overlapping(
                (shiftAssignment) -> shiftAssignment.getShift()
                    .getStartAt(),
                (shiftAssignment) -> shiftAssignment.getShift().getEndAt()
            )
        )
        .penalize(
            Conflict.NO_OVERLAPPING_SHIFTS.getName(),
            HardSoftScore.ONE_HARD,
            BaseConstraintPenalty::noOverlappingShiftsPenalty
        );
  }

}
