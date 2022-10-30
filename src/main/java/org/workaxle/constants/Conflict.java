package org.workaxle.constants;

public enum Conflict {

  AT_LEAST_N_HOURS_BETWEEN_TWO_SHIFTS("atLeastNHoursBetweenTwoShifts",
      "The duration between 2 shifts must be greater than "),
  AT_MOST_ONE_SHIFT_PER_DAY("atMostOneShiftPerDay",
      "The number of shift an employee can have per day is 1."),
  EVENLY_SHIFT_DISTRIBUTION("evenlyShiftsDistribution",
      "Try to distribute shifts to every employee equally."),
  ONLY_REQUIRED_ROLES("onlyRequiredRole",
      "The Employee must have the required role to take this shift"),
  NO_OVERLAPPING_SHIFTS("noOverlappingShifts",
      "no employee takes 2 or more shifts at the same time"),
  NO_SHIFT_ON_WEEKENDS("noShiftOnWeekends",
      "no shift can be scheduled on weekends"),
  AT_MOST_N_HOURS("atMostNHours",
      "no employee works more than n hours per period");

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
