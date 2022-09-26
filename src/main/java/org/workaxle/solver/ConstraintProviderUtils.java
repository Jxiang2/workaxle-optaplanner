package org.workaxle.solver;

import org.workaxle.domain.ShiftAssignment;

import java.time.Duration;
import java.time.LocalDateTime;

public class ConstraintProviderUtils {

    public static int getHourlyOverlap(ShiftAssignment first, ShiftAssignment second) {
        final LocalDateTime shift1Start = first.getShift().getStartAt();
        final LocalDateTime shift1End = first.getShift().getEndAt();
        final LocalDateTime shift2Start = second.getShift().getStartAt();
        final LocalDateTime shift2End = second.getShift().getEndAt();

        final long minutes = Duration.between(
            (shift1Start.compareTo(shift2Start) > 0) ? shift1Start : shift2Start,
            (shift1End.compareTo(shift2End) < 0) ? shift1End : shift2End
        ).toMinutes();

        return convertMinutesToHours(minutes);
    }

    public static int convertMinutesToHours(long minutes) {
        if (minutes > 0l && minutes < 60l) {
            return 1;
        }
        return (int) (minutes / 60l);
    }

}
