package org.workaxle.domain;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

import java.util.List;
import java.util.Objects;

@PlanningSolution
public class Schedule {

    @PlanningScore
    private HardSoftScore score;

    @ValueRangeProvider(id = "employeeRange")
    @ProblemFactCollectionProperty
    private List<Employee> employeeList;

    @PlanningEntityCollectionProperty
    private List<ShiftAssignment> shiftAssignmentList;

    public Schedule(List<Employee> employeeList, List<ShiftAssignment> shiftAssignmentList) {
        this.employeeList = employeeList;
        this.shiftAssignmentList = shiftAssignmentList;
    }

    public Schedule() {
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $score = this.getScore();
        result = result * PRIME + ($score == null ? 43 : $score.hashCode());
        final Object $employeeGroupList = this.getEmployeeGroupList();
        result = result * PRIME + ($employeeGroupList == null ? 43 : $employeeGroupList.hashCode());
        final Object $shiftAssignmentList = this.getShiftAssignmentList();
        result = result * PRIME + ($shiftAssignmentList == null ? 43 : $shiftAssignmentList.hashCode());
        return result;
    }

    public boolean equals(final Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Schedule))
            return false;
        final Schedule other = (Schedule) o;
        if (!other.canEqual((Object) this))
            return false;
        final Object this$score = this.getScore();
        final Object other$score = other.getScore();
        if (!Objects.equals(this$score, other$score))
            return false;
        final Object this$employeeGroupList = this.getEmployeeGroupList();
        final Object other$employeeGroupList = other.getEmployeeGroupList();
        if (!Objects.equals(this$employeeGroupList, other$employeeGroupList))
            return false;
        final Object this$shiftAssignmentList = this.getShiftAssignmentList();
        final Object other$shiftAssignmentList = other.getShiftAssignmentList();
        return Objects.equals(this$shiftAssignmentList, other$shiftAssignmentList);
    }

    protected boolean canEqual(final Object other) {
        return other instanceof Schedule;
    }

    public String toString() {
        return "Schedule(score=" + this.getScore() + ", employeeGroupList=" + this.getEmployeeGroupList() + ", shiftAssignmentList=" + this.getShiftAssignmentList() + ")";
    }

    public HardSoftScore getScore() {
        return this.score;
    }

    public List<Employee> getEmployeeGroupList() {
        return this.employeeList;
    }

    public List<ShiftAssignment> getShiftAssignmentList() {
        return this.shiftAssignmentList;
    }

    public void setShiftAssignmentList(List<ShiftAssignment> shiftAssignmentList) {
        this.shiftAssignmentList = shiftAssignmentList;
    }

    public void setEmployeeGroupList(List<Employee> employeeList) {
        this.employeeList = employeeList;
    }

    public void setScore(HardSoftScore score) {
        this.score = score;
    }

}
