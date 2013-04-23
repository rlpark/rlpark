package rlpark.plugin.rltoys.algorithms.representations.tilescoding;

import java.util.ArrayList;
import java.util.List;

import rlpark.plugin.rltoys.algorithms.functions.states.Projector;
import rlpark.plugin.rltoys.algorithms.representations.discretizer.Discretizer;
import rlpark.plugin.rltoys.algorithms.representations.discretizer.DiscretizerFactory;
import rlpark.plugin.rltoys.algorithms.representations.discretizer.partitions.BoundedBigPartitionFactory;
import rlpark.plugin.rltoys.algorithms.representations.tilescoding.hashing.Tiling;
import rlpark.plugin.rltoys.math.ranges.Range;
import rlpark.plugin.rltoys.math.vector.BinaryVector;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.math.vector.implementations.BVector;
import rlpark.plugin.rltoys.utils.Utils;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

@Monitor
public abstract class TileCoders implements Projector {
  private static final long serialVersionUID = -2663191120601745893L;
  protected final List<TileCoder> tileCoders = new ArrayList<TileCoder>();
  private BinaryVector vector;
  private boolean includeActiveFeature = false;
  private int tilingHashingIndex = 0;
  private final DiscretizerFactory discretizerFactory;
  private final int nbInputs;

  public TileCoders(DiscretizerFactory discretizerFactory, int nbInputs) {
    this.discretizerFactory = discretizerFactory;
    this.nbInputs = nbInputs;
  }

  public void includeActiveFeature() {
    includeActiveFeature = true;
    vector = newVectorInstance();
  }

  private BinaryVector newVectorInstance() {
    return new BVector(vectorSize());
  }

  public void addIndependentTilings(int gridResolution, int nbTilings) {
    for (int i = 0; i < nbInputs; i++)
      addTileCoder(new int[] { i }, gridResolution, nbTilings);
  }

  public void addFullTilings(int gridResolution, int nbTilings) {
    addTileCoder(Utils.range(0, nbInputs), gridResolution, nbTilings);
  }

  public void addTileCoder(int[] inputIndexes, int resolution, int nbTilings) {
    addTileCoder(discretizerFactory, inputIndexes, resolution, nbTilings);
  }

  public void addTileCoder(DiscretizerFactory discretizerFactory, int[] inputIndexes, int resolution, int nbTilings) {
    assert resolution > 0;
    assert nbTilings > 0;
    assert inputIndexes.length > 0;
    Tiling[] tilings = new Tiling[nbTilings];
    for (int tilingIndex = 0; tilingIndex < nbTilings; tilingIndex++) {
      Discretizer[] discretizers = new Discretizer[inputIndexes.length];
      for (int inputIndex = 0; inputIndex < discretizers.length; inputIndex++)
        discretizers[inputIndex] = discretizerFactory.createDiscretizer(inputIndexes[inputIndex], resolution,
                                                                        tilingIndex, nbTilings);
      tilings[tilingIndex] = new Tiling(tilingHashingIndex, discretizers, inputIndexes);
      tilingHashingIndex++;
    }
    addTileCoder(new TileCoder(tilings, resolution));
    vector = newVectorInstance();
  }

  public int nbInputs() {
    return nbInputs;
  }

  @Override
  public double vectorNorm() {
    int nbActiveTiles = 0;
    for (TileCoder tileCoder : tileCoders)
      nbActiveTiles += tileCoder.nbTilings();
    return includeActiveFeature ? nbActiveTiles + 1 : nbActiveTiles;
  }

  @Override
  public int vectorSize() {
    int vectorSize = computeVectorSize();
    return includeActiveFeature ? vectorSize + 1 : vectorSize;
  }

  @Override
  public BinaryVector project(double[] inputs) {
    vector.clear();
    if (inputs == null)
      return vector;
    activateIndexes(inputs, vector);
    if (includeActiveFeature)
      vector.setOn(vector.getDimension() - 1);
    return vector;
  }

  protected void addTileCoder(TileCoder tileCoder) {
    tileCoders.add(tileCoder);
  }

  abstract protected void activateIndexes(double[] inputs, BinaryVector vector);

  abstract protected int computeVectorSize();

  public RealVector vector() {
    return vector;
  }

  protected void setFeatureOn(BinaryVector vector, int[] indexes) {
    for (int i : indexes)
      vector.setOn(i);
  }

  public DiscretizerFactory discretizerFactory() {
    return discretizerFactory;
  }

  public List<TileCoder> tileCoders() {
    return tileCoders;
  }

  @Override
  public String toString() {
    StringBuilder result = new StringBuilder();
    for (int i = 0; i < tileCoders.size(); i++)
      result.append("TileCoder " + i + ":\n" + tileCoders.get(i).toString() + "\n");
    return result.toString();
  }

  public static Range[] buildRanges(int inputSize, double min, double max) {
    Range[] ranges = new Range[inputSize];
    for (int i = 0; i < ranges.length; i++)
      ranges[i] = new Range(min, max);
    return ranges;
  }

  public static DiscretizerFactory createDefaultDiscretizer(Range... ranges) {
    return new BoundedBigPartitionFactory(ranges);
  }

}
