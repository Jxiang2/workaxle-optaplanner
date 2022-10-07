package org.workaxle.solver.optional.workaxle;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import org.junit.jupiter.api.Test;
import org.optaplanner.test.api.score.stream.ConstraintVerifier;
import org.workaxle.domain.Employee;
import org.workaxle.domain.Schedule;
import org.workaxle.domain.Settings;
import org.workaxle.domain.Shift;
import org.workaxle.domain.ShiftAssignment;

public class OptionalConstraintProviderTest {

  ConstraintVerifier<OptionalConstraintProvider, Schedule> constraintVerifier =
      ConstraintVerifier
          .build(new OptionalConstraintProvider(), Schedule.class,
              ShiftAssignment.class);

  @Test
  void testAtMostNHoursShiftAssignments() {
    Shift s1 = new Shift(
        1L,
        "shift A1",
        LocalDateTime.of(2022, 11, 21, 9, 0),
        LocalDateTime.of(2022, 11, 21, 15, 0),
        new HashMap<>() {{
          put("Dev", 1);
          put("Design", 1);
        }}
    );
    Shift s2 = new Shift(
        2L,
        "shift A2",
        LocalDateTime.of(2022, 11, 22, 15, 0),
        LocalDateTime.of(2022, 11, 22, 22, 0),
        new HashMap<>() {{
          put("Dev", 1);
          put("Design", 1);
        }}
    );
    Employee e1 =
        new Employee(1L, "user 1", new HashSet<>(Arrays.asList("Dev", "Design")));
    int j = 1;
    ShiftAssignment sa1 = new ShiftAssignment(String.valueOf(j++), "Dev", s1, e1);
    ShiftAssignment sa2 = new ShiftAssignment(String.valueOf(j++), "Design", s2, e1);
    Settings settings = new Settings();
    settings.setMaxHoursOfWork(8);
    constraintVerifier
        .verifyThat(OptionalConstraintProvider::atMostNHoursWork)
        .given(
            sa1,
            sa2,
            settings
        )
        .penalizesBy(5);
  }

  @Test
  void testNoShiftOnWeekends() {
    Shift s1 = new Shift(
        1L,
        "shift A1",
        LocalDateTime.of(2022, 11, 26, 20, 0),
        LocalDateTime.of(2022, 11, 26, 23, 0),
        new HashMap<>() {{
          put("Dev", 1);
          put("Design", 1);
        }}
    );
    Shift s2 = new Shift(
        2L,
        "shift A2",
        LocalDateTime.of(2022, 11, 27, 9, 0),
        LocalDateTime.of(2022, 11, 27, 12, 0),
        new HashMap<>() {{
          put("Dev", 1);
          put("Design", 1);
        }}
    );
    Employee e1 =
        new Employee(1L, "user 1", new HashSet<>(Arrays.asList("Dev", "Design")));
    int j = 1;
    ShiftAssignment sa1 = new ShiftAssignment(String.valueOf(j++), "Dev", s1, e1);
    ShiftAssignment sa2 = new ShiftAssignment(String.valueOf(j++), "Design", s1, e1);
    Settings settings = new Settings();
    settings.setAllowWeekendShifts(false);
    constraintVerifier
        .verifyThat(OptionalConstraintProvider::noShiftOnWeekends)
        .given(
            sa1,
            sa2,
            settings
        )
        .penalizesBy(6);
  }

  @Test
  void testAtLeastNHoursBetweenTwoShifts() {
    Shift s1 = new Shift(
        1L,
        "shift A3",
        LocalDateTime.of(2022, 11, 21, 20, 0),
        LocalDateTime.of(2022, 11, 21, 23, 0),
        new HashMap<>() {{
          put("Dev", 1);
          put("Design", 1);
        }}
    );
    Shift s2 = new Shift(
        2L,
        "shift B1",
        LocalDateTime.of(2022, 11, 22, 9, 0),
        LocalDateTime.of(2022, 11, 22, 12, 0),
        new HashMap<>() {{
          put("Dev", 1);
          put("Design", 1);
        }}
    );
    Employee e1 =
        new Employee(1L, "user 1", new HashSet<>(Arrays.asList("Dev", "Design")));
    Employee e2 =
        new Employee(2L, "user 2", new HashSet<>(Arrays.asList("Dev", "Design")));
    Employee e3 =
        new Employee(3L, "user 3", new HashSet<>(Arrays.asList("Dev", "Design")));
    int j = 1;
    ShiftAssignment sa1 = new ShiftAssignment(String.valueOf(j++), "Dev", s1, e1);
    ShiftAssignment sa2 = new ShiftAssignment(String.valueOf(j++), "Design", s1, e2);
    ShiftAssignment sa3 = new ShiftAssignment(String.valueOf(j++), "Dev", s2, e3);
    ShiftAssignment sa4 = new ShiftAssignment(String.valueOf(j++), "Design", s2, e2);
    constraintVerifier
        .verifyThat(OptionalConstraintProvider::atLeastNHoursBetweenTwoShifts)
        .given(
            sa1,
            sa2,
            sa3,
            sa4
        )
        .penalizesBy(0);
  }

}
