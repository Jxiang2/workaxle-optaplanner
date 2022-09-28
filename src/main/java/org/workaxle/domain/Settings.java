package org.workaxle.domain;

import lombok.Data;

@Data
public class Settings {

    private int maxHours;

    private int shiftsBetween = 0;

    private boolean weekendShifts = false;

}
