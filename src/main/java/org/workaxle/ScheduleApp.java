package org.workaxle;

import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.solver.SolverConfig;
import org.workaxle.bootstrap.Data;
import org.workaxle.domain.Schedule;
import org.workaxle.domain.ShiftAssignment;
import org.workaxle.solver.ScheduleConstraintProvider;
import org.workaxle.util.solution.SolutionPrinter;

import java.time.Duration;

public class ScheduleApp {

    public static void main(String[] args) throws Exception {

        // Build solver
        SolverConfig solverConfig = SolverConfig.createFromXmlResource("scheduleConfig.xml");

        solverConfig
            .withSolutionClass(Schedule.class)
            .withEntityClasses(ShiftAssignment.class)
            .withConstraintProviderClass(ScheduleConstraintProvider.class)
            .withTerminationSpentLimit(Duration.ofSeconds(3));

        SolverFactory<Schedule> scheduleSolverFactory = SolverFactory.create(solverConfig);

        // load the dataset
        Schedule dataset = Data.generateData();

        // solve the problem
        Solver<Schedule> solver = scheduleSolverFactory.buildSolver();
        Schedule schedule = solver.solve(dataset);

        // print result
        SolutionPrinter.printResult(schedule);
    }

}