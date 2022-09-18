package org.workaxle.domain;

import lombok.Data;

import java.time.LocalTime;

@Data
public class Shift {

    Long id;

    String name;

    LocalTime startAt;

    LocalTime endAt;

}
