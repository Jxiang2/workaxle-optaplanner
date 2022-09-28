package org.workaxle.util.solution;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.workaxle.constants.Conflict;
import org.workaxle.domain.Schedule;
import org.workaxle.domain.Settings;
import org.workaxle.domain.ShiftAssignment;
import org.workaxle.util.common.Chronometric;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SolutionHandler {

    HardSoftScore score;

    Settings settings;

    List<ShiftAssignment> shiftAssignmentList;

    public SolutionHandler(Schedule schedule) {
        shiftAssignmentList = schedule.getShiftAssignmentList();
        score = schedule.getScore();
        settings = schedule.getSettings();
    }

    public SolutionHandler markInvalidDueToWeekendShifts() {
        if (score.getHardScore() < 0 && !settings.getWeekendShifts()) {
            for (ShiftAssignment shiftAssignment : shiftAssignmentList) {
                if (Chronometric.isWeekend(shiftAssignment.getDate())) {
                    final Map<String, Boolean> boolConflicts = shiftAssignment.getBoolConflicts();
                    boolConflicts.put(Conflict.NO_SHIFT_ON_WEEKENDS.getName(), true);
                }
            }
        }
        return this;
    }

    public SolutionHandler markInvalidDueToByDailyBetween() {
        if (score.getHardScore() < 0) {
            for (ShiftAssignment shiftAssignment : shiftAssignmentList) {
                final Set<String> newConflicts = shiftAssignmentList
                    .stream()
                    .filter(other -> !shiftAssignment.equals(other)
                        && shiftAssignment.getEmployee().equals(other.getEmployee())
                        && shiftAssignment.getDate().equals(other.getDate())
                    )
                    .map(other -> other.getId())
                    .collect(Collectors.toSet());

                final Map<String, Set<String>> setConflicts = shiftAssignment.getSetConflicts();
                final Set<String> previousConflicts =
                    setConflicts.get(Conflict.AT_MOST_ONE_SHIFT_PER_DAY.getName());

                if (previousConflicts == null) {
                    Set<String> conflictSet = new HashSet<>();
                    conflictSet.addAll(newConflicts);
                    setConflicts.put(Conflict.AT_MOST_ONE_SHIFT_PER_DAY.getName(), conflictSet);
                } else {
                    previousConflicts.addAll(newConflicts);
                }
            }
        }
        return this;
    }

    public SolutionHandler markInvalidDueToRequiredRole() {
        if (score.getHardScore() < 0) {
            for (ShiftAssignment shiftAssignment : shiftAssignmentList) {
                Set<String> employeeRoles = shiftAssignment.getEmployee().getRoleSet();
                if (!employeeRoles.contains(shiftAssignment.getRole())) {
                    final Map<String, Boolean> boolConflicts = shiftAssignment.getBoolConflicts();
                    boolConflicts.put(Conflict.ONLY_REQUIRED_ROLES.getName(), true);
                }
            }
        }
        return this;
    }

    public SolutionHandler markInvalidDueToHourlyBetween() {
        int shiftsBetween = settings.getShiftsBetween();
        if (score.getHardScore() < 0 && shiftsBetween != 0) {
            for (ShiftAssignment shiftAssignment : shiftAssignmentList) {
                final LocalDateTime startAt = shiftAssignment.getShift().getStartAt();
                final LocalDateTime endAt = shiftAssignment.getShift().getEndAt();

                Set<String> newConflicts = shiftAssignmentList
                    .stream()
                    .filter(other -> {
                        final LocalDateTime otherStartAt = other.getShift().getStartAt();
                        final LocalDateTime otherEndAt = other.getShift().getEndAt();

                        return !other.equals(shiftAssignment)
                            && shiftAssignment.getEmployee().equals(other.getEmployee())
                            && (Math.abs(Duration.between(endAt, otherStartAt).toHours()) < shiftsBetween
                            || Math.abs(Duration.between(otherEndAt, startAt).toHours()) < shiftsBetween);
                    })
                    .map(other -> other.getId())
                    .collect(Collectors.toSet());

                final Map<String, Set<String>> setConflicts = shiftAssignment.getSetConflicts();
                final Set<String> previousConflicts =
                    setConflicts.get(Conflict.AT_LEAST_N_HOURS_BETWEEN_TWO_SHIFTS.getName());

                if (previousConflicts == null) {
                    Set<String> conflictSet = new HashSet<>();
                    conflictSet.addAll(newConflicts);
                    setConflicts.put(Conflict.AT_LEAST_N_HOURS_BETWEEN_TWO_SHIFTS.getName(), conflictSet);
                } else {
                    previousConflicts.addAll(newConflicts);
                }
            }
        }
        return this;
    }

}
