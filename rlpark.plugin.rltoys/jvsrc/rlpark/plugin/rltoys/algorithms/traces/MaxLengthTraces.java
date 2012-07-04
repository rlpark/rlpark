package rlpark.plugin.rltoys.algorithms.traces;

import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.math.vector.implementations.SSortedVector;

public class MaxLengthTraces implements Traces {
  private static final long serialVersionUID = 2392872021978375762L;
  private final Traces traces;
  private final int maximumLength;

  public MaxLengthTraces(Traces traces, int maximumLength) {
    this.traces = traces;
    this.maximumLength = maximumLength;
    if (!(traces.newTraces(1).vect() instanceof SSortedVector))
      throw new RuntimeException("MaxLengthTraces supports only traces with a SSortedVector prototype");
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
  public SSortedVector vect() {
    return (SSortedVector) traces.vect();
  }

  private void controlLength() {
    int superfluousElements = vect().nonZeroElements() - maximumLength;
    if (superfluousElements <= 0)
      return;
    vect().removeTail(superfluousElements);
  }
}
