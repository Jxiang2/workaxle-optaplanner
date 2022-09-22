package org.workaxle.domain;

import lombok.Data;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

import java.time.LocalDate;

@Data
@PlanningEntity
public class ShiftAssignment {

    @PlanningId
    private String id;

    @PlanningVariable(valueRangeProviderRefs = "employeeRange")
    private Employee employee;

    private String role;

    private Shift shift;

    private boolean valid = true;

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

    public LocalDate getDate() {
        return shift.getEndAt().toLocalDate();
    }

    @Override
    public String toString() {
        return employee != null
            ?
            "ShiftAssignment{" +
                "validity=" + isValid() + ", " +
                "shiftName='" + shift.getName() + '\'' +
                ", employeeName=" + employee.getName() +
                ", employeeRoles=" + employee.getRoleSet() +
                ", roleRequired='" + role + '\'' +
                ", shift=" + shift.getStartAt() + " ; " + shift.getEndAt() +
                '}'
            :
            "ShiftAssignment{" +
                "id=" + getId() + ", " +
                "shiftName='" + shift.getName() + '\'' +
                ", null" +
                ", roleRequired='" + role + '\'' +
                ", shift=" + shift.getStartAt() + " ; " + shift.getEndAt() +
                '}';
    }

}
