package rlpark.plugin.rltoys.algorithms.learning.control;

import java.io.Serializable;

import zephyr.plugin.core.api.monitoring.annotations.Monitor;

public class AverageReward implements Serializable {
  private static final long serialVersionUID = 8427470578663163049L;
  private final double alpha_j;
  @Monitor
  private double r;
  @Monitor
  private double j;
  @Monitor
  private double r_diff;

  public AverageReward(double alpha_j) {
    this.alpha_j = alpha_j;
  }

  public double average(double r_tp1) {
    this.r = r_tp1;
    j = (1 - alpha_j) * j + alpha_j * r;
    r_diff = r - j;
    return r_diff;
  }
}
