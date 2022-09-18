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
    Long id;

    Shift shift;

    LocalDate date;

    @PlanningVariable(valueRangeProviderRefs = "employeeRange")
    EmployeeGroup employeeGroup;

    ShiftAssignment(Long id, Shift shift, LocalDate date) {
        this.id = id;
        this.shift = shift;
        this.date = date;
    }

    @Override
    public String toString() {
        return "from " + shift.startAt + " to " + shift.endAt + " ; " + employeeGroup.name + " at " + date;
    }

}
