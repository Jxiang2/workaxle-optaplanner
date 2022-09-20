package org.workaxle;

import org.json.simple.parser.ParseException;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.solver.SolverConfig;
import org.workaxle.dao.Data;
import org.workaxle.domain.ShiftAssignment;
import org.workaxle.domain.ShiftSchedule;
import org.workaxle.solver.ScheduleConstraintProvider;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

public class ScheduleApp {

    public static void main(String[] args) {
        // Build solver
        SolverFactory<ShiftSchedule> scheduleSolverFactory = SolverFactory.create(
            new SolverConfig()
                .withSolutionClass(ShiftSchedule.class)
                .withEntityClasses(ShiftAssignment.class)
                .withConstraintProviderClass(ScheduleConstraintProvider.class)
                .withTerminationSpentLimit(Duration.ofSeconds(5))
        );

        try {
            // load the dataset
            ShiftSchedule dataset = Data.generateDate();

            // solve the problem
            Solver<ShiftSchedule> solver = scheduleSolverFactory.buildSolver();
            ShiftSchedule shiftSchedule = solver.solve(dataset);

            // print result
            printResult(shiftSchedule);
        } catch (ParseException e) {
            System.out.println("Invalid data input");
        } catch (IOException e) {
            System.out.println("Input not found");
        }
    }

    private static void printResult(ShiftSchedule shiftSchedule) {
        List<ShiftAssignment> shiftAssignmentList = shiftSchedule.getShiftAssignmentList();
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

    private static ShiftSchedule generateNewData() {
        return null;
    }

}