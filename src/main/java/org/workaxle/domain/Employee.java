package org.workaxle.domain;

import lombok.Data;
import org.optaplanner.core.api.domain.lookup.PlanningId;

import java.util.List;

@Data
public class Employee {

    @PlanningId
    private Long id;

    private String name;

    private List<String> roles;

    public Employee(Long id, String name, List<String> roles) {
        this.id = id;
        this.name = name;
        this.roles = roles;
    }

    public Employee() {
    }

}
