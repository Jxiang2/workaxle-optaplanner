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

        try {
            // load the dataset
            Schedule dataset = Data.generateDate();

            // solve the problem
            Solver<Schedule> solver = scheduleSolverFactory.buildSolver();
            Schedule schedule = solver.solve(dataset);

            // print result
            printResult(schedule);
        } catch (ParseException e) {
            System.out.println("Invalid data input");
        } catch (IOException e) {
            System.out.println("Input not found");
        }
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