package org.workaxle.domain;

import java.util.Objects;

public class Employee {

    Long id;

    String name;

    String role;

    public Employee(Long id, String name, String role) {
        this.id = id;
        this.name = name;
        this.role = role;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final Object $name = this.getName();
        result = result * PRIME + ($name == null ? 43 : $name.hashCode());
        final Object $role = this.getRole();
        result = result * PRIME + ($role == null ? 43 : $role.hashCode());
        return result;
    }

    public boolean equals(final Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Employee))
            return false;
        final Employee other = (Employee) o;
        if (!other.canEqual((Object) this))
            return false;
        final Object this$id = this.getId();
        final Object other$id = other.getId();
        if (!Objects.equals(this$id, other$id))
            return false;
        final Object this$name = this.getName();
        final Object other$name = other.getName();
        if (!Objects.equals(this$name, other$name))
            return false;
        final Object this$role = this.getRole();
        final Object other$role = other.getRole();
        return Objects.equals(this$role, other$role);
    }

    protected boolean canEqual(final Object other) {
        return other instanceof Employee;
    }

    public String toString() {
        return "Employee(id=" + this.getId() + ", name=" + this.getName() + ", role=" + this.getRole() + ")";
    }

    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getRole() {
        return this.role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
