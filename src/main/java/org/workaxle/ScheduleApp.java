package org.workaxle;

import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.solver.SolverConfig;
import org.workaxle.domain.EmployeeGroup;
import org.workaxle.domain.Schedule;
import org.workaxle.domain.Shift;
import org.workaxle.domain.ShiftAssignment;
import org.workaxle.solver.ScheduleConstraintProvider;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ScheduleApp {

    public static void main(String[] args) {
        // Build solver
        SolverFactory<Schedule> scheduleSolverFactory = SolverFactory.create(
            new SolverConfig()
                .withSolutionClass(Schedule.class)
                .withEntityClasses(ShiftAssignment.class)
                .withConstraintProviderClass(ScheduleConstraintProvider.class)
                .withTerminationSpentLimit(Duration.ofSeconds(5))
        );

        // load the dataset
        Schedule dataset = generateData();

        // solve the problem
        Solver<Schedule> solver = scheduleSolverFactory.buildSolver();
        Schedule schedule = solver.solve(dataset);

        // print result
        printResult(schedule);
    }

    private static Schedule generateData() {
        // total shifts: 5 (days) * 4 (shiftsPerDay) = 20
        // total shifts per employee group: 20 / 6 = 3 ~ 4

        long i = 1L;
        Shift s1 = new Shift(i++, "shift A", LocalTime.of(9, 0), LocalTime.of(12, 0));
        Shift s2 = new Shift(i++, "shift B", LocalTime.of(12, 0), LocalTime.of(15, 0));
        Shift s3 = new Shift(i++, "shift C", LocalTime.of(15, 0), LocalTime.of(18, 0));
        Shift s4 = new Shift(i++, "shift D", LocalTime.of(18, 0), LocalTime.of(23, 0));

        long j = 1L;
        List<EmployeeGroup> employeeGroupList = new ArrayList<>();
        employeeGroupList.add(new EmployeeGroup(j++, "emp group A"));
        employeeGroupList.add(new EmployeeGroup(j++, "emp group B"));
        employeeGroupList.add(new EmployeeGroup(j++, "emp group C"));
        employeeGroupList.add(new EmployeeGroup(j++, "emp group D"));
        employeeGroupList.add(new EmployeeGroup(j++, "emp group E"));
        employeeGroupList.add(new EmployeeGroup(j++, "emp group F"));

        long k = 1L;
        List<ShiftAssignment> shiftAssignmentList = new ArrayList<>();
        shiftAssignmentList.add(new ShiftAssignment(k++, s1, LocalDate.of(2022, 11, 21)));
        shiftAssignmentList.add(new ShiftAssignment(k++, s2, LocalDate.of(2022, 11, 21)));
        shiftAssignmentList.add(new ShiftAssignment(k++, s3, LocalDate.of(2022, 11, 21)));
        shiftAssignmentList.add(new ShiftAssignment(k++, s4, LocalDate.of(2022, 11, 21)));

        shiftAssignmentList.add(new ShiftAssignment(k++, s1, LocalDate.of(2022, 11, 22)));
        shiftAssignmentList.add(new ShiftAssignment(k++, s2, LocalDate.of(2022, 11, 22)));
        shiftAssignmentList.add(new ShiftAssignment(k++, s3, LocalDate.of(2022, 11, 22)));
        shiftAssignmentList.add(new ShiftAssignment(k++, s4, LocalDate.of(2022, 11, 22)));

        shiftAssignmentList.add(new ShiftAssignment(k++, s1, LocalDate.of(2022, 11, 23)));
        shiftAssignmentList.add(new ShiftAssignment(k++, s2, LocalDate.of(2022, 11, 23)));
        shiftAssignmentList.add(new ShiftAssignment(k++, s3, LocalDate.of(2022, 11, 23)));
        shiftAssignmentList.add(new ShiftAssignment(k++, s4, LocalDate.of(2022, 11, 23)));

        shiftAssignmentList.add(new ShiftAssignment(k++, s1, LocalDate.of(2022, 11, 24)));
        shiftAssignmentList.add(new ShiftAssignment(k++, s2, LocalDate.of(2022, 11, 24)));
        shiftAssignmentList.add(new ShiftAssignment(k++, s3, LocalDate.of(2022, 11, 24)));
        shiftAssignmentList.add(new ShiftAssignment(k++, s4, LocalDate.of(2022, 11, 24)));

        shiftAssignmentList.add(new ShiftAssignment(k++, s1, LocalDate.of(2022, 11, 25)));
        shiftAssignmentList.add(new ShiftAssignment(k++, s2, LocalDate.of(2022, 11, 25)));
        shiftAssignmentList.add(new ShiftAssignment(k++, s3, LocalDate.of(2022, 11, 25)));
        shiftAssignmentList.add(new ShiftAssignment(k++, s4, LocalDate.of(2022, 11, 25)));

        return new Schedule(employeeGroupList, shiftAssignmentList);
    }

    private static void printResult(Schedule schedule) {
        List<ShiftAssignment> shiftAssignmentList = schedule.getShiftAssignmentList();
        int currentShiftPerDay = 1;
        for (ShiftAssignment sf : shiftAssignmentList) {
            System.out.println(sf);
            if (currentShiftPerDay == 4) {
                currentShiftPerDay = 0;
                System.out.println();

            }
            currentShiftPerDay += 1;
        }
    }

}