package org.workaxle.bootstrap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.workaxle.domain.*;

import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

public class Data {

    static String dataFilePath
        = "src/main/java/org/workaxle/bootstrap/examples/parallelShiftsData.json";

    public static Schedule generateData() throws IOException, ParseException {
        final JSONParser parser = new JSONParser();
        final FileReader fileReader = new FileReader(dataFilePath);
        final JSONObject jsonInput = (JSONObject) parser.parse(fileReader);

        final Object[] shiftsAndRoles = generateShifts(jsonInput);
        final Settings settings = generateSettings(jsonInput);
        final List<Employee> employeeList = generateValidEmployees(
            jsonInput, (Set<String>) shiftsAndRoles[1]
        );
        final List<ShiftAssignment> shiftAssignmentList = generateShiftAssignments(
            (List<Shift>) shiftsAndRoles[0]
        );

        return new Schedule(settings, employeeList, shiftAssignmentList);
    }

    private static Settings generateSettings(JSONObject jsonInput) throws JsonProcessingException {
        JSONObject settingsJson = (JSONObject) jsonInput.get("settings");
        return new ObjectMapper()
            .readValue(settingsJson.toJSONString(), Settings.class);
    }

    private static List<Employee> generateValidEmployees(
        JSONObject jsonInput,
        Set<String> allRoleSet
    ) throws JsonProcessingException {
        System.out.println(allRoleSet);
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

        final ArrayList<Employee> validatedEmployeesInput = new ArrayList<>();

        int employeesInputSize = employeesInput.size();
        while (employeesInputSize > 0) {
            final int employeeIndex = employeesInputSize - 1;
            final Employee currentEmployee = employeesInput.get(employeeIndex);

            final Set<String> employeeRoleSet = currentEmployee.getRoleSet();
            final Set<String> validatedEmployeeRoleSet = new HashSet<>();
            boolean contains = false;
            for (String employeeRole : employeeRoleSet) {
                if (allRoleSet.contains(employeeRole)) {
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

        return shiftAssignments;
    }

    private static Object[] generateShifts(JSONObject jsonInput) throws JsonProcessingException {
        final LocalDate startDate = LocalDate.parse((String) jsonInput.get("startDate"));
        final LocalDate endDate = LocalDate.parse((String) jsonInput.get("endDate"));
        final Set<String> allRoleSet = new HashSet<>();

        final List<Shift> shiftsInput = new ArrayList<>();
        for (Object o : (JSONArray) jsonInput.get("shifts")) {
            LocalDate currentDate = startDate;
            while (currentDate.isBefore(endDate.plusDays(1))) {
                final JSONObject shiftJson = (JSONObject) o;
                final JSONObject requiredRolesJson = (JSONObject) shiftJson.get("requiredRoles");

                final HashMap<String, Integer> requiredRoles = new ObjectMapper()
                    .readValue(requiredRolesJson.toJSONString(), HashMap.class);

                for (String role : requiredRoles.keySet()) {
                    allRoleSet.add(role);
                }

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
                        requiredRoles
                    )
                );

                currentDate = currentDate.plusDays(1);
            }
        }

        return new Object[]{
            shiftsInput,
            allRoleSet
        };
    }

    public static LocalDate[] generateStartEndDates() throws IOException, ParseException {
        final JSONParser parser = new JSONParser();
        final FileReader fileReader = new FileReader(dataFilePath);
        final JSONObject jsonInput = (JSONObject) parser.parse(fileReader);

        return new LocalDate[]{
            LocalDate.parse((String) jsonInput.get("startDate")),
            LocalDate.parse((String) jsonInput.get("endDate"))
        };
    }

}
