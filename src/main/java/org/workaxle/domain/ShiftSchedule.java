package org.workaxle.domain;

import lombok.Data;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

import java.util.List;

@Data
@PlanningSolution
public class ShiftSchedule {

    @PlanningScore
    private HardSoftScore score;

    @ValueRangeProvider(id = "employeeGroupRange")
    @ProblemFactCollectionProperty
    private List<EmployeeGroup> employeeGroupList;

    @PlanningEntityCollectionProperty
    private List<ShiftAssignment> shiftAssignmentList;

    public ShiftSchedule(List<EmployeeGroup> employeeGroupList, List<ShiftAssignment> shiftAssignmentList) {
        this.employeeGroupList = employeeGroupList;
        this.shiftAssignmentList = shiftAssignmentList;
    }

    public ShiftSchedule() {
    }

}