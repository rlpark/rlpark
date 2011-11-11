package rltoys.algorithms.representations.ltu.networks;

import rltoys.algorithms.representations.Projector;
import rltoys.math.vector.BinaryVector;
import rltoys.math.vector.RealVector;
import rltoys.math.vector.implementations.BVector;

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
