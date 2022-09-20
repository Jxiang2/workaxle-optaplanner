package org.workaxle.domain;

import lombok.Data;

import java.util.List;

@Data
public class Employee {

    private Long id;

    private String name;

    private List<String> roleList;

    private Boolean used = false;

    public Employee(Long id, String name, List<String> roleList) {
        this.id = id;
        this.name = name;
        this.roleList = roleList;
    }

    public Employee() {
    }

    @Override
    public String toString() {
        return "Employee{" +
            "roleList=" + roleList +
            '}';
    }


}
