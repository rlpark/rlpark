package rlpark.plugin.rltoys.math.averages;

import java.io.Serializable;

public interface MeanVar extends Serializable {
  void update(double newValue);

  MeanVar newInstance();

  double mean();

  double var();
}
