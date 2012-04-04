package rlpark.plugin.rltoys.math;

import java.io.Serializable;

import rlpark.plugin.rltoys.utils.Utils;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

public class MovingAverage implements Serializable {
  private static final long serialVersionUID = -303484486232439250L;
  private final double tau;
  private double average = 0.0;
  private double d = 0.0;
  @Monitor(emptyLabel = true)
  protected double movingAverage = 0.0;

  public MovingAverage(int timeSteps) {
    tau = 1.0 - Utils.timeStepsToDiscount(timeSteps);
  }

  public MovingAverage(double tau) {
    this.tau = tau;
  }

  public double update(double value) {
    average = (1 - tau) * average + tau * value;
    d = (1 - tau) * d + tau;
    movingAverage = average / d;
    return value;
  }

  public double average() {
    return movingAverage;
  }

  public void reset() {
    average = 0.0;
    d = 0.0;
    movingAverage = 0.0;
  }
}
