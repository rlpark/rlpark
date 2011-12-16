package rltoys.algorithms.representations.discretizer.partitions;


public class BoundedPartition extends WrappedPartition {
  private static final long serialVersionUID = 3610023628628518904L;

  public BoundedPartition(double min, double max, int resolution) {
    super(min, max, resolution);
  }

  @Override
  public int discretize(double input) {
    double boundedInput = Math.min(Math.max(input, min()), max());
    return super.discretize(boundedInput);
  }
}
