package rlpark.plugin.rltoys.algorithms.representations.discretizer.partitions;

public class BoundedPartition extends AbstractPartition {


  /**
   * 
   */
  private static final long serialVersionUID = 1230641720768025896L;

  public BoundedPartition(double min, double max, int resolution) {
    super(min, max, resolution);
  }

  @Override
  public int discretize(double input) {
    double margin = intervalWidth * .0001;
    double boundedInput = Math.min(Math.max(input, min + margin), max - margin);
    int result = (int) ((boundedInput - min) / intervalWidth);
    assert result >= 0 && result < resolution;
    return result;
  }

}
