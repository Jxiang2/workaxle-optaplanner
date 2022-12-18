package org.workaxle.solver;

import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.workaxle.solver.base.workaxle.BaseConstraintProvider;
import org.workaxle.solver.optional.workaxle.OptionalConstraintProvider;

import java.util.Arrays;
import java.util.stream.Stream;

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
