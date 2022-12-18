package org.workaxle.domain;

import lombok.Data;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Data
@PlanningEntity
public class ShiftAssignment {

  @PlanningId
  private String id;

  @PlanningVariable(valueRangeProviderRefs = "employeeRange")
  private Employee employee;

  private String role;

  private Shift shift;

  /**
   * BoolConflicts is a {@link Map} collection that contains {@link String} as key and
   * {@link Set<String>} as value
   * <p>
   * Each key represents a rule, and each value contains a hashset the id(s) of shiftAssignments
   * that causes this shiftAssignment to violate the rule.
   */
  private Map<String, Set<String>> setConflicts = new HashMap<>();

  /**
   * BoolConflicts is a {@link Map} collection that contains {@link String} as key and
   * {@link Boolean} as value
   * <p>
   * Each key represents a rule, and each value represents whether that rule is violated or not . If
   * a value is true, then it's corresponding rule is violated.
   */
  private Map<String, Boolean> boolConflicts = new HashMap<>();

  public ShiftAssignment(String id, String role, Shift shift) {
    this.id = id;
    this.role = role;
    this.shift = shift;
  }

  public ShiftAssignment(String id, String role, Shift shift,
                         Employee employee) {
    this.id = id;
    this.role = role;
    this.shift = shift;
    this.employee = employee;
  }

  public ShiftAssignment() {
  }

  public LocalDate getDate() {
    return shift.getEndAt().toLocalDate();
  }

  public int getShiftDurationInHours() {
    return (int) Duration.between(shift.getStartAt(), shift.getEndAt())
        .toHours();
  }

  @Override
  public String toString() {
    if (employee == null) {
      return "ShiftAssignment{" +
          "id=" + getId() + ", " +
          "shiftID=" + shift.getId() + '\'' +
          ", null" +
          ", roleRequired=" + role + '\'' +
          ", time=" + getDate() + "," + shift.getStartAt().toLocalTime() +
          "~" + shift.getEndAt().toLocalTime() +
          '}';
    }

    boolean noSetConflict = true;
    for (String conflictName : setConflicts.keySet()) {
      if (setConflicts.get(conflictName).size() != 0) {
        noSetConflict = false;
        break;
      }
    }

    boolean noBoolConflict = true;
    for (String conflictName : boolConflicts.keySet()) {
      if (boolConflicts.get(conflictName)) {
        noBoolConflict = false;
        break;
      }
    }

    if (noBoolConflict && noSetConflict) {
      return "ShiftAssignment{" +
          "id=" + getId() + '\'' +
          ", shiftId=" + shift.getId() + '\'' +
          ", employeeId=" + employee.getId() +
          ", employeeRoles=" + employee.getRoleSet() +
          ", roleRequired=" + role + '\'' +
          ", time=" + getDate() + "," + shift.getStartAt().toLocalTime() +
          "~" + shift.getEndAt().toLocalTime() +
          '}';
    } else if (noBoolConflict && !noSetConflict) {
      return "ShiftAssignment{" +
          "id=" + getId() + '\'' +
          ", shiftId=" + shift.getId() + '\'' +
          ", employeeId=" + employee.getId() +
          ", employeeRoles=" + employee.getRoleSet() +
          ", roleRequired=" + role + '\'' +
          ", time=" + getDate() + "," + shift.getStartAt().toLocalTime() +
          "~" + shift.getEndAt().toLocalTime() +
          ", setConflicts=" + getSetConflicts().toString() + '\'' +
          '}';
    } else if (!noBoolConflict && noSetConflict) {
      return "ShiftAssignment{" +
          "id=" + getId() + '\'' +
          ", shiftId=" + shift.getId() + '\'' +
          ", employeeId=" + employee.getId() +
          ", employeeRoles=" + employee.getRoleSet() +
          ", roleRequired=" + role + '\'' +
          ", time=" + getDate() + "," + shift.getStartAt().toLocalTime() +
          "~" + shift.getEndAt().toLocalTime() +
          ", boolConflicts=" + getBoolConflicts().toString() + '\'' +
          '}';
    } else {
      return "ShiftAssignment{" +
          "id=" + getId() + '\'' +
          ", shiftId=" + shift.getId() + '\'' +
          ", employeeId=" + employee.getId() +
          ", employeeRoles=" + employee.getRoleSet() +
          ", roleRequired=" + role + '\'' +
          ", time=" + getDate() + "," + shift.getStartAt().toLocalTime() +
          "~" + shift.getEndAt().toLocalTime() +
          ", setConflicts=" + getSetConflicts().toString() + '\'' +
          ", boolConflicts=" + getBoolConflicts().toString() + '\'' +
          '}';
    }
  }

}
