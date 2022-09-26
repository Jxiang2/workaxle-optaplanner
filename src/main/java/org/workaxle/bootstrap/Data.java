package org.workaxle.bootstrap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.workaxle.domain.Employee;
import org.workaxle.domain.Schedule;
import org.workaxle.domain.Shift;
import org.workaxle.domain.ShiftAssignment;

import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

public class Data {

    public static Schedule generateData() throws IOException, ParseException {
        final JSONParser parser = new JSONParser();
        final FileReader fileReader = new FileReader(
            "src/main/java/org/workaxle/bootstrap/data.json"
        );
        final JSONObject jsonInput = (JSONObject) parser.parse(fileReader);

        final List<Employee> employeeList = generateValidEmployees(jsonInput);
        final List<ShiftAssignment> shiftAssignmentList = generateShiftAssignments(
            generateShifts(jsonInput)
        );

        return new Schedule(employeeList, shiftAssignmentList);
    }

    private static List<Employee> generateValidEmployees(
        JSONObject jsonInput
    ) throws JsonProcessingException {
        final List<Employee> employeesInput = new ArrayList<>();

        for (Object o : (JSONArray) jsonInput.get("employees")) {
            final JSONObject employeeJson = (JSONObject) o;
            final JSONArray rolesJson = (JSONArray) employeeJson.get("roles");

            final Set<String> roleSet = new HashSet<>();
            rolesJson.forEach(role -> roleSet.add(role.toString()));
            final Employee employee = new Employee(
                (Long) employeeJson.get("id"),
                (String) employeeJson.get("name"),
                roleSet
            );
            employeesInput.add(employee);
        }

        final ArrayList allRoleList = new ObjectMapper()
            .readValue(jsonInput.get("allRequiredRoles").toString(), ArrayList.class);
        final ArrayList<Employee> validatedEmployeesInput = new ArrayList<>();

        int employeesInputSize = employeesInput.size();
        while (employeesInputSize > 0) {
            int employeeIndex = employeesInputSize - 1;
            final Employee currentEmployee = employeesInput.get(employeeIndex);

            final Set<String> employeeRoleSet = currentEmployee.getRoleSet();
            final Set<String> validatedEmployeeRoleSet = new HashSet<>();
            boolean contains = false;
            for (String employeeRole : employeeRoleSet) {
                if (allRoleList.contains(employeeRole)) {
                    validatedEmployeeRoleSet.add(employeeRole);
                    contains = true;
                }
            }
            if (contains) {
                currentEmployee.setRoleSet(validatedEmployeeRoleSet);
                validatedEmployeesInput.add(currentEmployee);
            }
            employeesInputSize--;
        }

        return validatedEmployeesInput;
    }

    private static List<ShiftAssignment> generateShiftAssignments(List<Shift> shifts) {
        final List<ShiftAssignment> shiftAssignments = new ArrayList<>();
        long j = 1;
        for (Shift shift : shifts) {
            final Map<String, Integer> requiredRoles = shift.getRequiredRoles();
            for (String role : requiredRoles.keySet()) {
                final int number = requiredRoles.get(role);
                for (int n = 0; n < number; n++) {
                    shiftAssignments.add(new ShiftAssignment(String.valueOf(j++), role, shift));
                }
            }
        }

        return shiftAssignments
            .stream()
            .sorted(Comparator.comparing(
                sa -> {
                    final LocalDateTime startAt = sa.getShift().getStartAt();
                    final LocalDateTime endAt = sa.getShift().getEndAt();
                    return startAt.plusMinutes(Duration.between(endAt, startAt).toMinutes() / 2);
                }
            ))
            .collect(Collectors.toList())
            ;
    }

    private static List<Shift> generateShifts(JSONObject jsonInput) throws JsonProcessingException {
        final LocalDate startDate = LocalDate.parse((String) jsonInput.get("startDate"));
        final LocalDate endDate = LocalDate.parse((String) jsonInput.get("endDate"));

        final List<Shift> shiftsInput = new ArrayList<>();
        for (Object o : (JSONArray) jsonInput.get("shifts")) {
            LocalDate currentDate = startDate;
            while (currentDate.isBefore(endDate.plusDays(1))) {
                final JSONObject shiftJson = (JSONObject) o;
                final JSONObject requiredRolesJson = (JSONObject) shiftJson.get("requiredRoles");

                shiftsInput.add(
                    new Shift(
                        (Long) shiftJson.get("id"),
                        (String) shiftJson.get("name"),
                        LocalDateTime.of(
                            currentDate,
                            LocalTime.parse((String) shiftJson.get("startAt"))
                        ),
                        LocalDateTime.of(
                            currentDate,
                            LocalTime.parse((String) shiftJson.get("endAt"))
                        ),
                        new ObjectMapper()
                            .readValue(requiredRolesJson.toJSONString(), HashMap.class)
                    )
                );

                currentDate = currentDate.plusDays(1);
            }
        }

        return shiftsInput;
    }

    public static LocalDate[] generateStartEndDates() throws IOException, ParseException {
        final JSONParser parser = new JSONParser();
        final FileReader fileReader = new FileReader(
            "src/main/java/org/workaxle/bootstrap/data.json"
        );
        final JSONObject jsonInput = (JSONObject) parser.parse(fileReader);

        return new LocalDate[]{
            LocalDate.parse((String) jsonInput.get("startDate")),
            LocalDate.parse((String) jsonInput.get("endDate"))
        };
    }

}
