package rlpark.plugin.rltoys.math.normalization;

import zephyr.plugin.core.api.monitoring.annotations.Monitor;

public class IncMeanVarNormalizer implements Normalizer, MeanVar {
  private static final long serialVersionUID = -7117059874975759612L;
  private final int minNbUpdate;
  @Monitor
  private double mean = 0.0;
  @Monitor
  private double var = 1.0;
  private int n = 0;
  private double m2 = 0.0;

  public IncMeanVarNormalizer() {
    this(5);
  }

  public IncMeanVarNormalizer(int minNbUpdate) {
    this.minNbUpdate = minNbUpdate;
  }

  @Override
  final public double normalize(double x) {
    if (n < minNbUpdate)
      return 0.0;
    if (var == 0.0)
      return 0.0;
    return (x - mean) / Math.sqrt(var);
  }

  // http://en.wikipedia.org/wiki/Algorithms_for_calculating_variance#On-line_algorithm
  @Override
  public void update(double x) {
    n++;
    double delta = x - mean;
    mean = mean + delta / n;
    m2 = m2 + delta * (x - mean);
    if (n > 1)
      var = variance(1);
  }

  @Override
  public double mean() {
    return mean;
  }

  @Override
  public double var() {
    return variance(1);
  }

  public int n() {
    return n;
  }

  public double variance(int ddf) {
    assert ddf >= 0;
    return m2 / (n - ddf);
  }

  @Override
  public IncMeanVarNormalizer newInstance() {
    return new IncMeanVarNormalizer();
  }

  public double stdError() {
    return Math.sqrt(variance(1));
  }
}
