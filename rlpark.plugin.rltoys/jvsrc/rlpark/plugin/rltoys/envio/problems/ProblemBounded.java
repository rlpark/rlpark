package rlpark.plugin.rltoys.envio.problems;

import rlpark.plugin.rltoys.math.ranges.Range;

public interface ProblemBounded extends RLProblem {
  Range[] getObservationRanges();
}
