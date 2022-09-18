package org.workaxle.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

import java.util.List;

@Data
@NoArgsConstructor
@PlanningSolution
public class Schedule {

    @PlanningScore
    private HardSoftScore score;

    @ValueRangeProvider(id = "employeeRange")
    @ProblemFactCollectionProperty
    private List<EmployeeGroup> employeeGroupList;

    @PlanningEntityCollectionProperty
    private List<ShiftAssignment> shiftAssignmentList;

    public Schedule(List<EmployeeGroup> employeeGroupList, List<ShiftAssignment> shiftAssignmentList) {
        this.employeeGroupList = employeeGroupList;
        this.shiftAssignmentList = shiftAssignmentList;
    }

}
