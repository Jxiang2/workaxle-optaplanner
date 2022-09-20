package org.workaxle.dao;

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
import java.time.ZoneId;
import java.util.*;

public class Data {

    public static void main(String[] args) {
        final Schedule schedule = generateDate();
        final List<EmployeeGroup> employeeGroupList = schedule.getEmployeeGroupList();
        for (EmployeeGroup employeeGroup : employeeGroupList) {
            System.out.println(employeeGroup);
        }
        System.out.println(schedule.getShiftAssignmentList());


    }

    public static Schedule generateDate() {
        try {
            final JSONParser parser = new JSONParser();
            final FileReader fileReader = new FileReader("src/main/java/org/workaxle/dao/data.json");
            JSONObject jsonInput = (JSONObject) parser.parse(fileReader);

            LocalDate startDate = LocalDate.parse((String) jsonInput.get("startDate"));
            LocalDate endDate = LocalDate.parse((String) jsonInput.get("endDate"));

            List<Employee> employeesInput = new ArrayList<>();
            for (Object o : (JSONArray) jsonInput.get("employees")) {
                Employee employee = new Employee();

                JSONObject employeeJson = (JSONObject) o;
                JSONArray rolesJson = (JSONArray) employeeJson.get("roles");
                employee.setId((Long) employeeJson.get("id"));
                employee.setName((String) employeeJson.get("name"));
                List<String> roles = new ArrayList<>();
                rolesJson.forEach(role -> roles.add(role.toString()));
                employee.setRoleList(roles);

                employeesInput.add(employee);
            }

            List<Shift> shiftsInput = new ArrayList<>();
            for (Object o : (JSONArray) jsonInput.get("shifts")) {
                Shift shift = new Shift();

                JSONObject shiftJson = (JSONObject) o;
                JSONObject requiredRolesJson = (JSONObject) shiftJson.get("requiredRoles");
                HashMap<String, Integer> requiredRoles = new ObjectMapper()
                    .readValue(requiredRolesJson.toJSONString(), HashMap.class);
                shift.setId((Long) shiftJson.get("id"));
                shift.setName((String) shiftJson.get("name"));
                shift.setStartAt(LocalTime.parse((String) shiftJson.get("startAt")));
                shift.setEndAt(LocalTime.parse((String) shiftJson.get("endAt")));
                shift.setRequiredRoles(requiredRoles);

                shiftsInput.add(shift);
            }

            System.out.println(shiftsInput);

            Schedule schedule = new Schedule();
            List<EmployeeGroup> employeeGroupList = new ArrayList<>();
            List<ShiftAssignment> shiftAssignmentList = new ArrayList<>();


            // validate input employees by checking their roles
            ArrayList allRoleList = new ObjectMapper().readValue(jsonInput.get("roles").toString(), ArrayList.class);
            ArrayList<Employee> validatedEmployeesInput = new ArrayList<>();

            int employeesInputSize = employeesInput.size();
            while (employeesInputSize > 0) {
                int employeeIndex = employeesInputSize - 1;
                Employee currentEmployee = employeesInput.get(employeeIndex);

                List<String> employeeRoleList = currentEmployee.getRoleList();
                List<String> validatedEmployeeRoleList = new ArrayList<>();
                boolean contains = false;
                for (String employeeRole : employeeRoleList) {
                    if (allRoleList.contains(employeeRole)) {
                        validatedEmployeeRoleList.add(employeeRole);
                        contains = true;
                    }
                }
                if (contains) {
                    currentEmployee.setRoleList(validatedEmployeeRoleList);
                    validatedEmployeesInput.add(currentEmployee);
                }
                employeesInputSize--;
            }
            Collections.shuffle(validatedEmployeesInput);

            // create employee groups based on shifts' role requirements
            for (Shift shift : shiftsInput) {
                Map<String, Integer> requiredRoles = shift.getRequiredRoles();

                EmployeeGroup employeeGroup = new EmployeeGroup();
                Map<String, Integer> groupRoles = new HashMap<>();
                List<Employee> groupMembers = new ArrayList<>();
                String groupId = "";
                for (Employee value : validatedEmployeesInput) {
                    for (Map.Entry<String, Integer> entry : requiredRoles.entrySet()) {
                        Employee employee = value;
                        String role = entry.getKey();
                        int currentCapacity = entry.getValue();

                        if (employee.getRoleList().contains(role) && !employee.getUsed()) {
                            if (currentCapacity < 1) {
                                break;
                            }
                            groupMembers.add(employee);
                            groupRoles.merge(role, 1, Integer::sum);
                            groupId = groupId.concat(String.valueOf(employee.getId()));
                            requiredRoles.put(role, currentCapacity - 1);
                            value.setUsed(true);
                        }
                    }
                }
                employeeGroup.setEmployeeList(groupMembers);
                employeeGroup.setRoles(groupRoles);
                employeeGroup.setId(groupId);

                employeeGroupList.add(employeeGroup);
            }

            // create shift assignments based on shifts
            LocalDate currentDate = startDate;
            while (currentDate.isBefore(endDate.plusDays(1))) {
                for (Shift shift : shiftsInput) {
                    long time = LocalDateTime.of(
                            currentDate,
                            shift.getStartAt()
                        )
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli();
                    Long shiftId = shift.getId();
                    String id = shiftId.toString() + time;
                    ShiftAssignment shiftAssignment = new ShiftAssignment(id, shift, currentDate);
                    shiftAssignmentList.add(shiftAssignment);
                }
                currentDate = currentDate.plusDays(1);
            }

            schedule.setEmployeeGroupList(employeeGroupList);
            schedule.setShiftAssignmentList(shiftAssignmentList);

            return schedule;
        } catch (IOException ioe) {
            System.out.println("input data not found");
            return null;
        } catch (ParseException pe) {
            System.out.println("Invalid input data");
            return null;
        }

    }

}
