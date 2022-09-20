package org.workaxle.newDomain;

import lombok.Data;

import java.util.Set;

@Data
public class Employee {

    private Long id;

    private String name;

    private Set<String> roleSet;

    public Employee(Long id, String name, Set<String> roleList) {
        this.id = id;
        this.name = name;
        this.roleSet = roleList;
    }

    public Employee() {
    }

    @Override
    public String toString() {
        return "Employee{" +
            "roleList=" + roleSet +
            '}';
    }

}
