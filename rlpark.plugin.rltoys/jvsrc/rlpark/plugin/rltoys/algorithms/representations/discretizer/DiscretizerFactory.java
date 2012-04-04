package rlpark.plugin.rltoys.algorithms.representations.discretizer;

import java.io.Serializable;

public interface DiscretizerFactory extends Serializable {
  Discretizer createDiscretizer(int inputIndex, int resolution, int tilingIndex, int nbTilings);
}
