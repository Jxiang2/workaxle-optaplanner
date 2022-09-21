package org.workaxle;

import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.solver.SolverConfig;
import org.workaxle.domain.Employee;
import org.workaxle.domain.Schedule;
import org.workaxle.domain.Shift;
import org.workaxle.domain.ShiftAssignment;
import org.workaxle.solver.ScheduleConstraintProvider;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class ScheduleApp {

    public static void main(String[] args) throws Exception {
        // Build solver
        SolverFactory<Schedule> scheduleSolverFactory = SolverFactory.create(
            new SolverConfig()
                .withSolutionClass(Schedule.class)
                .withEntityClasses(ShiftAssignment.class)
                .withConstraintProviderClass(ScheduleConstraintProvider.class)
                .withTerminationSpentLimit(Duration.ofSeconds(5))
        );

        // load the dataset
        Schedule dataset = generateNewData();

        // solve the problem
        Solver<Schedule> solver = scheduleSolverFactory.buildSolver();
        Schedule schedule = solver.solve(dataset);

        // print result
        printResult(schedule);
    }

    private static Schedule generateNewData() {
        long i = 1L;
        List<Shift> shifts = new ArrayList<>();
        shifts.add(new Shift(
            i++,
            "Shift 1A",
            LocalDateTime.of(2022, 11, 21, 9, 0),
            LocalDateTime.of(2022, 11, 21, 12, 0),
            new HashMap<>() {{
                put("Dev", 1);
                put("Design", 1);
            }}
        ));

        shifts.add(new Shift(
            i++,
            "Shift 1B",
            LocalDateTime.of(2022, 11, 21, 15, 0),
            LocalDateTime.of(2022, 11, 21, 18, 0),
            new HashMap<>() {{
                put("Clean", 1);
            }}
        ));

        shifts.add(new Shift(
            i++,
            "Shift 1C",
            LocalDateTime.of(2022, 11, 21, 20, 0),
            LocalDateTime.of(2022, 11, 21, 23, 0),
            new HashMap<>() {{
                put("Dev", 1);
                put("Design", 1);
            }}
        ));

        shifts.add(new Shift(
            i++,
            "Shift 2A",
            LocalDateTime.of(2022, 11, 22, 9, 0),
            LocalDateTime.of(2022, 11, 22, 12, 0),
            new HashMap<>() {{
                put("Dev", 1);
                put("Design", 1);
            }}
        ));

        shifts.add(new Shift(
            i++,
            "Shift 2B",
            LocalDateTime.of(2022, 11, 22, 15, 0),
            LocalDateTime.of(2022, 11, 22, 18, 0),
            new HashMap<>() {{
                put("Clean", 1);
            }}
        ));

        shifts.add(new Shift(
            i++,
            "Shift 2C",
            LocalDateTime.of(2022, 11, 22, 20, 0),
            LocalDateTime.of(2022, 11, 22, 23, 0),
            new HashMap<>() {{
                put("Dev", 1);
                put("Design", 1);
            }}
        ));

        shifts.add(new Shift(
            i++,
            "Shift 3A",
            LocalDateTime.of(2022, 11, 23, 9, 0),
            LocalDateTime.of(2022, 11, 23, 12, 0),
            new HashMap<>() {{
                put("Dev", 1);
                put("Design", 1);
            }}
        ));

        shifts.add(new Shift(
            i++,
            "Shift 3B",
            LocalDateTime.of(2022, 11, 23, 15, 0),
            LocalDateTime.of(2022, 11, 23, 18, 0),
            new HashMap<>() {{
                put("Clean", 1);
            }}
        ));

        shifts.add(new Shift(
            i++,
            "Shift 3C",
            LocalDateTime.of(2022, 11, 23, 20, 0),
            LocalDateTime.of(2022, 11, 23, 23, 0),
            new HashMap<>() {{
                put("Dev", 1);
                put("Design", 1);
            }}
        ));

        shifts.add(new Shift(
            i++,
            "Shift 4A",
            LocalDateTime.of(2022, 11, 24, 9, 0),
            LocalDateTime.of(2022, 11, 24, 12, 0),
            new HashMap<>() {{
                put("Dev", 1);
                put("Design", 1);
            }}
        ));

        shifts.add(new Shift(
            i++,
            "Shift 4B",
            LocalDateTime.of(2022, 11, 24, 15, 0),
            LocalDateTime.of(2022, 11, 24, 18, 0),
            new HashMap<>() {{
                put("Clean", 1);
            }}
        ));

        shifts.add(new Shift(
            i++,
            "Shift 4C",
            LocalDateTime.of(2022, 11, 24, 20, 0),
            LocalDateTime.of(2022, 11, 24, 23, 0),
            new HashMap<>() {{
                put("Dev", 1);
                put("Design", 1);
            }}
        ));

        shifts.add(new Shift(
            i++,
            "Shift 5A",
            LocalDateTime.of(2022, 11, 25, 9, 0),
            LocalDateTime.of(2022, 11, 25, 12, 0),
            new HashMap<>() {{
                put("Dev", 1);
                put("Design", 1);
            }}
        ));

        shifts.add(new Shift(
            i++,
            "Shift 5B",
            LocalDateTime.of(2022, 11, 25, 15, 0),
            LocalDateTime.of(2022, 11, 25, 18, 0),
            new HashMap<>() {{
                put("Clean", 1);
            }}
        ));

        shifts.add(new Shift(
            i++,
            "Shift 5C",
            LocalDateTime.of(2022, 11, 25, 20, 0),
            LocalDateTime.of(2022, 11, 25, 23, 0),
            new HashMap<>() {{
                put("Dev", 1);
                put("Design", 1);
            }}
        ));

        // prepare for shiftAssignments
        List<ShiftAssignment> shiftAssignments = new ArrayList<>();
        int j = 1;
        for (Shift shift : shifts) {
            Map<String, Integer> requiredRoles = shift.getRequiredRoles();
            for (String role : requiredRoles.keySet()) {
                int number = requiredRoles.get(role);
                for (int n = 0; n < number; n++) {
                    shiftAssignments.add(new ShiftAssignment(String.valueOf(j++), role, shift));
                }
            }
        }

        // prepare for employees
        int k = 1;
        List<Employee> employees = new ArrayList<>();
        employees.add(new Employee((long) k++, "user1", Set.of("Dev", "Design")));
        employees.add(new Employee((long) k++, "user2", Set.of("Dev", "Design")));
        employees.add(new Employee((long) k++, "user3", Set.of("Clean")));
        employees.add(new Employee((long) k++, "user4", Set.of("Dev", "Design")));
        employees.add(new Employee((long) k++, "user5", Set.of("Design", "Clean")));
        employees.add(new Employee((long) k++, "user6", Set.of("Dev", "Clean")));

        System.out.println(employees);

        return new Schedule(employees, shiftAssignments);
    }

    private static void printResult(Schedule schedule) {
        List<ShiftAssignment> shiftAssignmentList = schedule.getShiftAssignmentList();
        System.out.println(shiftAssignmentList.size());
        for (int i = 0; i < shiftAssignmentList.size() - 1; i++) {
            ShiftAssignment shiftAssignment = shiftAssignmentList.get(i);
            LocalDate currentDate = shiftAssignment.getDate();
            if (shiftAssignmentList.get(i + 1) != null) {
                if (!shiftAssignmentList.get(i + 1).getDate().equals(currentDate)) {
                    System.out.println(shiftAssignment);
                    System.out.println();
                } else {
                    System.out.println(shiftAssignment);
                }
            }
        }
        System.out.println(shiftAssignmentList.get(shiftAssignmentList.size() - 1));
    }

}