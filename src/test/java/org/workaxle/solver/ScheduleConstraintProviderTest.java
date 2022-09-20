package org.workaxle.solver;

import org.junit.jupiter.api.Test;
import org.optaplanner.test.api.score.stream.ConstraintVerifier;
import org.workaxle.domain.EmployeeGroup;
import org.workaxle.domain.Schedule;
import org.workaxle.domain.Shift;
import org.workaxle.domain.ShiftAssignment;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;

public class ScheduleConstraintProviderTest {

    ConstraintVerifier<ScheduleConstraintProvider, Schedule> constraintVerifier = ConstraintVerifier.build(
        new ScheduleConstraintProvider(), Schedule.class, ShiftAssignment.class
    );

    @Test
    void testOneShiftPerEmployeeGroupPerDay() {
        long i = 1L;
        Shift s1 = new Shift(
            i++,
            "shift A",
            LocalTime.of(9, 0),
            LocalTime.of(12, 0),
            new HashMap<>() {{
                put("Dev", 1);
                put("Design", 1);
            }}
        );
        Shift s2 = new Shift(
            i++,
            "shift B",
            LocalTime.of(12, 0),
            LocalTime.of(15, 0),
            new HashMap<>() {{
                put("Dev", 1);
                put("Design", 1);
            }}
        );

        EmployeeGroup e1 = new EmployeeGroup("1", new HashMap<>() {{
            put("Dev", 1);
            put("Design", 1);
        }});
        EmployeeGroup e2 = new EmployeeGroup("1", new HashMap<>() {{
            put("Dev", 1);
            put("Design", 1);
        }});

        ShiftAssignment firstSa = new ShiftAssignment("1", s1, LocalDate.of(2022, 11, 21), e1);
        ShiftAssignment conflictSa = new ShiftAssignment("2", s2, LocalDate.of(2022, 11, 21), e1);
        ShiftAssignment nonConflictSa1 = new ShiftAssignment("3", s2, LocalDate.of(2022, 11, 21), e2);
        ShiftAssignment nonConflictSa2 = new ShiftAssignment("4", s1, LocalDate.of(2022, 11, 22), e1);

        constraintVerifier
            .verifyThat(ScheduleConstraintProvider::oneShiftPerEmployeePerDay)
            .given(firstSa, conflictSa, nonConflictSa1, nonConflictSa2)
            .penalizesBy(3);
    }

    @Test
    void testAtLeast12HoursBetweenTwoShifts() {
        long i = 1L;
        Shift s1 = new Shift(
            i++,
            "shift A",
            LocalTime.of(20, 0),
            LocalTime.of(23, 0),
            new HashMap<>() {{
                put("Dev", 1);
                put("Design", 1);
            }}
        );
        Shift s2 = new Shift(
            i++,
            "shift B",
            LocalTime.of(9, 0),
            LocalTime.of(12, 0),
            new HashMap<>() {{
                put("Dev", 1);
                put("Design", 1);
            }}
        );

        EmployeeGroup e1 = new EmployeeGroup("1", new HashMap<>() {{
            put("Dev", 1);
            put("Design", 1);
        }});

        ShiftAssignment firstSa = new ShiftAssignment("1", s1, LocalDate.of(2022, 11, 21), e1);
        ShiftAssignment conflictSa = new ShiftAssignment("2", s2, LocalDate.of(2022, 11, 22), e1);
        ShiftAssignment nonConflictSa = new ShiftAssignment("3", s2, LocalDate.of(2022, 11, 23), e1);

        constraintVerifier
            .verifyThat(ScheduleConstraintProvider::atLeast12HoursBetweenTwoShifts)
            .given(firstSa, conflictSa)
            .penalizesBy(2);
    }

}
