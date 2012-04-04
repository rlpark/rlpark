package rlpark.plugin.rltoys.algorithms.representations.ltu.units;

import zephyr.plugin.core.api.monitoring.annotations.Monitor;

public class LTUAdaptive extends LTUThreshold {
  private static final long serialVersionUID = 7927007001800685788L;
  public final double minFrequency;
  public final double maxFrequency;
  private final double frequencyLatencySpeed;
  @Monitor
  private double averageFrequency;
  @Monitor
  private final double thresholdStepSize;


  public LTUAdaptive(double minFrequency, double maxFrequency, double frequencyLatencySpeed, double thresholdStepSize) {
    assert minFrequency >= 0.0 && maxFrequency > minFrequency;
    this.minFrequency = minFrequency;
    this.maxFrequency = maxFrequency;
    this.thresholdStepSize = thresholdStepSize;
    this.averageFrequency = (minFrequency + maxFrequency) / 2.0;
    this.frequencyLatencySpeed = frequencyLatencySpeed;
  }

  public LTUAdaptive(int index, int[] inputs, byte[] weights, double minFrequency, double maxFrequency,
      double frequencyLatencySpeed, double thresholdStepSize) {
    super(index, inputs, weights);
    assert minFrequency >= 0.0 && maxFrequency > minFrequency;
    this.minFrequency = minFrequency;
    this.maxFrequency = maxFrequency;
    this.frequencyLatencySpeed = frequencyLatencySpeed;
    this.thresholdStepSize = thresholdStepSize;
  }

  @Override
  public boolean updateActivation() {
    boolean isActive = super.updateActivation();
    averageFrequency *= frequencyLatencySpeed;
    if (isActive)
      averageFrequency += 1 - frequencyLatencySpeed;
    if (averageFrequency > maxFrequency)
      threshold += thresholdStepSize;
    else if (averageFrequency < minFrequency)
      threshold -= thresholdStepSize;
    return isActive;
  }

  @Override
  public LTUAdaptive newLTU(int index, int[] inputs, byte[] weights) {
    return new LTUAdaptive(index, inputs, weights, minFrequency, maxFrequency, frequencyLatencySpeed, thresholdStepSize);
  }
}
