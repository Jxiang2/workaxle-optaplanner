package org.workaxle.domain;

import lombok.Data;
import org.optaplanner.core.api.domain.solution.*;
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

  @ProblemFactProperty
  private Settings settings;

  @PlanningEntityCollectionProperty
  private List<ShiftAssignment> shiftAssignmentList;

  public Schedule(Settings settings,
                  List<Employee> employeeList,
                  List<ShiftAssignment> shiftAssignmentList
  ) {
    this.settings = settings;
    this.employeeList = employeeList;
    this.shiftAssignmentList = shiftAssignmentList;
  }

  public Schedule() {
  }

}
