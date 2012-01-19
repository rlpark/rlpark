package rltoys.algorithms.representations.acting;

import rltoys.math.ranges.Range;

public interface BoundedPolicy extends Policy {
  Range range();
}
