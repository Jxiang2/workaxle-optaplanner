package org.workaxle.domain;

import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.solution.ProblemFactProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

@Getter
@Setter
@EqualsAndHashCode
@PlanningSolution
public class Schedule {

  @PlanningScore
  private HardSoftScore score;

  @ValueRangeProvider(id = "employeeRange")
  @ProblemFactCollectionProperty
  private List<Employee> employeeList;

  @ProblemFactProperty
  private Settings settings;

  @PlanningEntityCollectionProperty
  private List<ShiftAssignment> shiftAssignmentList;

  public Schedule() {
  }

  public Schedule(Settings settings,
      List<Employee> employeeList,
      List<ShiftAssignment> shiftAssignmentList
  ) {
    this.settings = settings;
    this.employeeList = employeeList;
    this.shiftAssignmentList = shiftAssignmentList;
  }
}
