package rlpark.plugin.rltoys.math.vector.filters;

import rlpark.plugin.rltoys.math.vector.MutableVector;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.math.vector.SparseVector;
import rlpark.plugin.rltoys.math.vector.implementations.PVector;

public class Filters {
  interface FilteredOperation {
    MutableVector operate();

    MutableVector sparseOperate(int[] indexes, int nbActive);
  }

  private static MutableVector operate(FilteredOperation operation, RealVector filter) {
    if (filter instanceof SparseVector) {
      SparseVector sfilter = (SparseVector) filter;
      return operation.sparseOperate(sfilter.nonZeroIndexes(), sfilter.nonZeroElements());
    }
    return operation.operate();
  }

  public static MutableVector minToSelf(final MutableVector result, final MutableVector other, RealVector filter) {
    FilteredOperation minOperation = new FilteredOperation() {
      @Override
      public MutableVector sparseOperate(int[] indexes, int nbActive) {
        for (int i = 0; i < nbActive; i++) {
          int index = indexes[i];
          result.setEntry(index, Math.min(result.getEntry(index), other.getEntry(index)));
        }
        return null;
      }

      @Override
      public MutableVector operate() {
        int dimension = result.getDimension();
        for (int index = 0; index < dimension; index++)
          result.setEntry(index, Math.min(result.getEntry(index), other.getEntry(index)));
        return result;
      }
    };
    return operate(minOperation, filter);
  }

  public static MutableVector mapMultiplyToSelf(final PVector result, final double d, RealVector filter) {
    FilteredOperation mapMultiplySelfOperation = new FilteredOperation() {
      @Override
      public MutableVector sparseOperate(int[] indexes, int nbActive) {
        for (int i = 0; i < nbActive; i++)
          result.data[indexes[i]] *= d;
        return result;
      }

      @Override
      public MutableVector operate() {
        return result.mapMultiplyToSelf(d);
      }
    };
    return operate(mapMultiplySelfOperation, filter);
  }

  public static MutableVector mapDivideToSelf(final PVector result, final double d, RealVector filter) {
    FilteredOperation mapDivideSelfOperation = new FilteredOperation() {
      @Override
      public MutableVector sparseOperate(int[] indexes, int nbActive) {
        for (int i = 0; i < nbActive; i++)
          result.data[indexes[i]] /= d;
        return result;
      }

      @Override
      public MutableVector operate() {
        return result.mapMultiplyToSelf(1 / d);
      }
    };
    return operate(mapDivideSelfOperation, filter);
  }
}
