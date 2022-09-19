package org.workaxle.domain;

import lombok.Data;
import org.optaplanner.core.api.domain.lookup.PlanningId;

import java.util.List;
import java.util.Map;

@Data
public class EmployeeGroup {

    @PlanningId
    private String id;

    private Map<String, Integer> roles;

    private List<Employee> employeeList;

    public EmployeeGroup(String id, Map<String, Integer> roles) {
        this.id = id;
        this.roles = roles;
    }

    public EmployeeGroup() {
    }

}
