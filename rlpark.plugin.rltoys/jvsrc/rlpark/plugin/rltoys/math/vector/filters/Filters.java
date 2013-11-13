package rlpark.plugin.rltoys.math.vector.filters;

import rlpark.plugin.rltoys.math.vector.MutableVector;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.math.vector.SparseVector;
import rlpark.plugin.rltoys.math.vector.implementations.PVector;
import rlpark.plugin.rltoys.utils.NotImplemented;

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

  public static MutableVector minToSelf(final MutableVector result, final RealVector other, RealVector filter) {
    FilteredOperation minOperation = new FilteredOperation() {
      @Override
      public MutableVector sparseOperate(int[] indexes, int nbActive) {
        for (int i = 0; i < nbActive; i++) {
          int index = indexes[i];
          result.setEntry(index, Math.min(result.getEntry(index), other.getEntry(index)));
        }
        return result;
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

  public static MutableVector maxToSelf(final MutableVector result, final RealVector other, RealVector filter) {
    FilteredOperation maxOperation = new FilteredOperation() {
      @Override
      public MutableVector sparseOperate(int[] indexes, int nbActive) {
        for (int i = 0; i < nbActive; i++) {
          int index = indexes[i];
          result.setEntry(index, Math.max(result.getEntry(index), other.getEntry(index)));
        }
        return result;
      }

      @Override
      public MutableVector operate() {
        int dimension = result.getDimension();
        for (int index = 0; index < dimension; index++)
          result.setEntry(index, Math.max(result.getEntry(index), other.getEntry(index)));
        return result;
      }
    };
    return operate(maxOperation, filter);
  }

  public static MutableVector maxToSelf(final MutableVector result, final double other, RealVector filter) {
    FilteredOperation maxOperation = new FilteredOperation() {
      @Override
      public MutableVector sparseOperate(int[] indexes, int nbActive) {
        for (int i = 0; i < nbActive; i++) {
          int index = indexes[i];
          result.setEntry(index, Math.max(result.getEntry(index), other));
        }
        return result;
      }

      @Override
      public MutableVector operate() {
        int dimension = result.getDimension();
        for (int index = 0; index < dimension; index++)
          result.setEntry(index, Math.max(result.getEntry(index), other));
        return result;
      }
    };
    return operate(maxOperation, filter);
  }

  public static MutableVector minToSelf(final MutableVector result, final double other, RealVector filter) {
    FilteredOperation maxOperation = new FilteredOperation() {
      @Override
      public MutableVector sparseOperate(int[] indexes, int nbActive) {
        for (int i = 0; i < nbActive; i++) {
          int index = indexes[i];
          result.setEntry(index, Math.min(result.getEntry(index), other));
        }
        return result;
      }

      @Override
      public MutableVector operate() {
        int dimension = result.getDimension();
        for (int index = 0; index < dimension; index++)
          result.setEntry(index, Math.min(result.getEntry(index), other));
        return result;
      }
    };
    return operate(maxOperation, filter);
  }

  public static MutableVector boundAbsToSelf(final MutableVector result, final double other, RealVector filter) {
    FilteredOperation maxOperation = new FilteredOperation() {
      @Override
      public MutableVector sparseOperate(int[] indexes, int nbActive) {
        for (int i = 0; i < nbActive; i++) {
          int index = indexes[i];
          result.setEntry(index, boundValue(result.getEntry(index)));
        }
        return result;
      }

      @Override
      public MutableVector operate() {
        int dimension = result.getDimension();
        for (int index = 0; index < dimension; index++)
          result.setEntry(index, boundValue(result.getEntry(index)));
        return result;
      }

      public double boundValue(double value) {
        return Math.min(Math.abs(value), other) * Math.signum(value);
      }
    };
    return operate(maxOperation, filter);
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

  public static MutableVector mapDivideToSelf(final MutableVector result, final double d, RealVector filter) {
    FilteredOperation mapDivideSelfOperation = new FilteredOperation() {
      @Override
      public MutableVector sparseOperate(int[] indexes, int nbActive) {
        for (int i = 0; i < nbActive; i++) {
          int index = indexes[i];
          result.setEntry(index, result.getEntry(index) / d);
        }
        return result;
      }

      @Override
      public MutableVector operate() {
        return result.mapMultiplyToSelf(1 / d);
      }
    };
    return operate(mapDivideSelfOperation, filter);
  }

  public static MutableVector ebeDivideToSelf(final MutableVector result, final RealVector arg, RealVector filter) {
    FilteredOperation mapDivideSelfOperation = new FilteredOperation() {
      @Override
      public MutableVector sparseOperate(int[] indexes, int nbActive) {
        for (int i = 0; i < nbActive; i++) {
          int entryIndex = indexes[i];
          result.setEntry(entryIndex, result.getEntry(entryIndex) / arg.getEntry(entryIndex));
        }
        return result;
      }

      @Override
      public MutableVector operate() {
        return result.ebeDivideToSelf(arg);
      }
    };
    return operate(mapDivideSelfOperation, filter);
  }

  public static MutableVector powToSelf(final PVector result, final double b, RealVector filter) {
    FilteredOperation powSelfOperation = new FilteredOperation() {
      @Override
      public MutableVector sparseOperate(int[] indexes, int nbActive) {
        for (int i = 0; i < nbActive; i++) {
          int entryIndex = indexes[i];
          double previousValue = result.getEntry(entryIndex);
          double resultValue = Math.pow(previousValue, b);
          result.setEntry(entryIndex, resultValue);
        }
        return result;
      }

      @Override
      public MutableVector operate() {
        throw new NotImplemented();
      }
    };
    return operate(powSelfOperation, filter);
  }

  public static MutableVector expTo(final RealVector source, final MutableVector result, RealVector filter) {
    FilteredOperation expOperation = new FilteredOperation() {
      @Override
      public MutableVector sparseOperate(int[] indexes, int nbActive) {
        for (int i = 0; i < nbActive; i++) {
          int entryIndex = indexes[i];
          result.setEntry(entryIndex, Math.exp(source.getEntry(entryIndex)));
        }
        return result;
      }

      @Override
      public MutableVector operate() {
        int dimension = result.getDimension();
        for (int index = 0; index < dimension; index++)
          result.setEntry(index, Math.exp(source.getEntry(index)));
        return result;
      }
    };
    return operate(expOperation, filter);
  }


  public static MutableVector logTo(final RealVector source, final MutableVector result, RealVector filter) {
    FilteredOperation expOperation = new FilteredOperation() {
      @Override
      public MutableVector sparseOperate(int[] indexes, int nbActive) {
        for (int i = 0; i < nbActive; i++) {
          int entryIndex = indexes[i];
          result.setEntry(entryIndex, Math.log(source.getEntry(entryIndex)));
        }
        return result;
      }

      @Override
      public MutableVector operate() {
        int dimension = result.getDimension();
        for (int index = 0; index < dimension; index++)
          result.setEntry(index, Math.log(source.getEntry(index)));
        return result;
      }
    };
    return operate(expOperation, filter);
  }


  public static MutableVector set(final MutableVector result, final MutableVector x) {
    FilteredOperation setOperation = new FilteredOperation() {
      @Override
      public MutableVector sparseOperate(int[] indexes, int nbActive) {
        for (int i = 0; i < nbActive; i++) {
          int entryIndex = indexes[i];
          result.setEntry(entryIndex, x.getEntry(entryIndex));
        }
        return result;
      }

      @Override
      public MutableVector operate() {
        int dimension = result.getDimension();
        for (int index = 0; index < dimension; index++)
          result.setEntry(index, x.getEntry(index));
        return result;
      }
    };
    return operate(setOperation, x);
  }
}
