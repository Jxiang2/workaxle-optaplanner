package org.workaxle;

import java.time.Duration;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.config.solver.SolverConfig;
import org.workaxle.bootstrap.Data;
import org.workaxle.domain.Schedule;
import org.workaxle.domain.ShiftAssignment;
import org.workaxle.solver.AggregateConstraintProvider;
import org.workaxle.util.solution.SolutionPrinter;

public class ScheduleApp {

  public static void main(String[] args) throws Exception {

    // Build solver
    SolverConfig solverConfig = SolverConfig.createFromXmlResource(
        "scheduleConfig.xml"
    );

    solverConfig
        .withSolutionClass(Schedule.class)
        .withEntityClasses(ShiftAssignment.class)
        .withConstraintProviderClass(AggregateConstraintProvider.class)
        .withEnvironmentMode(EnvironmentMode.REPRODUCIBLE)
        .withTerminationSpentLimit(Duration.ofSeconds(5));

    SolverFactory<Schedule> scheduleSolverFactory =
        SolverFactory.create(solverConfig);

    // load the dataset
    Schedule dataset = Data.generateData();

    // solve the problem
    Solver<Schedule> solver = scheduleSolverFactory.buildSolver();
    Schedule schedule = solver.solve(dataset);

    // print result
    SolutionPrinter.printResult(schedule);
  }

}