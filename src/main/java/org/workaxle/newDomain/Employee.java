package org.workaxle.newDomain;

import lombok.Data;

import java.util.Set;

@Data
public class Employee {

    private Long id;

    private String name;

    private Set<String> roleSet;

    public Employee(Long id, String name, Set<String> roleSet) {
        this.id = id;
        this.name = name;
        this.roleSet = roleSet;
    }

    public Employee() {
    }

    @Override
    public String toString() {
        return "Employee{" +
            "id=" + id + " ; " +
            "roleList=" + roleSet +
            '}';
    }

}
