package rlpark.plugin.rltoys.agents.representations;

import rlpark.plugin.rltoys.algorithms.functions.states.Projector;
import rlpark.plugin.rltoys.math.ranges.Range;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.math.vector.implementations.PVector;

public class IdentityProjector implements Projector {
  private static final long serialVersionUID = 4540220410020682331L;
  private final int vectorSize;
  private final double vectorNorm;

  public IdentityProjector(Range[] observationRanges) {
    vectorSize = observationRanges.length;
    double norm = 0;
    for (Range range : observationRanges) {
      double maxValue = Math.max(Math.abs(range.min()), Math.abs(range.max()));
      norm += maxValue * maxValue;
    }
    vectorNorm = Math.sqrt(norm);
  }

  @Override
  public RealVector project(double[] obs) {
    return new PVector(obs);
  }

  @Override
  public int vectorSize() {
    return vectorSize;
  }

  @Override
  public double vectorNorm() {
    return vectorNorm;
  }
}
