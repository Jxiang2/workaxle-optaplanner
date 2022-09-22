package org.workaxle.domain;

import lombok.Data;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Data
@PlanningEntity
public class ShiftAssignment {

    @PlanningId
    private String id;

    @PlanningVariable(valueRangeProviderRefs = "employeeRange")
    private Employee employee;

    private String role;

    private Shift shift;

    private Map<String, Boolean> conflicts = new HashMap<>() {{
        put("hourlyGap", false);
        put("dailyGap", false);
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
        return employee != null
            ?
            "ShiftAssignment{" +
                conflicts + ", " +
                "shiftName='" + shift.getName() + '\'' +
                ", employeeId=" + employee.getId() +
                ", employeeRoles=" + employee.getRoleSet() +
                ", roleRequired='" + role + '\'' +
                ", time=" + shift.getStartAt().toLocalTime() + "~" + shift.getEndAt().toLocalTime() + "," + getDate() +
                '}'
            :
            "ShiftAssignment{" +
                "id=" + getId() + ", " +
                "shiftName='" + shift.getName() + '\'' +
                ", null" +
                ", roleRequired='" + role + '\'' +
                ", time=" + shift.getStartAt().toLocalTime() + "~" + shift.getEndAt().toLocalTime() + "," + getDate() +
                '}';
    }

    public LocalDate getDate() {
        return shift.getEndAt().toLocalDate();
    }

}
