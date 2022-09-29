package org.workaxle.domain;

import lombok.Data;

@Data
public class Settings {

    private Integer maxHoursOfWork;

    private Integer hoursBetweenShifts;

    private Boolean allowWeekendShifts = false;

}
