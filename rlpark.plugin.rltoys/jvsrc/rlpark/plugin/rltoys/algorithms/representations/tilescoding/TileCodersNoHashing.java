package rlpark.plugin.rltoys.algorithms.representations.tilescoding;

import java.util.ArrayList;
import java.util.List;

import rlpark.plugin.rltoys.algorithms.representations.discretizer.DiscretizerFactory;
import rlpark.plugin.rltoys.algorithms.representations.tilescoding.hashing.Identity;
import rlpark.plugin.rltoys.math.ranges.Range;
import rlpark.plugin.rltoys.math.vector.BinaryVector;

public class TileCodersNoHashing extends TileCoders {
  private static final long serialVersionUID = -5352847170450739533L;
  private final List<Identity> identities = new ArrayList<Identity>();

  public TileCodersNoHashing(int inputSize, double min, double max) {
    this(buildRanges(inputSize, min, max));
  }

  public TileCodersNoHashing(Range... ranges) {
    this(createDefaultDiscretizer(ranges), ranges.length);
  }

  public TileCodersNoHashing(DiscretizerFactory discretizerFactory, int nbInputs) {
    super(discretizerFactory, nbInputs);
  }

  @Override
  protected int computeVectorSize() {
    int vectorSize = 0;
    for (int i = 0; i < tileCoders.size(); i++) {
      TileCoder tileCoder = tileCoders.get(i);
      Identity identity = identities.get(i);
      vectorSize += identity.memorySize() * tileCoder.nbTilings();
    }
    return vectorSize;
  }

  @Override
  protected void activateIndexes(double[] inputs, BinaryVector vector) {
    int indexOffset = 0;
    for (int i = 0; i < tileCoders.size(); i++) {
      TileCoder tileCoder = tileCoders.get(i);
      Identity identity = identities.get(i);
      int[] activeTiles = tileCoder.updateActiveTiles(identity, inputs);
      for (int j = 0; j < activeTiles.length; j++) {
        activeTiles[j] += indexOffset;
        indexOffset += identity.memorySize();
      }
      setFeatureOn(vector, activeTiles);
    }
  }

  @Override
  protected void addTileCoder(TileCoder tileCoder) {
    super.addTileCoder(tileCoder);
    identities.add(new Identity(tileCoder.tilings()[0]));
  }
}
