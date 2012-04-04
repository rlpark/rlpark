package rlpark.plugin.rltoys.algorithms.learning.predictions.td;

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import rlpark.plugin.rltoys.algorithms.learning.predictions.td.GTDLambda;
import rlpark.plugin.rltoys.algorithms.representations.acting.ConstantPolicy;
import rlpark.plugin.rltoys.algorithms.representations.traces.AMaxTraces;
import rlpark.plugin.rltoys.math.vector.implementations.PVector;
import rlpark.plugin.rltoys.math.vector.implementations.Vectors;
import rlpark.plugin.rltoys.problems.stategraph.FSGAgentState;
import rlpark.plugin.rltoys.problems.stategraph.RandomWalk;
import rlpark.plugin.rltoys.problems.stategraph.FiniteStateGraph.StepData;


public class GTDLambdaTest {
  @Test
  public void testOffPolicyGTD() {
    testOffPolicyGTD(0.0, 0.9, 0.01, 0.5, 0.2, 0.5);
    testOffPolicyGTD(0.0, 0.9, 0.01, 0.5, 0.5, 0.2);
  }

  @Test
  public void testOffPolicyGTDWithEligibility() {
    testOffPolicyGTD(0.1, 0.9, 0.01, 0.0, 0.2, 0.5);
    testOffPolicyGTD(0.1, 0.9, 0.01, 0.5, 0.2, 0.5);
    testOffPolicyGTD(0.1, 0.9, 0.01, 0.5, 0.5, 0.2);
  }

  private void testOffPolicyGTD(double lambda, double gamma, double alpha_v, double alpha_w,
      double targetLeftProbability, double behaviourLeftProbability) {
    Random random = new Random(0);
    ConstantPolicy behaviourPolicy = RandomWalk.newPolicy(random, behaviourLeftProbability);
    ConstantPolicy targetPolicy = RandomWalk.newPolicy(random, targetLeftProbability);
    RandomWalk problem = new RandomWalk(behaviourPolicy);
    FSGAgentState agentState = new FSGAgentState(problem);
    GTDLambda gtd = new GTDLambda(lambda, gamma, alpha_v, alpha_w, agentState.size, new AMaxTraces());
    int nbEpisode = 0;
    double[] solution = agentState.computeSolution(targetPolicy, gamma, lambda);
    PVector phi_t = null;
    while (TDTest.distanceToSolution(solution, gtd.weights()) > 0.05) {
      StepData stepData = agentState.step();
      double rho = 0.0;
      if (stepData.a_t != null)
        rho = targetPolicy.pi(stepData.v_t(), stepData.a_t) / behaviourPolicy.pi(stepData.v_t(), stepData.a_t);
      PVector phi_tp1 = agentState.currentFeatureState();
      gtd.update(rho, phi_t, phi_tp1, stepData.r_tp1);
      if (stepData.s_tp1 == null) {
        nbEpisode += 1;
        Assert.assertTrue(nbEpisode < 100000);
      }
      phi_t = phi_tp1 != null ? phi_tp1.copy() : null;
    }
    Assert.assertTrue(nbEpisode > 100);
    Assert.assertTrue(Vectors.checkValues(gtd.weights()));
  }
}
