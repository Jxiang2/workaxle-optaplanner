package org.workaxle.example.solver;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.Joiners;
import org.workaxle.example.domain.Lesson;

public class TimeTableConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[]{
            roomConflict(constraintFactory),
            teacherConflict(constraintFactory),
            studentGroupConflict(constraintFactory),
        };
    }

    public Constraint roomConflict(ConstraintFactory constraintFactory) {
        // assign a lesson for each room
        // a room can accommodate at most one lesson at the same time.

        return constraintFactory
            // select a lesson
            .forEach(Lesson.class)
            // and pair it with another lesson
            .join(
                Lesson.class,
                // in the same timeslot
                Joiners.equal(Lesson::getTimeslot),
                // in the same room
                Joiners.equal(Lesson::getRoom),
                // and the pair is unique (different id, no reverse pairs)
                Joiners.lessThan(Lesson::getId)
            )
            // ... then penalize each pair with a hard weight
            .penalize("Room conflict", HardSoftScore.ONE_HARD);
    }

    public Constraint teacherConflict(ConstraintFactory constraintFactory) {
        // assign a lesson to each teacher
        // a teacher can teach at most one lesson at the same time.

        return constraintFactory
            .forEach(Lesson.class)
            .join(
                Lesson.class,
                Joiners.equal(Lesson::getTimeslot),
                Joiners.equal(Lesson::getTeacher),
                Joiners.lessThan(Lesson::getId)
            )
            .penalize("Teacher conflict", HardSoftScore.ONE_HARD);
    }

    public Constraint studentGroupConflict(ConstraintFactory constraintFactory) {
        // assign a lesson to each student group
        // a student group can attend at most one lesson at the same time.

        return constraintFactory
            .forEach(Lesson.class)
            .join(Lesson.class,
                Joiners.equal(Lesson::getTimeslot),
                Joiners.equal(Lesson::getStudentGroup),
                Joiners.lessThan(Lesson::getId))
            .penalize("Student group conflict", HardSoftScore.ONE_HARD);
    }

}
