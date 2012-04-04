package rlpark.plugin.rltoys.algorithms.learning.predictions;

import rlpark.plugin.rltoys.envio.observations.TRStep;
import rlpark.plugin.rltoys.envio.problems.ProblemBounded;
import rlpark.plugin.rltoys.envio.states.Projector;
import rlpark.plugin.rltoys.math.ranges.Range;
import zephyr.plugin.core.api.viewable.ContinuousFunction;

public class ValueFunction2D implements ContinuousFunction {
  private final Projector projector;
  private final Predictor predictor;
  private final ProblemBounded problem;

  public ValueFunction2D(Projector projector, ProblemBounded problem, Predictor predictor) {
    this.projector = projector;
    this.problem = problem;
    assert problem.getObservationRanges().length == 2;
    this.predictor = predictor;
  }

  @Override
  public double value(double[] obs) {
    if (obs == null)
      return 0.0;
    return predictor.predict(projector.project(obs));
  }

  public Range[] ranges() {
    return problem.getObservationRanges();
  }

  public double[] position() {
    TRStep step = problem.lastStep();
    if (step == null)
      return null;
    return step.o_tp1;
  }
}
