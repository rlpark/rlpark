package rltoys.environments.continuousgridworld;

import rltoys.math.ranges.Range;

public interface ContinuousFunction {
  double fun(double[] position);

  int nbDimension();

  Range range();
}
