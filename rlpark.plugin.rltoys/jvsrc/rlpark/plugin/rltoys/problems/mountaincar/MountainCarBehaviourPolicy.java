package rlpark.plugin.rltoys.problems.mountaincar;

import java.util.Random;

import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.actions.ActionArray;
import rlpark.plugin.rltoys.envio.policy.StochasticPolicy;
import rlpark.plugin.rltoys.envio.rl.RLAgent;
import rlpark.plugin.rltoys.envio.rl.TRStep;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.math.vector.implementations.PVector;

public class MountainCarBehaviourPolicy extends StochasticPolicy implements RLAgent {
  private static final long serialVersionUID = -8033945251597842725L;
  private final int velocityIndex;
  private final double epsilon;
  private Action a_tp1;
  private final double[] distribution;

  public MountainCarBehaviourPolicy(MountainCar mountainCar, Random random, double epsilon) {
    super(random, mountainCar.actions());
    distribution = new double[actions.length];
    velocityIndex = mountainCar.legend().indexOf(MountainCar.VELOCITY);
    this.epsilon = epsilon;
  }

  @Override
  public Action getAtp1(TRStep step) {
    a_tp1 = null;
    if (!step.isEpisodeEnding())
      a_tp1 = decide(new PVector(step.o_tp1));
    return a_tp1;
  }

  private void updateDistribution(double velocity) {
    double adjustedVelocity = velocity == 0 ? 0.1 : velocity;
    double defaultProbability = epsilon / MountainCar.Actions.length;
    for (int i = 0; i < actions.length; i++) {
      Action action = actions[i];
      double probability = defaultProbability;
      double throttle = ActionArray.toDouble(action);
      if (throttle * adjustedVelocity > 0)
        probability += 1 - epsilon;
      distribution[i] = probability;
    }
  }

  @Override
  public double pi(RealVector s, Action a) {
    updateDistribution(((PVector) s).data[velocityIndex]);
    return distribution[atoi(a)];
  }

  @Override
  public Action decide(RealVector s) {
    updateDistribution(((PVector) s).data[velocityIndex]);
    return chooseAction(distribution);
  }

  @Override
  public double[] distribution() {
    return distribution;
  }
}
