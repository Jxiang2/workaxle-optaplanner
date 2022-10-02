package org.workaxle.util.solution;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import org.json.simple.parser.ParseException;
import org.workaxle.bootstrap.Data;
import org.workaxle.domain.Schedule;
import org.workaxle.domain.ShiftAssignment;

public class SolutionPrinter {

  public static void printResult(Schedule schedule)
      throws IOException, ParseException {
    final List<ShiftAssignment> shiftAssignmentList =
        schedule.getShiftAssignmentList();

    new SolutionHandler(schedule)
        .markInvalidDueToOneShiftPerDay()
        .markInvalidDueToHoursBetweenShifts()
        .markInvalidDueToRequiredRole()
        .markInvalidDueToWeekendShifts()
        .markInvalidDueToExceedingMaxHours();

    System.out.println(
        "Total number of valid shift assignments: " +
            shiftAssignmentList.size() + "\n"
    );

    final LocalDate startDay = Data.generateStartEndDates()[0];
    final LocalDate endDay = Data.generateStartEndDates()[1];
    LocalDate currentDay = startDay;

    while (currentDay.isBefore(endDay.plusDays(1))) {
      for (ShiftAssignment shiftAssignment : shiftAssignmentList) {
        if (shiftAssignment.getDate().equals(currentDay)) {
          System.out.println(shiftAssignment);
        }
      }
      currentDay = currentDay.plusDays(1);
      System.out.println();
    }
  }

}
