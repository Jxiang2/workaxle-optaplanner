package org.workaxle.domain;

import lombok.Data;

@Data
public class Employee {

    private Long id;

    private String name;

    private String role;

    public Employee(Long id, String name, String role) {
        this.id = id;
        this.name = name;
        this.role = role;
    }

    public Employee() {
    }

}
