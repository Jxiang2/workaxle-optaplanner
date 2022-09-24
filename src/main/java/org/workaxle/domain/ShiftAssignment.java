package org.workaxle.domain;

import lombok.Data;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.workaxle.constants.Conflict;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
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

    private Map<String, Set<String>> conflicts = new HashMap<>() {{
        put(Conflict.AT_LEAST_N_HOURS_BETWEEN_TWO_SHIFTS.getName(), new HashSet<>());
        put(Conflict.AT_MOST_ONE_SHIFT_PER_DAY.getName(), new HashSet<>());
    }};

    public ShiftAssignment(String id, String role, Shift shift) {
        this.id = id;
        this.role = role;
        this.shift = shift;
    }

    public ShiftAssignment(String id, String role, Shift shift, Employee employee) {
        this.id = id;
        this.role = role;
        this.shift = shift;
        this.employee = employee;
    }

    public ShiftAssignment() {
    }

    @Override
    public String toString() {
        if (employee == null) {
            return "ShiftAssignment{" +
                "id=" + getId() + ", " +
                "shiftID=" + shift.getId() + '\'' +
                ", null" +
                ", roleRequired=" + role + '\'' +
                ", time=" + getDate() + "," + shift.getStartAt().toLocalTime() + "~" + shift.getEndAt().toLocalTime() +
                '}';
        }

        boolean empty = true;
        for (String conflictName : conflicts.keySet()) {
            if (conflicts.get(conflictName).size() != 0) {
                empty = false;
                break;
            }
        }

        return empty
            ?
            "ShiftAssignment{" +
                "id=" + getId() + '\'' +
                ", shiftId=" + shift.getId() + '\'' +
                ", employeeId=" + employee.getId() +
                ", employeeRoles=" + employee.getRoleSet() +
                ", roleRequired=" + role + '\'' +
                ", time=" + getDate() + "," + shift.getStartAt().toLocalTime() + "~" + shift.getEndAt().toLocalTime() +
                '}'
            :
            "ShiftAssignment{" +
                "id=" + getId() + '\'' +
                ", shiftId=" + shift.getId() + '\'' +
                ", employeeId=" + employee.getId() +
                ", employeeRoles=" + employee.getRoleSet() +
                ", roleRequired=" + role + '\'' +
                ", time=" + getDate() + "," + shift.getStartAt().toLocalTime() + "~" + shift.getEndAt().toLocalTime() +
                ", conflicts=" + getConflicts().toString() + '\'' +
                '}';
    }

    public LocalDate getDate() {
        return shift.getEndAt().toLocalDate();
    }

}
