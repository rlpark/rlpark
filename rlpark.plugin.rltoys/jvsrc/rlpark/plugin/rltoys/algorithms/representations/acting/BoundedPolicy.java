package rlpark.plugin.rltoys.algorithms.representations.acting;

import rlpark.plugin.rltoys.math.ranges.Range;

public interface BoundedPolicy extends Policy {
  Range range();
}
