package rlpark.plugin.rltoys.math.normalization;

import java.io.Serializable;

public interface Normalizer extends Serializable {
  void update(double newValue);

  double normalize(double value);

  Normalizer newInstance();
}
