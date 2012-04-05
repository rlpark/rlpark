package rlpark.plugin.rltoys.algorithms.traces;

import java.util.Arrays;

import rlpark.plugin.rltoys.math.vector.MutableVector;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.math.vector.implementations.SVector;

public class MaxLengthTraces implements Traces {
  private static final long serialVersionUID = 2392872021978375762L;
  private final Traces traces;
  private final int maximumLength;

  public MaxLengthTraces(Traces traces, int maximumLength) {
    this.traces = traces;
    this.maximumLength = maximumLength;
  }

  @Override
  public Traces newTraces(int size) {
    return new MaxLengthTraces(traces.newTraces(size), maximumLength);
  }

  @Override
  public void update(double lambda, RealVector phi) {
    traces.update(lambda, phi);
    controlLength();
  }

  @Override
  public void clear() {
    traces.clear();
  }

  @Override
  public MutableVector vect() {
    return traces.vect();
  }

  private void controlLength() {
    if (((SVector) vect()).nonZeroElements() <= maximumLength)
      return;
    int[] entryRemoved = fillRemovedEntry();
    entryRemoved = adjustEntryForDynamicRemoval(entryRemoved);
    for (int entryIndex : entryRemoved)
      ((SVector) vect()).removeExistingEntry(entryIndex);
  }

  private int[] adjustEntryForDynamicRemoval(int[] entryRemoved) {
    int[] entryAdjusted = new int[entryRemoved.length];
    for (int i = 0; i < entryRemoved.length; i++) {
      int offset = 0;
      for (int j = 0; j < i; j++)
        if (entryRemoved[j] < entryRemoved[i])
          offset++;
      entryAdjusted[i] = entryRemoved[i] - offset;
    }
    return entryAdjusted;
  }

  private int[] fillRemovedEntry() {
    SVector vect = (SVector) vect();
    int[] entryRemoved = new int[vect.nonZeroElements() - maximumLength];
    Arrays.fill(entryRemoved, -1);
    double[] entryValues = new double[vect.nonZeroElements() - maximumLength];
    Arrays.fill(entryValues, Double.MAX_VALUE);
    double[] values = vect.values;
    double maximumRemovedValue = Double.MAX_VALUE;
    for (int entry = 0; entry < vect.nonZeroElements(); entry++) {
      final double value = values[entry];
      if (maximumRemovedValue < value)
        continue;
      insertValue(entryRemoved, entryValues, entry, value);
      maximumRemovedValue = entryValues[entryValues.length - 1];
    }
    return entryRemoved;
  }

  private void insertValue(int[] entryRemoved, double[] entryValues, int entry, double value) {
    int position = 0;
    while (value > entryValues[position] && position < entryRemoved.length - 1)
      position++;
    System.arraycopy(entryRemoved, position, entryRemoved, position + 1, entryRemoved.length - position - 1);
    System.arraycopy(entryValues, position, entryValues, position + 1, entryValues.length - position - 1);
    entryRemoved[position] = entry;
    entryValues[position] = value;
  }
}
