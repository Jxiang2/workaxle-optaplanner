package org.workaxle.solver;

import org.junit.jupiter.api.Test;
import org.optaplanner.test.api.score.stream.ConstraintVerifier;
import org.workaxle.domain.Employee;
import org.workaxle.domain.Schedule;
import org.workaxle.domain.Shift;
import org.workaxle.domain.ShiftAssignment;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class ScheduleConstraintProviderTest {

    ConstraintVerifier<ScheduleConstraintProvider, Schedule> constraintVerifier = ConstraintVerifier.build(
        new ScheduleConstraintProvider(), Schedule.class, ShiftAssignment.class
    );

    @Test
    void testOneShiftPerEmployeeGroupPerDay() {
        // TODO
        return;
    }

    @Test
    void testAtLeastNHoursBetweenTwoShifts() {
        long i = 1L;
        Shift s1 = new Shift(
            i++,
            "shift A3",
            LocalDateTime.of(2022, 11, 21, 20, 0),
            LocalDateTime.of(2022, 11, 21, 23, 0),
            new HashMap<>() {{
                put("Dev", 1);
                put("Design", 1);
            }}
        );
        Shift s2 = new Shift(
            i++,
            "shift B1",
            LocalDateTime.of(2022, 11, 22, 9, 0),
            LocalDateTime.of(2022, 11, 22, 12, 0),
            new HashMap<>() {{
                put("Dev", 1);
                put("Design", 1);
            }}
        );

        // same employee
        Employee e1 = new Employee(1L, "user 1", new HashSet<>(Arrays.asList("Dev", "Design")));
        Employee e2 = new Employee(1L, "user 1", new HashSet<>(Arrays.asList("Dev", "Design")));

        long j = 1;
        ShiftAssignment sa = new ShiftAssignment(String.valueOf(j++), "Dev", s1, e1);
        ShiftAssignment conflictSa = new ShiftAssignment(String.valueOf(j++), "Dev", s2, e2);

        constraintVerifier
            .verifyThat(ScheduleConstraintProvider::atLeastNHoursBetweenTwoShifts)
            .given(sa, conflictSa)
            .penalizesBy(2 * 60);
    }

    @Test
    void evenlyShiftsDistribution() {
        // TODO
        return;
    }

    @Test
    void onlyRequiredRole() {
        // TODO
        return;
    }

}
