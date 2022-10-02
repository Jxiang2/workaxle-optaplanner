package org.workaxle.solver.base;

import org.workaxle.domain.Employee;
import org.workaxle.domain.ShiftAssignment;
import org.workaxle.util.common.TimeUtil;

public class BaseConstraintPenalty {

  private static final int DAY_IN_HOURS = 24;

  private static final int MAX_PENALTY_SCORE = 100;

  static int atMostOneShiftPerDayPenalty(
      ShiftAssignment first,
      ShiftAssignment second
  ) {
    return DAY_IN_HOURS;
  }

  static int evenlyShiftsDistributionPenalty(Employee employee, int count) {
    return count * count;
  }

  static int onlyRequiredRolePenalty(ShiftAssignment sa) {
    return MAX_PENALTY_SCORE;
  }

  static int noOverlappingShiftsPenalty(
      ShiftAssignment first,
      ShiftAssignment second
  ) {
    return TimeUtil.getHourlyOverlap(first, second);
  }


}
