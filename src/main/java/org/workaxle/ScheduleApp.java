package org.workaxle;

import org.json.simple.parser.ParseException;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.solver.SolverConfig;
import org.workaxle.dao.Data;
import org.workaxle.domain.Schedule;
import org.workaxle.domain.ShiftAssignment;
import org.workaxle.solver.ScheduleConstraintProvider;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class ScheduleApp {

    public static void main(String[] args) throws Exception {

        // Build solver
        SolverConfig solverConfig = SolverConfig.createFromXmlResource("scheduleConfig.xml");

        solverConfig
            .withSolutionClass(Schedule.class)
            .withEntityClasses(ShiftAssignment.class)
            .withConstraintProviderClass(ScheduleConstraintProvider.class)
            .withTerminationSpentLimit(Duration.ofSeconds(5));

        SolverFactory<Schedule> scheduleSolverFactory = SolverFactory.create(solverConfig);

        // load the dataset
        Schedule dataset = Data.generateData();

        // solve the problem
        Solver<Schedule> solver = scheduleSolverFactory.buildSolver();
        Schedule schedule = solver.solve(dataset);

        // print result
        printResult(schedule);
    }

    private static void printResult(Schedule schedule) throws IOException, ParseException {
        List<ShiftAssignment> shiftAssignmentList = schedule.getShiftAssignmentList();

        final LocalDate shiftSStartDay = Data.generateStartEndDates()[0];
        final LocalDate shiftsEndDay = Data.generateStartEndDates()[1];

        int duration = 12;
        dropInvalidByInterShiftDuration(duration, shiftAssignmentList);

        System.out.println("Total number of shift assignments: " + shiftAssignmentList.size() + "\n");

        LocalDate currentDay = shiftSStartDay;
        while (currentDay.isBefore(shiftsEndDay.plusDays(1))) {
            for (ShiftAssignment shiftAssignment : shiftAssignmentList) {
                if (shiftAssignment.getDate().equals(currentDay)) {
                    System.out.println(shiftAssignment);
                }
            }
            currentDay = currentDay.plusDays(1);
            System.out.println();
        }

    }

    private static void dropInvalidByInterShiftDuration(
        int duration,
        List<ShiftAssignment> shiftAssignmentList
    ) {
        for (int i = 0; i < shiftAssignmentList.size(); i++) {
            ShiftAssignment first = shiftAssignmentList.get(i);
            LocalDateTime firstEndAt = first.getShift().getEndAt();
            for (ShiftAssignment second : shiftAssignmentList) {
                LocalDateTime secondStartAt = second.getShift().getStartAt();
                if (first.getEmployee().equals(second.getEmployee()) &&
                    secondStartAt.isAfter(firstEndAt) &&
                    Math.abs(Duration.between(firstEndAt, secondStartAt).toHours()) < duration
                ) {
                    first.setValid(false);
                }

            }
        }
    }

}