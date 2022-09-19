package org.workaxle.domain;

import lombok.Data;

import java.time.LocalTime;
import java.util.Map;

@Data
public class Shift {

    private Long id;

    private String name;

    private LocalTime startAt;

    private LocalTime endAt;

    private Map<String, Integer> requiredRoles;

    public Shift(Long id, String name, LocalTime startAt, LocalTime endAt, Map<String, Integer> requiredRoles) {
        this.id = id;
        this.name = name;
        this.startAt = startAt;
        this.endAt = endAt;
        this.requiredRoles = requiredRoles;
    }

    public Shift() {
    }

}
