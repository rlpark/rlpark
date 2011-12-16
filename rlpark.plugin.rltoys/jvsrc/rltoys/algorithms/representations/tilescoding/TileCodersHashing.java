package rltoys.algorithms.representations.tilescoding;

import rltoys.algorithms.representations.discretizer.DiscretizerFactory;
import rltoys.algorithms.representations.discretizer.partitions.PartitionFactory;
import rltoys.algorithms.representations.tilescoding.hashing.Hashing;
import rltoys.math.ranges.Range;
import rltoys.math.vector.BinaryVector;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

@Monitor
public class TileCodersHashing extends TileCoders {
  private static final long serialVersionUID = -5352847170450739533L;
  private final Hashing hashing;

  public TileCodersHashing(Hashing hashing, int inputSize, double min, double max) {
    this(hashing, new PartitionFactory(true, min, max, inputSize), inputSize);
  }

  public TileCodersHashing(Hashing hashing, Range... ranges) {
    this(hashing, new PartitionFactory(true, ranges), ranges.length);
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
