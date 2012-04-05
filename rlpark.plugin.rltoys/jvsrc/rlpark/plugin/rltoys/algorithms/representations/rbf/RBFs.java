package rlpark.plugin.rltoys.algorithms.representations.rbf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import rlpark.plugin.rltoys.algorithms.functions.states.Projector;
import rlpark.plugin.rltoys.math.ranges.Range;
import rlpark.plugin.rltoys.math.vector.implementations.SVector;
import rlpark.plugin.rltoys.utils.Utils;

public class RBFs implements Projector {
  private static final long serialVersionUID = -1905703492835265008L;
  private final List<RBF> rbfs = new ArrayList<RBF>();
  private SVector vector;
  private boolean includeActiveFeature = false;
  private final double tolerance;

  public RBFs(double tolerance) {
    this.tolerance = tolerance;
  }

  private SVector newVectorInstance() {
    return new SVector(vectorSize());
  }

  public void includeActiveFeature() {
    includeActiveFeature = true;
    vector = newVectorInstance();
  }

  public void addIndependentRBFs(Range[] allRanges, int resolution, double stddev) {
    for (int i = 0; i < allRanges.length; i++)
      addRBFs(allRanges, new int[] { i }, resolution, stddev);
  }

  public void addIndependentRBFs(Range[] allRanges, int[] selectedInputs, int resolution, double stddev) {
    for (int i : selectedInputs)
      addRBFs(allRanges, new int[] { i }, resolution, stddev);
  }

  public void addFullRBFs(Range[] allRanges, int resolution, double stddev) {
    addRBFs(allRanges, Utils.range(0, allRanges.length), resolution, stddev);
  }

  public void addRBFs(Range[] ranges, int[] inputIndexes, int resolution, double stddev) {
    RBF[] addedRbfs = new RBF[] { new RBF(new int[] {}, new double[] {}, stddev) };
    for (int i = 0; i < inputIndexes.length; i++) {
      int inputIndex = inputIndexes[i];
      addedRbfs = combine(addedRbfs, inputIndex, ranges[inputIndex], resolution, stddev);
    }
    for (RBF rbf : addedRbfs)
      rbfs.add(rbf);
    vector = newVectorInstance();
  }

  private RBF[] combine(RBF[] rbfs, int inputIndex, Range range, int resolution, double stddev) {
    RBF[] result = new RBF[rbfs.length * resolution];
    double step = range.length() / resolution;
    double start = range.min() + step / 2;
    for (int i = 0; i < rbfs.length; i++) {
      RBF source = rbfs[i % rbfs.length];
      int newPatternLength = source.patternIndexes().length + 1;
      for (int x = 0; x < resolution; x++) {
        int[] patternIndexes = Arrays.copyOf(source.patternIndexes(), newPatternLength);
        patternIndexes[newPatternLength - 1] = inputIndex;
        double[] patternValues = Arrays.copyOf(source.patternValues(), newPatternLength);
        patternValues[newPatternLength - 1] = x * step + start;
        result[i * resolution + x] = new RBF(patternIndexes, patternValues, stddev);
      }
    }
    return result;
  }

  @Override
  public SVector project(double[] inputs) {
    vector.clear();
    if (inputs == null)
      return vector.copy();
    for (int i = 0; i < rbfs.size(); i++) {
      final RBF rbf = rbfs.get(i);
      double distance = rbf.value(inputs);
      if (distance > tolerance)
        vector.setEntry(i, distance);
    }
    if (includeActiveFeature)
      vector.setEntry(vector.getDimension() - 1, 1.0);
    return vector.copy();
  }

  @Override
  public int vectorSize() {
    int vectorSize = rbfs.size();
    return includeActiveFeature ? vectorSize + 1 : vectorSize;
  }

  @Override
  public double vectorNorm() {
    return vectorSize();
  }
}
