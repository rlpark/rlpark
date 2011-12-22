package rltoys.algorithms.learning.control.gq;

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import rltoys.algorithms.learning.control.acting.Greedy;
import rltoys.algorithms.learning.control.acting.UnknownPolicy;
import rltoys.algorithms.learning.control.qlearning.QLearning;
import rltoys.algorithms.learning.control.qlearning.QLearningControl;
import rltoys.algorithms.learning.predictions.LinearLearner;
import rltoys.algorithms.representations.acting.Policy;
import rltoys.algorithms.representations.acting.RandomPolicy;
import rltoys.algorithms.representations.actions.Action;
import rltoys.algorithms.representations.actions.TabularAction;
import rltoys.algorithms.representations.tilescoding.TileCoders;
import rltoys.algorithms.representations.tilescoding.TileCodersNoHashing;
import rltoys.algorithms.representations.traces.AMaxTraces;
import rltoys.algorithms.representations.traces.RTraces;
import rltoys.environments.envio.RLAgent;
import rltoys.environments.envio.Runner;
import rltoys.environments.envio.observations.TRStep;
import rltoys.environments.envio.offpolicy.OffPolicyLearner;
import rltoys.environments.mountaincar.MountainCar;
import rltoys.math.vector.BinaryVector;

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
      QLearning qlearning = new QLearning(actions, alpha, Gamma, Lambda, toStateAction, toStateAction.vectorSize(),
                                          new RTraces());
      Greedy acting = new Greedy(qlearning, actions, toStateAction);
      return new QLearningControl(acting, qlearning);
    }

    private GreedyGQ createGreedyGQ(Policy behaviourPolicy, TileCoders projector, Action[] actions) {
      TabularAction toStateAction = new TabularAction(actions, projector.vectorNorm(), projector.vectorSize());
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
      Action a_tp1 = behaviourPolicy.decide(x_tp1);
      qlearning.learn(x_t, step.a_t, x_tp1, a_tp1, step.r_tp1);
      greedygq.learn(x_t, step.a_t, x_tp1, a_tp1, step.r_tp1);
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
}
