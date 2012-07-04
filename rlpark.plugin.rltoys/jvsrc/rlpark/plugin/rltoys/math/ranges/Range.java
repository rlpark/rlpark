/**
 * 
 */
package rlpark.plugin.rltoys.math.ranges;

import java.io.Serializable;
import java.util.Random;

import zephyr.plugin.core.api.monitoring.annotations.Monitor;

@Monitor
public class Range implements Serializable {
  private static final long serialVersionUID = 3076267038250925863L;
  private double min;
  private double max;

  public Range() {
    this(Double.MAX_VALUE, -Double.MAX_VALUE);
  }


  public Range(Range range) {
    this(range.min, range.max);
  }

  public Range(double min, double max) {
    this.min = min;
    this.max = max;
  }

  public double bound(double value) {
    return Math.max(min, Math.min(max, value));
  }

  public double choose(Random random) {
    return random.nextFloat() * length() + min;
  }

  public boolean in(double value) {
    return value >= min && value <= max;
  }

  @Override
  public boolean equals(Object object) {
    if (super.equals(object))
      return true;
    Range other = (Range) object;
    return min == other.min && max == other.max;
  }

  @Override
  public int hashCode() {
    return (int) (min + max);
  }

  public double length() {
    return max - min;
  }

  public double[] sample(final int nbValue) {
    double[] values = new double[nbValue];
    double sampleSize = (max - min) / nbValue;
    for (int i = 0; i < values.length; i++)
      values[i] = i * sampleSize + min;
    return values;
  }

  public double center() {
    return min + (length() / 2);
  }

  @Override
  public String toString() {
    return String.format("[%f,%f]", min, max);
  }

  public void update(double value) {
    min = Math.min(value, min);
    max = Math.max(value, max);
  }

  public double min() {
    return min;
  }

  public double max() {
    return max;
  }

  public void reset() {
    min = Double.MAX_VALUE;
    max = -Double.MAX_VALUE;
  }
}