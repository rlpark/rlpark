package rltoys.algorithms.representations.tilescoding;

import java.util.ArrayList;
import java.util.List;

import rltoys.algorithms.representations.Projector;
import rltoys.algorithms.representations.discretizer.Discretizer;
import rltoys.algorithms.representations.discretizer.DiscretizerFactory;
import rltoys.algorithms.representations.tilescoding.hashing.Tiling;
import rltoys.math.vector.BinaryVector;
import rltoys.math.vector.RealVector;
import rltoys.math.vector.implementations.BVector;
import rltoys.utils.Utils;
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
    return new BVector(vectorSize(), (int) vectorNorm());
  }

  public void addIndependentTilings(int gridResolution, int nbTilings) {
    for (int i = 0; i < nbInputs; i++)
      addTileCoder(new int[] { i }, gridResolution, nbTilings);
  }

  public void addFullTilings(int gridResolution, int nbTilings) {
    addTileCoder(Utils.range(0, nbInputs), gridResolution, nbTilings);
  }

  public void addTileCoder(int[] inputIndexes, int resolution, int nbTilings) {
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

  public BinaryVector getCurrentState() {
    return vector.nonZeroElements() > 0 ? vector.copy() : null;
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
      return vector.copy();
    activateIndexes(inputs, vector);
    if (includeActiveFeature)
      vector.setOn(vector.getDimension() - 1);
    return vector.copy();
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
}
