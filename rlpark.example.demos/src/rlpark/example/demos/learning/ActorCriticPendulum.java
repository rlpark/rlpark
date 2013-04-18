package rlpark.example.demos.learning;

import java.util.Random;

import rlpark.plugin.rltoys.agents.functions.ValueFunction2D;
import rlpark.plugin.rltoys.agents.functions.FunctionProjected2D;
import rlpark.plugin.rltoys.agents.rl.LearnerAgentFA;
import rlpark.plugin.rltoys.algorithms.control.actorcritic.onpolicy.Actor;
import rlpark.plugin.rltoys.algorithms.control.actorcritic.onpolicy.AverageRewardActorCritic;
import rlpark.plugin.rltoys.algorithms.functions.policydistributions.PolicyDistribution;
import rlpark.plugin.rltoys.algorithms.functions.policydistributions.helpers.ScaledPolicyDistribution;
import rlpark.plugin.rltoys.algorithms.functions.policydistributions.structures.NormalDistributionScaled;
import rlpark.plugin.rltoys.algorithms.predictions.td.OnPolicyTD;
import rlpark.plugin.rltoys.algorithms.predictions.td.TDLambda;
import rlpark.plugin.rltoys.algorithms.representations.discretizer.partitions.AbstractPartitionFactory;
import rlpark.plugin.rltoys.algorithms.representations.tilescoding.TileCodersNoHashing;
import rlpark.plugin.rltoys.experiments.helpers.Runner;
import rlpark.plugin.rltoys.experiments.helpers.Runner.RunnerEvent;
import rlpark.plugin.rltoys.math.ranges.Range;
import rlpark.plugin.rltoys.problems.pendulum.SwingPendulum;
import zephyr.plugin.core.api.Zephyr;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;
import zephyr.plugin.core.api.signals.Listener;
import zephyr.plugin.core.api.synchronization.Clock;


@Monitor
public class ActorCriticPendulum implements Runnable {
  final FunctionProjected2D valueFunction;
  double reward;
  private final SwingPendulum problem;
  private final Clock clock = new Clock("ActorCriticPendulum");
  private final LearnerAgentFA agent;
  private final Runner runner;

  public ActorCriticPendulum() {
    Random random = new Random(0);
    problem = new SwingPendulum(null, false);
    TileCodersNoHashing tileCoders = new TileCodersNoHashing(problem.getObservationRanges());
    ((AbstractPartitionFactory) tileCoders.discretizerFactory()).setRandom(random, .2);
    tileCoders.addFullTilings(10, 10);
    double gamma = 1.0;
    double lambda = .5;
    double vectorNorm = tileCoders.vectorNorm();
    int vectorSize = tileCoders.vectorSize();
    OnPolicyTD critic = new TDLambda(lambda, gamma, .1 / vectorNorm, vectorSize);
    PolicyDistribution policyDistribution = new NormalDistributionScaled(random, 0.0, 1.0);
    policyDistribution = new ScaledPolicyDistribution(policyDistribution, new Range(-2, 2), problem.actionRanges()[0]);
    Actor actor = new Actor(policyDistribution, 0.001 / vectorNorm, vectorSize);
    AverageRewardActorCritic actorCritic = new AverageRewardActorCritic(.0001, critic, actor);
    agent = new LearnerAgentFA(actorCritic, tileCoders);
    valueFunction = new ValueFunction2D(tileCoders, problem, critic);
    runner = new Runner(problem, agent, -1, 1000);
    runner.onEpisodeEnd.connect(new Listener<Runner.RunnerEvent>() {
      @Override
      public void listen(RunnerEvent eventInfo) {
        System.out.println(String.format("Episode %d: %f", eventInfo.nbEpisodeDone, eventInfo.episodeReward));
      }
    });
    Zephyr.advertise(clock, this);
  }

  @Override
  public void run() {
    while (clock.tick())
      runner.step();
  }

  public static void main(String[] args) {
    new ActorCriticPendulum().run();
  }
}
