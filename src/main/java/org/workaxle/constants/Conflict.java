package org.workaxle.constants;

public enum Conflict {

    AT_LEAST_N_HOURS_BETWEEN_TWO_SHIFTS("atLeastNHoursBetweenTwoShifts", "The duration between 2 shifts must be greater than "),
    AT_MOST_ONE_SHIFT_PER_DAY("atMostOneShiftPerDay", "The number of shift an employee can have per day is 1."),
    EVENLY_SHIFT_DISTRIBUTION("evenlyShiftsDistribution", "Try to distribute shifts to every employee equally."),
    ONLY_REQUIRED_ROLES("onlyRequiredRole", "This shift can only accept employees with required " + "roles."),
    No_OVERLAPPING_SHIFTS("noOverlappingShifts", "no employee takes 2 or more shifts at the same time");

    private final String name;

    private String fullMessage;

    Conflict(String name, String fullMessage) {
        this.name = name;
        this.fullMessage = fullMessage;
    }

    public String getName() {
        return name;
    }

    public String getFullMessage() {
        return fullMessage;
    }

}
