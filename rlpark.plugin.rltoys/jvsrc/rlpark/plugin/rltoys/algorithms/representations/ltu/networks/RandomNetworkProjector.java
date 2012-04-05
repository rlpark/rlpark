package rlpark.plugin.rltoys.algorithms.representations.ltu.networks;

import rlpark.plugin.rltoys.algorithms.functions.states.Projector;
import rlpark.plugin.rltoys.math.vector.BinaryVector;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.math.vector.implementations.BVector;

public class RandomNetworkProjector implements Projector {
  private static final long serialVersionUID = 520975442867936390L;
  private final RandomNetwork randomNetwork;

  public RandomNetworkProjector(RandomNetwork randomNetwork) {
    this.randomNetwork = randomNetwork;
  }

  @Override
  public RealVector project(double[] obs) {
    BinaryVector bobs = obs != null ? BVector.toBinary(obs) : null;
    return randomNetwork.project(bobs);
  }

  @Override
  public int vectorSize() {
    return randomNetwork.outputSize;
  }

  @Override
  public double vectorNorm() {
    return randomNetwork.nbActive;
  }
}
