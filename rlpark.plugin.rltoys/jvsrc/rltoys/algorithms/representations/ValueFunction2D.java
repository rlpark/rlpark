package rltoys.algorithms.representations;

import rltoys.algorithms.learning.predictions.Predictor;
import rltoys.math.ranges.Range;
import zephyr.plugin.core.api.viewable.ContinuousFunction;

public class ValueFunction2D implements ContinuousFunction {
  private final Range[] ranges;
  private final Projector projector;
  private final Predictor predictor;

  public ValueFunction2D(Projector projector, Range[] ranges, Predictor predictor) {
    assert ranges.length == 2;
    this.projector = projector;
    this.ranges = ranges;
    this.predictor = predictor;
  }

  @Override
  public double value(double[] obs) {
    if (obs == null)
      return 0.0;
    return predictor.predict(projector.project(obs));
  }

  public Range[] ranges() {
    return ranges;
  }
}
