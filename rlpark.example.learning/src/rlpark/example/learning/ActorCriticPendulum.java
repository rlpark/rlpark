package rlpark.example.learning;

import java.util.Random;

import rlpark.plugin.rltoys.algorithms.learning.control.actorcritic.onpolicy.Actor;
import rlpark.plugin.rltoys.algorithms.learning.control.actorcritic.onpolicy.AverageRewardActorCritic;
import rlpark.plugin.rltoys.algorithms.learning.control.actorcritic.policystructure.NormalDistributionSkewed;
import rlpark.plugin.rltoys.algorithms.learning.predictions.ValueFunction2D;
import rlpark.plugin.rltoys.algorithms.learning.predictions.td.OnPolicyTD;
import rlpark.plugin.rltoys.algorithms.learning.predictions.td.TDLambda;
import rlpark.plugin.rltoys.algorithms.representations.acting.PolicyDistribution;
import rlpark.plugin.rltoys.algorithms.representations.acting.ScaledPolicyDistribution;
import rlpark.plugin.rltoys.algorithms.representations.discretizer.partitions.PartitionFactory;
import rlpark.plugin.rltoys.algorithms.representations.tilescoding.TileCodersNoHashing;
import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.control.ControlLearner;
import rlpark.plugin.rltoys.envio.observations.TRStep;
import rlpark.plugin.rltoys.math.ranges.Range;
import rlpark.plugin.rltoys.math.vector.BinaryVector;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.problems.pendulum.SwingPendulum;
import rlpark.plugin.rltoys.problems.pendulum.SwingPendulumWithReset;
import zephyr.plugin.core.api.Zephyr;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;
import zephyr.plugin.core.api.synchronization.Clock;

@Monitor
public class ActorCriticPendulum implements Runnable {
  final ValueFunction2D valueFunction;
  double reward;
  private final SwingPendulum problem;
  private final ControlLearner actorCritic;
  private final TileCodersNoHashing tileCoders;
  private final Clock clock = new Clock("ActorCriticPendulum");

  public ActorCriticPendulum() {
    Random random = new Random(0);
    problem = new SwingPendulumWithReset(null, 1000);
    tileCoders = new TileCodersNoHashing(problem.getObservationRanges());
    ((PartitionFactory) tileCoders.discretizerFactory()).setRandom(random, .2);
    tileCoders.addFullTilings(10, 10);
    double gamma = 1.0;
    double lambda = .75;
    double vectorNorm = tileCoders.vectorNorm();
    int vectorSize = tileCoders.vectorSize();
    OnPolicyTD critic = new TDLambda(lambda, gamma, .1 / vectorNorm, vectorSize);
    PolicyDistribution policyDistribution = new NormalDistributionSkewed(random, 0.0, 1.0);
    policyDistribution = new ScaledPolicyDistribution(policyDistribution, new Range(-2, 2), problem.actionRanges()[0]);
    Actor actor = new Actor(policyDistribution, 0.001 / vectorNorm, vectorSize);
    actorCritic = new AverageRewardActorCritic(.0001, critic, actor);
    valueFunction = new ValueFunction2D(tileCoders, problem, critic);
    Zephyr.advertise(clock, this);
  }

  @Override
  public void run() {
    TRStep step = problem.initialize();
    Action action = null;
    RealVector x_t = null;
    while (clock.tick()) {
      if (step.isEpisodeStarting())
        x_t = null;
      BinaryVector x_tp1 = tileCoders.project(step.o_tp1);
      reward = step.r_tp1;
      action = actorCritic.step(x_t, step.a_t, x_tp1, step.r_tp1);
      step = problem.step(action);
      x_t = x_tp1;
    }
  }

  public static void main(String[] args) {
    new ActorCriticPendulum().run();
  }
}
