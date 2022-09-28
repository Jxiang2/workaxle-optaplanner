package org.workaxle.domain;

import lombok.Data;

@Data
public class Settings {

    private Integer maxHours;

    private Integer shiftsBetween;

    private Boolean weekendShifts = false;

}
