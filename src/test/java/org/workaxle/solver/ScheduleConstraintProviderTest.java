package org.workaxle.solver;

import org.junit.jupiter.api.Test;
import org.optaplanner.test.api.score.stream.ConstraintVerifier;
import org.workaxle.domain.EmployeeGroup;
import org.workaxle.domain.Schedule;
import org.workaxle.domain.Shift;
import org.workaxle.domain.ShiftAssignment;

import java.time.LocalDate;
import java.time.LocalTime;

public class ScheduleConstraintProviderTest {

    @Test
    void testOneShiftPerEmployeeGroupPerDay() {
        long i = 1L;
        Shift s1 = new Shift(i++, "shift A", LocalTime.of(9, 0), LocalTime.of(12, 0));

        long j = 1L;
        EmployeeGroup e1 = new EmployeeGroup(j++, "emp group A");
        EmployeeGroup e2 = new EmployeeGroup(j++, "emp group B");

        long k = 1L;
        ShiftAssignment firstSa = new ShiftAssignment(k++, s1, LocalDate.of(2022, 11, 21), e1);
        ShiftAssignment conflictSa = new ShiftAssignment(k++, s1, LocalDate.of(2022, 11, 21), e1);
        ShiftAssignment nonConflictSa1 = new ShiftAssignment(k++, s1, LocalDate.of(2022, 11, 21), e2);
        ShiftAssignment nonConflictSa2 = new ShiftAssignment(k++, s1, LocalDate.of(2022, 11, 22), e1);

        ConstraintVerifier<ScheduleConstraintProvider, Schedule> constraintVerifier = ConstraintVerifier.build(
            new ScheduleConstraintProvider(), Schedule.class, ShiftAssignment.class
        );

        constraintVerifier
            .verifyThat(ScheduleConstraintProvider::oneShiftPerEmployeeGroupPerDay)
            .given(firstSa, conflictSa, nonConflictSa1, nonConflictSa2)
            .penalizesBy(1);
    }

}
