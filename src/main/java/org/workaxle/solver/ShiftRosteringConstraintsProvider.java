package org.workaxle.solver;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.Joiners;
import org.workaxle.domain.ShiftAssignment;

public class ShiftRosteringConstraintsProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[]{
            employeeGroupConflict(constraintFactory),
        };
    }

    public Constraint employeeGroupConflict(ConstraintFactory constraintFactory) {
        // assign an employee group to each shiftAssignment
        // an employee group can be assigned to at most one shiftAssignment at the same time

        return constraintFactory
            // select a shiftAssignment
            .forEach(ShiftAssignment.class)
            // and pair it with another shiftAssignment
            .join(
                ShiftAssignment.class,
                Joiners.equal(ShiftAssignment::getShift), // from the same shift
                Joiners.equal(ShiftAssignment::getEmployeeGroup), // use the same employee group
                Joiners.lessThan(ShiftAssignment::getId) // pair uniquely
            )
            // then penalize each pair with a hard weight
            .penalize("Employee group conflict", HardSoftScore.ONE_HARD);
    }

}
