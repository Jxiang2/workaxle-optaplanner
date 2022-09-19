package org.workaxle.domain;

import lombok.Data;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;


@Data
@PlanningEntity
public class ShiftAssignment {

    @PlanningVariable(valueRangeProviderRefs = "employeeRange")
    private Employee employee;

    @PlanningId
    private Long id;

    private Shift shift;

    private LocalDate date;

    public ShiftAssignment(Long id, Shift shift, LocalDate date) {
        this.id = id;
        this.shift = shift;
        this.date = date;
    }

    public ShiftAssignment(Long id, Shift shift, LocalDate date, Employee employee) {
        this.id = id;
        this.shift = shift;
        this.date = date;
        this.employee = employee;
    }

    public ShiftAssignment() {
    }

    public LocalDateTime getStartDatetime() {
        LocalTime endTime = getShift().getStartAt();
        LocalDate date = getDate();
        return LocalDateTime.of(date, endTime);
    }

    public LocalDateTime getEndDateTime() {
        LocalTime endTime = getShift().getEndAt();
        LocalDate date = getDate();
        return LocalDateTime.of(date, endTime);
    }


    @Override
    public String toString() {
        if (employee != null) {
            return employee.getName() + " ; " + shift.getStartAt() + "~" + shift.getEndAt() + " ; " + date;
        }
        return "No employee assigned" + " ; " + shift.getStartAt() + "~" + shift.getEndAt() + " ; " + date;
    }

}
