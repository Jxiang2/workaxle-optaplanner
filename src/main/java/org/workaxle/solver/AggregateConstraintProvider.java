package org.workaxle.solver;

import java.util.Arrays;
import java.util.stream.Stream;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.workaxle.solver.base.BaseConstraintProvider;
import org.workaxle.solver.optional.OptionalConstraintProvider;

public class AggregateConstraintProvider implements ConstraintProvider {

  BaseConstraintProvider baseConstraintProvider = new BaseConstraintProvider();

  OptionalConstraintProvider optionalConstraintProvider = new OptionalConstraintProvider();

  @Override
  public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
    return Stream.concat(
        Arrays.stream(
            baseConstraintProvider.exportConstraints(constraintFactory)),
        Arrays.stream(
            optionalConstraintProvider.exportConstraints(constraintFactory))
    ).toArray(Constraint[]::new);
  }

}
