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
public class Schedule {

    @PlanningScore
    private HardSoftScore score;

    @ValueRangeProvider(id = "employeeRange")
    @ProblemFactCollectionProperty
    private List<Employee> employeeList;

    @PlanningEntityCollectionProperty
    private List<ShiftEmployee> shiftEmployeeList;

    public Schedule(List<Employee> employeeList, List<ShiftEmployee> shiftEmployeeList) {
        this.employeeList = employeeList;
        this.shiftEmployeeList = shiftEmployeeList;
    }

    public Schedule() {
    }

}
