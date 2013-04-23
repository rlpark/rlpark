package rlpark.plugin.rltoys.algorithms.representations.tilescoding;

import rlpark.plugin.rltoys.algorithms.representations.discretizer.DiscretizerFactory;
import rlpark.plugin.rltoys.algorithms.representations.tilescoding.hashing.Hashing;
import rlpark.plugin.rltoys.math.ranges.Range;
import rlpark.plugin.rltoys.math.vector.BinaryVector;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

@Monitor
public class TileCodersHashing extends TileCoders {
  private static final long serialVersionUID = -5352847170450739533L;
  private final Hashing hashing;

  public TileCodersHashing(Hashing hashing, int inputSize, double min, double max) {
    this(hashing, buildRanges(inputSize, min, max));
  }

  public TileCodersHashing(Hashing hashing, Range... ranges) {
    this(hashing, createDefaultDiscretizer(ranges), ranges.length);
  }

  public TileCodersHashing(Hashing hashing, DiscretizerFactory discretizerFactory, int nbInputs) {
    super(discretizerFactory, nbInputs);
    this.hashing = hashing;
  }

  @Override
  protected int computeVectorSize() {
    return hashing.memorySize();
  }

  @Override
  protected void activateIndexes(double[] inputs, BinaryVector vector) {
    for (TileCoder tileCoder : tileCoders)
      setFeatureOn(vector, tileCoder.updateActiveTiles(hashing, inputs));
  }
}
