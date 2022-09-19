package org.workaxle.domain;

import lombok.Data;

import java.time.LocalTime;
import java.util.List;

@Data
public class Shift {

    private Long id;

    private String name;

    private LocalTime startAt;

    private LocalTime endAt;

    private List<String> requiredRoles;

    public Shift(Long id, String name, LocalTime startAt, LocalTime endAt, List<String> requiredRoles) {
        this.id = id;
        this.name = name;
        this.startAt = startAt;
        this.endAt = endAt;
        this.requiredRoles = requiredRoles;
    }

    public Shift() {
    }

}
