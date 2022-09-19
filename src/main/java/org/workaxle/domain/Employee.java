package org.workaxle.domain;

import lombok.Data;
import org.optaplanner.core.api.domain.lookup.PlanningId;

import java.util.Map;

@Data
public class Employee {

    @PlanningId
    private Long id;

    private String name;

    private Map<String, Integer> roles;

    public Employee(Long id, String name, Map<String, Integer> roles) {
        this.id = id;
        this.name = name;
        this.roles = roles;
    }

    public Employee() {
    }

}
