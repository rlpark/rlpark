package rlpark.plugin.rltoys.junit.algorithms.control.gq;

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import rlpark.plugin.rltoys.algorithms.LinearLearner;
import rlpark.plugin.rltoys.algorithms.control.OffPolicyLearner;
import rlpark.plugin.rltoys.algorithms.control.acting.Greedy;
import rlpark.plugin.rltoys.algorithms.control.acting.UnknownPolicy;
import rlpark.plugin.rltoys.algorithms.control.gq.GQ;
import rlpark.plugin.rltoys.algorithms.control.gq.GreedyGQ;
import rlpark.plugin.rltoys.algorithms.control.qlearning.QLearning;
import rlpark.plugin.rltoys.algorithms.control.qlearning.QLearningControl;
import rlpark.plugin.rltoys.algorithms.functions.policydistributions.helpers.RandomPolicy;
import rlpark.plugin.rltoys.algorithms.functions.stateactions.TabularAction;
import rlpark.plugin.rltoys.algorithms.representations.tilescoding.TileCoders;
import rlpark.plugin.rltoys.algorithms.representations.tilescoding.TileCodersNoHashing;
import rlpark.plugin.rltoys.algorithms.traces.AMaxTraces;
import rlpark.plugin.rltoys.algorithms.traces.RTraces;
import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.policy.Policies;
import rlpark.plugin.rltoys.envio.policy.Policy;
import rlpark.plugin.rltoys.envio.rl.RLAgent;
import rlpark.plugin.rltoys.envio.rl.TRStep;
import rlpark.plugin.rltoys.experiments.runners.Runner;
import rlpark.plugin.rltoys.math.vector.BinaryVector;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.math.vector.implementations.Vectors;
import rlpark.plugin.rltoys.problems.mountaincar.MountainCar;

public class GQQLambdaTest {
  private static final double Alpha = .1;
  private static final double Gamma = .99;
  private static final double Lambda = .3;

  @SuppressWarnings("serial")
  class Agent implements RLAgent {
    private final Random random = new Random(0);
    private final QLearningControl qlearning;
    private final GreedyGQ greedygq;
    private final Policy behaviourPolicy;
    private final TileCoders representation;
    private BinaryVector x_t;

    public Agent(MountainCar problem) {
      behaviourPolicy = new UnknownPolicy(new RandomPolicy(random, problem.actions()));
      representation = createRepresentation(problem);
      qlearning = createQLearning(representation, problem.actions());
      greedygq = createGreedyGQ(behaviourPolicy, representation, problem.actions());
    }

    private TileCoders createRepresentation(MountainCar behaviourProblem) {
      TileCoders representation = new TileCodersNoHashing(behaviourProblem.getObservationRanges());
      representation.addFullTilings(10, 10);
      representation.includeActiveFeature();
      return representation;
    }

    private QLearningControl createQLearning(TileCoders projector, Action[] actions) {
      double alpha = Alpha / projector.vectorNorm();
      TabularAction toStateAction = new TabularAction(actions, projector.vectorNorm(), projector.vectorSize());
      toStateAction.includeActiveFeature();
      QLearning qlearning = new QLearning(actions, alpha, Gamma, Lambda, toStateAction, new RTraces());
      Greedy acting = new Greedy(qlearning, actions, toStateAction);
      return new QLearningControl(acting, qlearning);
    }

    private GreedyGQ createGreedyGQ(Policy behaviourPolicy, TileCoders projector, Action[] actions) {
      TabularAction toStateAction = new TabularAction(actions, projector.vectorNorm(), projector.vectorSize());
      toStateAction.includeActiveFeature();
      double alpha = Alpha / projector.vectorNorm();
      GQ gq = new GQ(alpha, 0.0, 1 - Gamma, Lambda, toStateAction.vectorSize(), new AMaxTraces(1.0));
      Greedy acting = new Greedy(gq, actions, toStateAction);
      return new GreedyGQ(gq, actions, toStateAction, acting, behaviourPolicy);
    }

    @Override
    public Action getAtp1(TRStep step) {
      if (step.isEpisodeStarting())
        x_t = null;
      BinaryVector x_tp1 = representation.project(step.o_tp1);
      Action a_tp1 = Policies.decide(behaviourPolicy, x_tp1);
      qlearning.learn(x_t, step.a_t, x_tp1, a_tp1, step.r_tp1);
      greedygq.learn(x_t, step.a_t, x_tp1, a_tp1, step.r_tp1);
      Assert.assertTrue(Vectors.equals(traces(qlearning), traces(greedygq), 1e-8));
      Assert.assertArrayEquals(weights(qlearning), weights(greedygq), 1e-8);
      x_t = x_tp1;
      return a_tp1;
    }

    private double[] weights(OffPolicyLearner control) {
      return ((LinearLearner) control.predictor()).weights().data;
    }
  }

  @Test
  public void compareGQToQLearning() {
    MountainCar problem = new MountainCar(null);
    new Runner(problem, new Agent(problem), 2, 1000).run();
  }

  public RealVector traces(GreedyGQ greedygq) {
    return greedygq.predictor().traces().vect();
  }

  public RealVector traces(QLearningControl qlearning) {
    return qlearning.predictor().traces().vect();
  }
}
