package rlpark.plugin.rltoys.envio.policy;

import rlpark.plugin.rltoys.math.ranges.Range;

public interface BoundedPolicy extends Policy {
  Range range();
}
