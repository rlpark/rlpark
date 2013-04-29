package rlpark.plugin.rltoys.math.averages;

import java.io.Serializable;

import rlpark.plugin.rltoys.utils.Utils;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

public class MovingAverage implements Serializable {
  private static final long serialVersionUID = -303484486232439250L;
  private final double alpha;
  private double average = 0.0;
  @Monitor
  private double d = 0.0;
  @Monitor
  protected double movingAverage = 0.0;

  public MovingAverage(int timeSteps) {
    alpha = 1.0 - Utils.timeStepsToDiscount(timeSteps);
  }

  public MovingAverage(double tau) {
    this.alpha = tau;
  }

  public double update(double value) {
    average = (1 - alpha) * average + alpha * value;
    d = (1 - alpha) * d + alpha;
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

  public double d() {
    return d;
  }

  public double alpha() {
    return alpha;
  }
}
