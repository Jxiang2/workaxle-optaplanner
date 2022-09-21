package org.workaxle.solver;

import org.junit.jupiter.api.Test;
import org.optaplanner.test.api.score.stream.ConstraintVerifier;
import org.workaxle.domain.Schedule;
import org.workaxle.domain.ShiftEmployee;

public class ScheduleConstraintProviderTest {

    ConstraintVerifier<ScheduleConstraintProvider, Schedule> constraintVerifier = ConstraintVerifier.build(
        new ScheduleConstraintProvider(), Schedule.class, ShiftEmployee.class
    );

    @Test
    void testOneShiftPerEmployeeGroupPerDay() {
        // TODO
        return;
    }

    @Test
    void testAtLeast12HoursBetweenTwoShifts() {
        // TODO
        return;
    }

    @Test
    void evenlyShiftsDistribution() {
        // TODO
        return;
    }

    @Test
    void requiredRole() {
        // TODO
        return;
    }

}
