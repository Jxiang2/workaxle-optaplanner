package org.workaxle.solver;

import org.junit.jupiter.api.Test;
import org.optaplanner.test.api.score.stream.ConstraintVerifier;
import org.workaxle.domain.Schedule;
import org.workaxle.domain.ShiftAssignment;

// TODO
public class ScheduleConstraintProviderTest {

    ConstraintVerifier<ScheduleConstraintProvider, Schedule> constraintVerifier = ConstraintVerifier.build(
        new ScheduleConstraintProvider(), Schedule.class, ShiftAssignment.class
    );

    @Test
    void testOneShiftPerEmployeeGroupPerDay() {
        return;
    }

    @Test
    void testAtLeast12HoursBetweenTwoShifts() {
        return;
    }

    @Test
    void evenlyShiftsDistribution() {
        return;
    }

    @Test
    void requiredRole() {
        return;
    }

}
