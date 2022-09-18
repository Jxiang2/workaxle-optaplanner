package org.workaxle.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@PlanningEntity
public class ShiftAssignment {

    @PlanningId
    Long id;

    Shift shift;

    LocalDate date;

    @PlanningVariable(valueRangeProviderRefs = "employeeRange")
    EmployeeGroup employeeGroup;

    public ShiftAssignment(Long id, Shift shift, LocalDate date) {
        this.id = id;
        this.shift = shift;
        this.date = date;
    }

    public LocalDateTime getStartDatetime() {
        LocalTime endTime = getShift().getStartAt();
        LocalDate date = getDate();
        return LocalDateTime.of(date, endTime);
    }

    public LocalDateTime getEndDatetime() {
        LocalTime endTime = getShift().getEndAt();
        LocalDate date = getDate();
        return LocalDateTime.of(date, endTime);
    }

    @Override
    public String toString() {
        return employeeGroup.name + " ; " + shift.startAt + "~" + shift.endAt + " ; " + date;
    }

}
