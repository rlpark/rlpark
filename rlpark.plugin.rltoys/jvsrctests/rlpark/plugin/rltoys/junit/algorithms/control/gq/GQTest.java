package rlpark.plugin.rltoys.junit.algorithms.control.gq;

import java.util.Map;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import rlpark.plugin.rltoys.algorithms.LinearLearner;
import rlpark.plugin.rltoys.algorithms.control.OffPolicyLearner;
import rlpark.plugin.rltoys.algorithms.control.gq.GQ;
import rlpark.plugin.rltoys.algorithms.control.gq.GreedyGQ;
import rlpark.plugin.rltoys.algorithms.functions.Predictor;
import rlpark.plugin.rltoys.algorithms.functions.stateactions.StateToStateAction;
import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.policy.ConstantPolicy;
import rlpark.plugin.rltoys.envio.policy.Policy;
import rlpark.plugin.rltoys.experiments.testing.predictions.FiniteStateGraphOnPolicy;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.math.vector.implementations.PVector;
import rlpark.plugin.rltoys.math.vector.implementations.Vectors;
import rlpark.plugin.rltoys.problems.stategraph.FSGAgentState;
import rlpark.plugin.rltoys.problems.stategraph.FiniteStateGraph.StepData;
import rlpark.plugin.rltoys.problems.stategraph.GraphState;
import rlpark.plugin.rltoys.problems.stategraph.RandomWalk;


public class GQTest {
  public interface OffPolicyLearnerFactory {
    OffPolicyLearner createLearner(Action[] actions, StateToStateAction toStateAction, Policy target, Policy behaviour);

    double beta();

    double lambda();
  }

  class GreedyGQFactory implements OffPolicyLearnerFactory {
    private final double beta;
    private final double alpha_theta;
    private final double alpha_w;
    private final double lambda;

    public GreedyGQFactory(double beta, double alpha_theta, double alpha_w, double lambda) {
      this.beta = beta;
      this.alpha_theta = alpha_theta;
      this.alpha_w = alpha_w;
      this.lambda = lambda;
    }

    @Override
    public OffPolicyLearner createLearner(Action[] actions, StateToStateAction toStateAction, Policy target,
        Policy behaviour) {
      GQ gq = new GQ(alpha_theta, alpha_w, beta, lambda, toStateAction.vectorSize() * actions.length);
      return new GreedyGQ(gq, actions, toStateAction, target, behaviour);
    }

    @Override
    public double beta() {
      return beta;
    }

    @Override
    public double lambda() {
      return lambda;
    }
  };

  @Test
  public void testOnPolicyGQ() {
    testGQOnRandomWalk(0.5, 0.5, new GreedyGQFactory(0.0, 0.01, 0.0, 0.0));
    testGQOnRandomWalk(0.5, 0.5, new GreedyGQFactory(0.1, 0.01, 0.0, 0.0));
    testGQOnRandomWalk(0.5, 0.5, new GreedyGQFactory(0.1, 0.01, 0.0, 0.1));
    testGQOnRandomWalk(0.5, 0.5, new GreedyGQFactory(0.1, 0.01, 0.5, 0.1));
  }

  @Test
  public void testOffPolicyGQ() {
    testGQOnRandomWalk(0.3, 0.5, new GreedyGQFactory(0.1, 0.01, 0.0, 0.0));
    testGQOnRandomWalk(0.3, 0.5, new GreedyGQFactory(0.1, 0.01, 0.0, 0.1));
    testGQOnRandomWalk(0.3, 0.5, new GreedyGQFactory(0.1, 0.01, 0.5, 0.1));
  }

  static public void testGQOnRandomWalk(double targetLeftProbability, double behaviourLeftProbability,
      OffPolicyLearnerFactory learnerFactory) {
    Random random = new Random(0);
    ConstantPolicy behaviourPolicy = RandomWalk.newPolicy(random, behaviourLeftProbability);
    ConstantPolicy targetPolicy = RandomWalk.newPolicy(random, targetLeftProbability);
    RandomWalk problem = new RandomWalk(behaviourPolicy);
    FSGAgentState agentState = new FSGAgentState(problem);
    OffPolicyLearner learner = learnerFactory.createLearner(agentState.graph().actions(), agentState, targetPolicy,
                                                            behaviourPolicy);
    int nbEpisode = 0;
    double[] solution = agentState.computeSolution(targetPolicy, 1 - learnerFactory.beta(), learnerFactory.lambda());
    PVector v = new PVector(agentState.size);
    while (FiniteStateGraphOnPolicy.distanceToSolution(solution, v) > 0.05) {
      StepData stepData = agentState.step();
      learner.learn(stepData.v_t(), stepData.a_t, stepData.v_tp1(), stepData.a_tp1, stepData.r_tp1);
      if (stepData.s_tp1 == null) {
        nbEpisode += 1;
        Assert.assertTrue(nbEpisode < 100000);
      }
      v = computeValueFunction(agentState, learner.predictor(), targetPolicy);
    }
    Assert.assertTrue(nbEpisode > 100);
    Assert.assertTrue(Vectors.checkValues(((LinearLearner) learner.predictor()).weights()));
  }

  static private PVector computeValueFunction(FSGAgentState agentState, Predictor actionValuePredictor,
      Policy targetPolicy) {
    PVector v = new PVector(agentState.size);
    for (Map.Entry<GraphState, Integer> entry : agentState.stateIndexes().entrySet()) {
      GraphState s = entry.getKey();
      int si = entry.getValue();
      double v_s = 0;
      targetPolicy.update(s.v());
      for (Action a : agentState.graph().actions()) {
        RealVector phi_sa = agentState.stateAction(s.v(), a);
        v_s += targetPolicy.pi(a) * actionValuePredictor.predict(phi_sa);
      }
      v.data[si] = v_s;
    }
    return v;
  }
}
