package org.workaxle.domain;

import java.time.LocalTime;
import java.util.Objects;

public class Shift {

    Long id;

    String name;

    LocalTime startAt;

    LocalTime endAt;

    public Shift(Long id, String name, LocalTime startAt, LocalTime endAt) {
        this.id = id;
        this.name = name;
        this.startAt = startAt;
        this.endAt = endAt;
    }

    public Shift() {
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final Object $name = this.getName();
        result = result * PRIME + ($name == null ? 43 : $name.hashCode());
        final Object $startAt = this.getStartAt();
        result = result * PRIME + ($startAt == null ? 43 : $startAt.hashCode());
        final Object $endAt = this.getEndAt();
        result = result * PRIME + ($endAt == null ? 43 : $endAt.hashCode());
        return result;
    }

    public boolean equals(final Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Shift))
            return false;
        final Shift other = (Shift) o;
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
        final Object this$startAt = this.getStartAt();
        final Object other$startAt = other.getStartAt();
        if (!Objects.equals(this$startAt, other$startAt))
            return false;
        final Object this$endAt = this.getEndAt();
        final Object other$endAt = other.getEndAt();
        return Objects.equals(this$endAt, other$endAt);
    }

    protected boolean canEqual(final Object other) {
        return other instanceof Shift;
    }

    public String toString() {
        return "Shift(id=" + this.getId() + ", name=" + this.getName() + ", startAt=" + this.getStartAt() + ", endAt=" + this.getEndAt() + ")";
    }

    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public LocalTime getStartAt() {
        return this.startAt;
    }

    public LocalTime getEndAt() {
        return this.endAt;
    }

    public void setEndAt(LocalTime endAt) {
        this.endAt = endAt;
    }

    public void setStartAt(LocalTime startAt) {
        this.startAt = startAt;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
