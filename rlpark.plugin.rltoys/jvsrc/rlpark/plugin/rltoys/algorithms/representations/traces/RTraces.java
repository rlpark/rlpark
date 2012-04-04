package rlpark.plugin.rltoys.algorithms.representations.traces;

import java.util.Arrays;

import rlpark.plugin.rltoys.math.vector.BinaryVector;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.math.vector.implementations.SVector;


/**
 * Replacing traces for binary vectors
 */
public class RTraces extends ATraces {
  private static final long serialVersionUID = -324210619484987917L;

  public RTraces() {
    this(-1, 0);
  }

  public RTraces(int targetSize, double targetTolerance) {
    this(0, targetSize, targetTolerance);
  }

  public RTraces(int size, int targetSize, double targetTolerance) {
    super(size, targetSize, targetTolerance, new SVector(0));
  }

  @Override
  public RTraces newTraces(int size) {
    return new RTraces(size, targetSize, targetTolerance);
  }

  @Override
  protected void addToSelf(RealVector phi) {
    SVector svector = (SVector) vector;
    int[] thisIndexes = Arrays.copyOf(svector.indexes, svector.indexes.length);
    double[] thisValues = Arrays.copyOf(svector.values, svector.values.length);
    int[] otherIndexes = ((BinaryVector) phi).activeIndexes();
    int thisNbActive = svector.nonZeroElements();
    int i = 0, j = 0;
    svector.clear();
    while (i < thisNbActive || j < otherIndexes.length) {
      if (j < otherIndexes.length && (i == thisNbActive || thisIndexes[i] > otherIndexes[j])) {
        svector.insertElementAtPosition(svector.nonZeroElements(), otherIndexes[j], 1.0);
        j++;
      } else if (j == otherIndexes.length || thisIndexes[i] < otherIndexes[j]) {
        svector.insertElementAtPosition(svector.nonZeroElements(), thisIndexes[i], thisValues[i]);
        i++;
      } else {
        svector.insertElementAtPosition(svector.nonZeroElements(), thisIndexes[i], 1.0);
        i++;
        j++;
      }
    }
  }
}
