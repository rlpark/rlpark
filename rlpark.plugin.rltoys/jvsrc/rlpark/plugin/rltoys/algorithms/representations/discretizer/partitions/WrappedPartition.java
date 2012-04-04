package rlpark.plugin.rltoys.algorithms.representations.discretizer.partitions;


public class WrappedPartition extends AbstractPartition {
  private static final long serialVersionUID = 3610023628628518904L;

  public WrappedPartition(double min, double max, int resolution) {
    super(min, max, resolution);
  }

  @Override
  public int discretize(double input) {
    double n = (input - min()) / partLength;
    if (n < 0)
      n += ((int) (-n / resolution) + 1) * resolution;
    return (int) (n % resolution);
  }
}
