package rlpark.plugin.rltoys.math.normalization;

import rlpark.plugin.rltoys.math.ranges.Range;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

public class MinMaxNormalizer implements Normalizer {
  private static final long serialVersionUID = 4495161964136798707L;
  public final static double MIN = -1;
  public final static double MAX = 1;
  private int nbUpdate = 0;
  private final Range targetRange;
  @Monitor
  private final Range valueRange = new Range();

  public MinMaxNormalizer() {
    this(new Range(MIN, MAX));
  }

  public MinMaxNormalizer(Range range) {
    this.targetRange = range;
  }

  @Override
  public double normalize(double x) {
    if (valueRange.length() == 0 || nbUpdate == 0)
      return 0;
    double scaled = Math.max(0.0, Math.min(1.0, (x - valueRange.min()) / valueRange.length()));
    return scaled * targetRange.length() + targetRange.min();
  }

  public float normalize(float x) {
    return (float) normalize((double) x);
  }

  @Override
  public void update(double x) {
    valueRange.update(x);
    nbUpdate++;
  }

  @Override
  public MinMaxNormalizer newInstance() {
    return new MinMaxNormalizer(targetRange);
  }

  public void reset() {
    valueRange.reset();
    nbUpdate = 0;
  }

  public Range targetRange() {
    return targetRange;
  }

  public Range valueRange() {
    return valueRange;
  }
}
