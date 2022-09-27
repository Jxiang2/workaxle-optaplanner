package org.workaxle.solver.base;

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

public class BaseConstraintProviderTest {

    ConstraintVerifier<BaseConstraintProvider, Schedule> constraintVerifier = ConstraintVerifier
        .build(new BaseConstraintProvider(), Schedule.class, ShiftAssignment.class);

    @Test
    void testAtMostOneShiftPerDay() {
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
            LocalDateTime.of(2022, 11, 21, 12, 0),
            LocalDateTime.of(2022, 11, 21, 15, 0),
            new HashMap<>() {{
                put("Dev", 1);
                put("Design", 1);
            }}
        );
        // same employee
        Employee e1 = new Employee(1L, "user 1", new HashSet<>(Arrays.asList("Dev", "Design")));
        long j = 1;
        ShiftAssignment sa1 = new ShiftAssignment(String.valueOf(j++), "Dev", s1, e1);
        ShiftAssignment sa2 = new ShiftAssignment(String.valueOf(j++), "Design", s1, e1);
        constraintVerifier
            .verifyThat(BaseConstraintProvider::atMostOneShiftPerDay)
            .given(sa1, sa2)
            .penalizesBy(24);
    }


    @Test
    void testEvenlyShiftsDistribution() {
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
        Employee e1 = new Employee(1L, "user 1", new HashSet<>(Arrays.asList("Dev", "Design")));
        Employee e2 = new Employee(2L, "user 2", new HashSet<>(Arrays.asList("Dev", "Design")));
        Employee e3 = new Employee(3L, "user 3", new HashSet<>(Arrays.asList("Dev", "Design")));
        long j = 1;
        ShiftAssignment sa1 = new ShiftAssignment(String.valueOf(j++), "Dev", s1, e1);
        ShiftAssignment sa2 = new ShiftAssignment(String.valueOf(j++), "Design", s1, e2);
        ShiftAssignment sa3 = new ShiftAssignment(String.valueOf(j++), "Design", s2, e3);
        ShiftAssignment sa4 = new ShiftAssignment(String.valueOf(j++), "Dev", s2, e3);
        int penalty = 2 * 2 + 1 + 1;
        constraintVerifier
            .verifyThat(BaseConstraintProvider::evenlyShiftsDistribution)
            .given(
                sa1,
                sa2,
                sa3,
                sa4
            )
            .penalizesBy(penalty);
    }

    @Test
    void testNoOverlappingShifts() {
        Shift s1 = new Shift(
            1L,
            "shift A3",
            LocalDateTime.of(2022, 11, 21, 9, 0),
            LocalDateTime.of(2022, 11, 21, 12, 0),
            new HashMap<>() {{
                put("Dev", 1);
                put("Design", 1);
            }}
        );
        Shift s2 = new Shift(
            1L,
            "shift A3",
            LocalDateTime.of(2022, 11, 21, 8, 0),
            LocalDateTime.of(2022, 11, 21, 11, 0),
            new HashMap<>() {{
                put("Dev", 1);
                put("Design", 1);
            }}
        );
        Employee e1 = new Employee(1L, "user 1", new HashSet<>(Arrays.asList("Dev", "Design")));
        long j = 1;
        ShiftAssignment sa1 = new ShiftAssignment(String.valueOf(j++), "Dev", s1, e1);
        ShiftAssignment sa2 = new ShiftAssignment(String.valueOf(j++), "Design", s2, e1);
        constraintVerifier
            .verifyThat(BaseConstraintProvider::noOverlappingShifts)
            .given(
                sa1,
                sa2
            )
            .penalizesBy(2);
    }

    @Test
    void testOnlyRequiredRole() {
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
        Employee e1 = new Employee(1L, "user 1", new HashSet<>(Arrays.asList("Security", "Clean")));
        ShiftAssignment sa1 = new ShiftAssignment(String.valueOf(1), "Dev", s1, e1);
        constraintVerifier
            .verifyThat(BaseConstraintProvider::onlyRequiredRole)
            .given(
                sa1
            )
            .penalizesBy(100);
    }

}
