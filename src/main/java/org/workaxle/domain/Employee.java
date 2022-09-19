package org.workaxle.domain;

import lombok.Data;

import java.util.List;

@Data
public class Employee {

    private Long id;

    private String name;

    private List<String> roleList;

    public Employee(Long id, String name, List<String> roleList) {
        this.id = id;
        this.name = name;
        this.roleList = roleList;
    }

    public Employee() {
    }


}
