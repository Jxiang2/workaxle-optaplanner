package org.workaxle.domain;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

@PlanningEntity
public class ShiftAssignment {

    @PlanningId
    Long id;

    Shift shift;

    LocalDate date;

    @PlanningVariable(valueRangeProviderRefs = "employeeRange")
    Employee employee;

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

    public Shift getShift() {
        return this.shift;
    }

    public LocalDate getDate() {
        return this.date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setShift(Shift shift) {
        this.shift = shift;
    }

    public LocalDateTime getEndDatetime() {
        LocalTime endTime = getShift().getEndAt();
        LocalDate date = getDate();
        return LocalDateTime.of(date, endTime);
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final Object $shift = this.getShift();
        result = result * PRIME + ($shift == null ? 43 : $shift.hashCode());
        final Object $date = this.getDate();
        result = result * PRIME + ($date == null ? 43 : $date.hashCode());
        final Object $employeeGroup = this.getEmployeeGroup();
        result = result * PRIME + ($employeeGroup == null ? 43 : $employeeGroup.hashCode());
        return result;
    }

    public boolean equals(final Object o) {
        if (o == this)
            return true;
        if (!(o instanceof ShiftAssignment))
            return false;
        final ShiftAssignment other = (ShiftAssignment) o;
        if (!other.canEqual((Object) this))
            return false;
        final Object this$id = this.getId();
        final Object other$id = other.getId();
        if (!Objects.equals(this$id, other$id))
            return false;
        final Object this$shift = this.getShift();
        final Object other$shift = other.getShift();
        if (!Objects.equals(this$shift, other$shift))
            return false;
        final Object this$date = this.getDate();
        final Object other$date = other.getDate();
        if (!Objects.equals(this$date, other$date))
            return false;
        final Object this$employeeGroup = this.getEmployeeGroup();
        final Object other$employeeGroup = other.getEmployeeGroup();
        return Objects.equals(this$employeeGroup, other$employeeGroup);
    }

    @Override
    public String toString() {
        if (employee != null) {
            return employee.name + " ; " + shift.startAt + "~" + shift.endAt + " ; " + date;
        }
        return "No employee assigned" + " ; " + shift.startAt + "~" + shift.endAt + " ; " + date;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof ShiftAssignment;
    }

    public Long getId() {
        return this.id;
    }

    public Employee getEmployeeGroup() {
        return this.employee;
    }

    public void setEmployeeGroup(Employee employee) {
        this.employee = employee;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
