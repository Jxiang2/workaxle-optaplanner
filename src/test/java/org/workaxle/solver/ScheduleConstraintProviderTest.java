package org.workaxle.solver;

import org.junit.jupiter.api.Test;
import org.optaplanner.test.api.score.stream.ConstraintVerifier;
import org.workaxle.domain.Employee;
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

        long j = 1L;
        Employee e1 = new Employee(j++, "emp group A", new HashMap<>() {{
            put("Dev", 1);
            put("Design", 1);
        }});
        Employee e2 = new Employee(j++, "emp group B", new HashMap<>() {{
            put("Dev", 1);
            put("Design", 1);
        }});

        long k = 1L;
        ShiftAssignment firstSa = new ShiftAssignment(k++, s1, LocalDate.of(2022, 11, 21), e1);
        ShiftAssignment conflictSa = new ShiftAssignment(k++, s2, LocalDate.of(2022, 11, 21), e1);
        ShiftAssignment nonConflictSa1 = new ShiftAssignment(k++, s2, LocalDate.of(2022, 11, 21), e2);
        ShiftAssignment nonConflictSa2 = new ShiftAssignment(k++, s1, LocalDate.of(2022, 11, 22), e1);

        constraintVerifier
            .verifyThat(ScheduleConstraintProvider::oneShiftPerEmployeePerDay)
            .given(firstSa, conflictSa, nonConflictSa1, nonConflictSa2)
            .penalizesBy(1);
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

        long j = 1L;
        Employee e1 = new Employee(j++, "emp group A", new HashMap<>() {{
            put("Dev", 1);
            put("Design", 1);
        }});

        long k = 1L;
        ShiftAssignment firstSa = new ShiftAssignment(k++, s1, LocalDate.of(2022, 11, 21), e1);
        ShiftAssignment conflictSa = new ShiftAssignment(k++, s2, LocalDate.of(2022, 11, 22), e1);
        ShiftAssignment nonConflictSa = new ShiftAssignment(k++, s2, LocalDate.of(2022, 11, 23), e1);

        constraintVerifier
            .verifyThat(ScheduleConstraintProvider::atLeast12HoursBetweenTwoShifts)
            .given(firstSa, conflictSa)
            .penalizesBy(120);
    }

}
