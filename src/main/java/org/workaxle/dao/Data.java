package org.workaxle.dao;

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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

public class Data {

    public static Schedule generateData() throws IOException, ParseException {
        final JSONParser parser = new JSONParser();
        final FileReader fileReader = new FileReader("src/main/java/org/workaxle/dao/data.json");
        JSONObject jsonInput = (JSONObject) parser.parse(fileReader);

        List<Employee> employeeList = generateValidEmployees(jsonInput);
        List<Shift> shiftList = generateShifts(jsonInput);
        List<ShiftAssignment> shiftAssignmentList = generateShiftAssignments(shiftList);

        return new Schedule(employeeList, shiftAssignmentList);
    }

    private static List<Employee> generateValidEmployees(JSONObject jsonInput) throws JsonProcessingException {
        List<Employee> employeesInput = new ArrayList<>();

        for (Object o : (JSONArray) jsonInput.get("employees")) {
            JSONObject employeeJson = (JSONObject) o;
            JSONArray rolesJson = (JSONArray) employeeJson.get("roles");

            Set<String> roleSet = new HashSet<>();
            rolesJson.forEach(role -> roleSet.add(role.toString()));
            Employee employee = new Employee(
                (Long) employeeJson.get("id"),
                (String) employeeJson.get("name"),
                roleSet
            );
            employeesInput.add(employee);
        }

        ArrayList allRoleList =
            new ObjectMapper().readValue(jsonInput.get("roles").toString(), ArrayList.class);
        ArrayList<Employee> validatedEmployeesInput = new ArrayList<>();

        int employeesInputSize = employeesInput.size();
        while (employeesInputSize > 0) {
            int employeeIndex = employeesInputSize - 1;
            Employee currentEmployee = employeesInput.get(employeeIndex);

            Set<String> employeeRoleSet = currentEmployee.getRoleSet();
            Set<String> validatedEmployeeRoleSet = new HashSet<>();
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

        Collections.reverse(validatedEmployeesInput);

        return validatedEmployeesInput;
    }

    private static List<Shift> generateShifts(JSONObject jsonInput) throws JsonProcessingException {
        LocalDate startDate = LocalDate.parse((String) jsonInput.get("startDate"));
        LocalDate endDate = LocalDate.parse((String) jsonInput.get("endDate"));

        List<Shift> shiftsInput = new ArrayList<>();
        for (Object o : (JSONArray) jsonInput.get("shifts")) {
            LocalDate currentDate = startDate;
            while (currentDate.isBefore(endDate.plusDays(2))) {
                JSONObject shiftJson = (JSONObject) o;
                JSONObject requiredRolesJson = (JSONObject) shiftJson.get("requiredRoles");

                HashMap requiredRoles = new ObjectMapper()
                    .readValue(requiredRolesJson.toJSONString(), HashMap.class);

                LocalDateTime currentStartDatetime = LocalDateTime.of(
                    currentDate,
                    LocalTime.parse((String) shiftJson.get("startAt"))
                );

                LocalDateTime currentEndDatetime = LocalDateTime.of(
                    currentDate,
                    LocalTime.parse((String) shiftJson.get("endAt"))
                );

                Shift shift = new Shift(
                    (Long) shiftJson.get("id"),
                    (String) shiftJson.get("name"),
                    currentStartDatetime,
                    currentEndDatetime,
                    requiredRoles
                );
                shiftsInput.add(shift);

                currentDate = currentDate.plusDays(1);
            }
        }

        return shiftsInput;
    }

    private static List<ShiftAssignment> generateShiftAssignments(List<Shift> shifts) {
        List<ShiftAssignment> shiftAssignments = new ArrayList<>();
        long j = 1;
        for (Shift shift : shifts) {
            Map<String, Integer> requiredRoles = shift.getRequiredRoles();
            for (String role : requiredRoles.keySet()) {
                int number = requiredRoles.get(role);
                for (int n = 0; n < number; n++) {
                    shiftAssignments.add(new ShiftAssignment(String.valueOf(j++), role, shift));
                }
            }
        }
        return shiftAssignments;
    }

    public static LocalDate[] generateStartEndDates() throws IOException, ParseException {
        final JSONParser parser = new JSONParser();
        final FileReader fileReader = new FileReader("src/main/java/org/workaxle/dao/data.json");
        JSONObject jsonInput = (JSONObject) parser.parse(fileReader);

        return new LocalDate[]{
            LocalDate.parse((String) jsonInput.get("startDate")),
            LocalDate.parse((String) jsonInput.get("endDate"))
        };
    }

}
