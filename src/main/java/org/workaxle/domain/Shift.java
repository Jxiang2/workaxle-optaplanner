package org.workaxle.domain;

import java.time.LocalDateTime;
import java.util.Map;
import lombok.Data;

@Data
public class Shift {

  private Long id;

  private String name;

  private LocalDateTime startAt;

  private LocalDateTime endAt;

  private Map<String, Integer> requiredRoles;

  public Shift(
      Long id,
      String name,
      LocalDateTime startAt,
      LocalDateTime endAt,
      Map<String, Integer> requiredRoles
  ) {
    this.id = id;
    this.name = name;
    this.startAt = startAt;
    this.endAt = endAt;
    this.requiredRoles = requiredRoles;
  }

  public Shift() {
  }

}