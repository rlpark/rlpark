package rlpark.plugin.rltoys.algorithms.representations.discretizer.avebins;

import java.io.Serializable;

import rlpark.plugin.rltoys.math.averages.MeanVar;
import rlpark.plugin.rltoys.math.vector.BinaryVector;
import rlpark.plugin.rltoys.math.vector.implementations.BVector;

public class AveBinsTree implements Serializable {
  private static final long serialVersionUID = -7079039075980716444L;
  public final int size;
  private final AveBins[] allAveBins;

  public AveBinsTree(MeanVar prototype, int nbBins) {
    this(prototype, nbBins, 2);
  }

  public AveBinsTree(MeanVar prototype, int nbBins, int divisor) {
    allAveBins = new AveBins[(int) (Math.log(nbBins) / Math.log(divisor))];
    int currentNbBins = nbBins;
    int index = 0;
    while (currentNbBins > 1) {
      allAveBins[index] = new AveBins(prototype, currentNbBins);
      currentNbBins /= divisor;
      index++;
    }
    size = computeSize();
  }

  private int computeSize() {
    int result = 0;
    for (AveBins aveBins : allAveBins)
      result += aveBins.resolution();
    return result;
  }

  public BinaryVector toBinary(double x) {
    BVector result = new BVector(size);
    int offset = 0;
    for (AveBins aveBins : allAveBins) {
      result.setOn(aveBins.discretize(x) + offset);
      offset += aveBins.resolution();
    }
    return result;
  }
}
