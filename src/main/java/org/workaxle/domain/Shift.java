package org.workaxle.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalTime;

@Data
@AllArgsConstructor
public class Shift {

    Long id;

    String name;

    LocalTime startAt;

    LocalTime endAt;

}
