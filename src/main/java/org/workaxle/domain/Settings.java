package org.workaxle.domain;

import lombok.Data;

@Data
public class Settings {

    private int hoursBetweenShifts = 0;

    private boolean weekendShifts = false;

}
