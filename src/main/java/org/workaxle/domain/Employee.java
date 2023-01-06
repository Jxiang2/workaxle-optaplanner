package org.workaxle.domain;

import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class Employee {

  private Long id;

  private String name;

  private Set<String> roleSet;

  public Employee() {
  }

  public Employee(Long id, String name, Set<String> roleSet) {
    this.id = id;
    this.name = name;
    this.roleSet = roleSet;
  }

  @Override
  public String toString() {
    return "Employee{" +
        "id=" + id + " ; " +
        "roleSet=" + roleSet +
        '}';
  }

}
