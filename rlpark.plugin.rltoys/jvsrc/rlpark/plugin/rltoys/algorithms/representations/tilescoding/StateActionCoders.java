package rlpark.plugin.rltoys.algorithms.representations.tilescoding;

import java.util.Arrays;

import rlpark.plugin.rltoys.algorithms.functions.stateactions.StateToStateAction;
import rlpark.plugin.rltoys.algorithms.representations.discretizer.ActionDiscretizer;
import rlpark.plugin.rltoys.algorithms.representations.discretizer.Discretizer;
import rlpark.plugin.rltoys.algorithms.representations.discretizer.DiscretizerFactory;
import rlpark.plugin.rltoys.algorithms.representations.tilescoding.hashing.Hashing;
import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.math.vector.RealVector;

public class StateActionCoders implements StateToStateAction {
  private static final long serialVersionUID = 6906465332938314787L;
  private final TileCoders tileCoders;
  private final ActionDiscretizer actionDiscretizer;

  public StateActionCoders(ActionDiscretizer actionDiscretizer, DiscretizerFactory discretizerFactory, int nbInputs) {
    this(actionDiscretizer, new TileCodersNoHashing(createDiscretizerFactory(actionDiscretizer, discretizerFactory,
                                                                             nbInputs), nbInputs
        + actionDiscretizer.nbOutput()));
  }

  public StateActionCoders(ActionDiscretizer actionDiscretizer, Hashing hashing, DiscretizerFactory discretizerFactory,
      int nbInputs) {
    this(actionDiscretizer, new TileCodersHashing(hashing, createDiscretizerFactory(actionDiscretizer,
                                                                                    discretizerFactory, nbInputs),
                                                  nbInputs + actionDiscretizer.nbOutput()));
  }

  public StateActionCoders(ActionDiscretizer actionDiscretizer, TileCoders tileCoders) {
    this.actionDiscretizer = actionDiscretizer;
    this.tileCoders = tileCoders;
  }

  @Override
  public RealVector stateAction(RealVector s, Action a) {
    if (s == null || a == null)
      return tileCoders.project(null);
    double[] sa = Arrays.copyOf(s.accessData(), s.getDimension() + actionDiscretizer.nbOutput());
    System.arraycopy(actionDiscretizer.discretize(a), 0, sa, s.getDimension(), actionDiscretizer.nbOutput());
    return tileCoders.project(sa);
  }

  @Override
  public double vectorNorm() {
    return tileCoders.vectorNorm();
  }

  @Override
  public int vectorSize() {
    return tileCoders.vectorSize();
  }

  public TileCoders tileCoders() {
    return tileCoders;
  }

  static public DiscretizerFactory createDiscretizerFactory(ActionDiscretizer actionDiscretizerFactory,
      final DiscretizerFactory discretizerFactory, final int nbInputs) {
    final Discretizer[] actionDiscretizers = actionDiscretizerFactory.actionDiscretizers();
    return new DiscretizerFactory() {
      private static final long serialVersionUID = -4362287012399520301L;

      @Override
      public Discretizer createDiscretizer(int inputIndex, int resolution, int tilingIndex, int nbTilings) {
        if (inputIndex < nbInputs)
          return discretizerFactory.createDiscretizer(inputIndex, resolution, tilingIndex, nbTilings);
        return actionDiscretizers[inputIndex - nbInputs];
      }
    };
  }
}
