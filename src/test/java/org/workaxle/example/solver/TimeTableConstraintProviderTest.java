package org.workaxle.example.solver;

import org.junit.jupiter.api.Test;
import org.optaplanner.test.api.score.stream.ConstraintVerifier;
import org.workaxle.example.domain.Lesson;
import org.workaxle.example.domain.Room;
import org.workaxle.example.domain.TimeTable;
import org.workaxle.example.domain.Timeslot;

import java.time.DayOfWeek;
import java.time.LocalTime;

public class TimeTableConstraintProviderTest {

    private static final Room ROOM1 = new Room("Room1");

    private static final Timeslot TIMESLOT1 = new Timeslot(DayOfWeek.MONDAY, LocalTime.NOON, LocalTime.MIDNIGHT);

    private static final Timeslot TIMESLOT2 = new Timeslot(DayOfWeek.TUESDAY, LocalTime.NOON, LocalTime.MIDNIGHT);

    ConstraintVerifier<TimeTableConstraintProvider, TimeTable> constraintVerifier = ConstraintVerifier.build(
        new TimeTableConstraintProvider(), TimeTable.class, Lesson.class);

    @Test
    void roomConflict() {
        Lesson firstLesson = new Lesson(
            1L, "Subject1", "Teacher1", "Group1", TIMESLOT1, ROOM1
        );
        Lesson conflictingLesson = new Lesson(
            2L, "Subject2", "Teacher2", "Group2", TIMESLOT1, ROOM1
        );
        Lesson nonConflictingLesson = new Lesson(
            3L, "Subject3", "Teacher3", "Group3", TIMESLOT2, ROOM1
        );

        constraintVerifier
            .verifyThat(TimeTableConstraintProvider::roomConflict)
            .given(firstLesson, conflictingLesson, nonConflictingLesson)
            .penalizesBy(1);
    }

}
