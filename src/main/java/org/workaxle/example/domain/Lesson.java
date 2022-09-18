package org.workaxle.example.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

/**
 * Lesson: a teacher teaches a subject to a group of students
 * <p>
 * Note: If a subject is taught multiple times per week by the same teacher
 * to the same student group, there are multiple Lesson instances that
 * are only distinguishable by id
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@PlanningEntity
public class Lesson {

    @PlanningId
    private Long id;

    private String subject;

    private String teacher;

    private String studentGroup;

    @PlanningVariable(valueRangeProviderRefs = "timeslotRange")
    private Timeslot timeslot;

    @PlanningVariable(valueRangeProviderRefs = "roomRange")
    private Room room;

    public Lesson(Long id, String subject, String teacher, String studentGroup) {
        this.id = id;
        this.subject = subject;
        this.teacher = teacher;
        this.studentGroup = studentGroup;
    }

    @Override
    public String toString() {
        return subject + "(" + id + ")";
    }

}
