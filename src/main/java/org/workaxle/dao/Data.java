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
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Data {

    public static void main(String[] args) throws IOException, ParseException {
        getDate();
    }

    public static void getDate() throws IOException, ParseException {
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
            HashMap requiredRoles = new ObjectMapper()
                .readValue(requiredRolesJson.toJSONString(), HashMap.class);
            shift.setId((Long) shiftJson.get("id"));
            shift.setName((String) shiftJson.get("name"));
            shift.setStartAt(LocalTime.parse((String) shiftJson.get("startAt")));
            shift.setEndAt(LocalTime.parse((String) shiftJson.get("endAt")));
            shift.setRequiredRoles((Map<String, Integer>) requiredRoles);

            shiftsInput.add(shift);
        }

        Schedule schedule = new Schedule();
        List<EmployeeGroup> employeeGroupList = new ArrayList<>();
        List<ShiftAssignment> shiftAssignmentList = new ArrayList<>();

        
    }

}
