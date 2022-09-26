package org.workaxle.util.solution;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.workaxle.constants.Conflict;
import org.workaxle.domain.ShiftAssignment;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SolutionHandler {

    List<ShiftAssignment> shiftAssignmentList;

    HardSoftScore score;

    public SolutionHandler(HardSoftScore score, List<ShiftAssignment> shiftAssignmentList) {
        this.score = score;
        this.shiftAssignmentList = shiftAssignmentList;
    }

    // Todo: refactor using set to reduce runtime complexity to O(n)
    public SolutionHandler markInvalidDueToByDailyBetween(
        LocalDate shiftsStartDay,
        LocalDate shiftsEndDay
    ) {
        if (score.getHardScore() < 0) {
            while (shiftsStartDay.isBefore(shiftsEndDay.plusDays(1))) {
                final LocalDate shiftsStartDayCopy = shiftsStartDay;
                final List<ShiftAssignment> currentDateShiftAssignments = shiftAssignmentList
                    .stream()
                    .filter(shiftAssignment -> shiftAssignment.getDate().equals(shiftsStartDayCopy))
                    .collect(Collectors.toList());

                for (ShiftAssignment first : currentDateShiftAssignments) {
                    for (ShiftAssignment second : currentDateShiftAssignments) {
                        if (!first.equals(second) &&
                            first.getEmployee().equals(second.getEmployee())
                        ) {
                            addConflictToMutualConflictSets(
                                first,
                                second,
                                Conflict.AT_MOST_ONE_SHIFT_PER_DAY.getName()
                            );
                        }
                    }
                }

                shiftsStartDay = shiftsStartDay.plusDays(1);
            }
        }
        return this;
    }

    private void addConflictToMutualConflictSets(
        ShiftAssignment first,
        ShiftAssignment second,
        String conflictCName
    ) {
        final Set<String> firstConflictSet = first.getConflicts().get(conflictCName);
        final Set<String> secondConflictSet = second.getConflicts().get(conflictCName);

        firstConflictSet.add(second.getId());
        secondConflictSet.add(first.getId());
    }

    public SolutionHandler markInvalidDueToRequiredRole() {
        if (score.getHardScore() < 0) {
            for (ShiftAssignment shiftAssignment : shiftAssignmentList) {
                Set<String> employeeRoles = shiftAssignment.getEmployee().getRoleSet();
                if (!employeeRoles.contains(shiftAssignment.getRole())) {
                    final Set<String> invalidRoles = shiftAssignment
                        .getConflicts()
                        .get(Conflict.ONLY_REQUIRED_ROLES.getName());
                    invalidRoles.addAll(employeeRoles);
                }
            }
        }
        return this;
    }

    // Todo: refactor using set to reduce runtime complexity to O(n)
    public SolutionHandler markInvalidDueToHourlyBetween(int duration) {
        if (score.getHardScore() < 0) {
            for (ShiftAssignment first : shiftAssignmentList) {
                final LocalDateTime firstEndAt = first.getShift().getEndAt();
                for (ShiftAssignment second : shiftAssignmentList) {
                    final LocalDateTime secondStartAt = second.getShift().getStartAt();
                    if (!first.equals(second) &&
                        first.getEmployee().equals(second.getEmployee()) &&
                        secondStartAt.isAfter(firstEndAt) &&
                        Math.abs(Duration.between(firstEndAt, secondStartAt).toHours()) < duration
                    ) {
                        addConflictToMutualConflictSets(
                            first,
                            second,
                            Conflict.AT_LEAST_N_HOURS_BETWEEN_TWO_SHIFTS.getName()
                        );
                    }
                }
            }
        }
        return this;
    }

}
