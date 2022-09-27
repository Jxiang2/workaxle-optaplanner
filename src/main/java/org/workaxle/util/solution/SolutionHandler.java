package org.workaxle.util.solution;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.workaxle.constants.Conflict;
import org.workaxle.domain.ShiftAssignment;
import org.workaxle.solver.ConstraintUtil;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SolutionHandler {

    final List<ShiftAssignment> shiftAssignmentList;

    final HardSoftScore score;

    public SolutionHandler(HardSoftScore score, List<ShiftAssignment> shiftAssignmentList) {
        this.score = score;
        this.shiftAssignmentList = shiftAssignmentList;
    }

    public SolutionHandler markInvalidDueToWeekendShifts() {
        for (ShiftAssignment shiftAssignment : shiftAssignmentList) {
            if (ConstraintUtil.isWeekend(shiftAssignment.getDate())) {
                final Set<String> conflictSet = shiftAssignment
                    .getConflicts().get(Conflict.NO_SHIFT_ON_WEEKENDS.getName());
                conflictSet.add(shiftAssignment.getId());
            }
        }
        return this;
    }

    public SolutionHandler markInvalidDueToByDailyBetween() {
        for (ShiftAssignment shiftAssignment : shiftAssignmentList) {
            final Set<String> conflictSet = shiftAssignment
                .getConflicts().get(Conflict.AT_MOST_ONE_SHIFT_PER_DAY.getName());

            conflictSet.addAll(
                shiftAssignmentList
                    .stream()
                    .filter(other -> !shiftAssignment.equals(other)
                        && shiftAssignment.getEmployee().equals(other.getEmployee())
                        && shiftAssignment.getDate().equals(other.getDate())
                    )
                    .map(other -> other.getId())
                    .collect(Collectors.toSet())
            );
        }
        return this;
    }

    public SolutionHandler markInvalidDueToRequiredRole() {
        for (ShiftAssignment shiftAssignment : shiftAssignmentList) {
            Set<String> employeeRoles = shiftAssignment.getEmployee().getRoleSet();

            if (!employeeRoles.contains(shiftAssignment.getRole())) {
                final Set<String> invalidRoles = shiftAssignment
                    .getConflicts()
                    .get(Conflict.ONLY_REQUIRED_ROLES.getName());
                invalidRoles.addAll(employeeRoles);
            }
        }
        return this;
    }

    public SolutionHandler markInvalidDueToHourlyBetween(int duration) {
        if (duration != 0) {
            for (ShiftAssignment shiftAssignment : shiftAssignmentList) {
                final Set<String> conflictSet = shiftAssignment
                    .getConflicts()
                    .get(Conflict.AT_LEAST_N_HOURS_BETWEEN_TWO_SHIFTS.getName());
                final LocalDateTime startAt = shiftAssignment.getShift().getStartAt();
                final LocalDateTime endAt = shiftAssignment.getShift().getEndAt();

                conflictSet.addAll(
                    shiftAssignmentList
                        .stream()
                        .filter(other -> {
                            final LocalDateTime otherStartAt = other.getShift().getStartAt();
                            final LocalDateTime otherEndAt = other.getShift().getEndAt();

                            return !other.equals(shiftAssignment)
                                && shiftAssignment.getEmployee().equals(other.getEmployee())
                                && (Math.abs(Duration.between(endAt, otherStartAt).toHours()) < duration
                                || Math.abs(Duration.between(otherEndAt, startAt).toHours()) < duration);
                        })
                        .map(other -> other.getId())
                        .collect(Collectors.toSet())
                );
            }
        }
        return this;
    }

}
