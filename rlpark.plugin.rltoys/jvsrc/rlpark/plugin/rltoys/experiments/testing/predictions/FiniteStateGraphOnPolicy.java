package rlpark.plugin.rltoys.experiments.testing.predictions;

import org.junit.Assert;

import rlpark.plugin.rltoys.algorithms.predictions.td.OnPolicyTD;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.math.vector.implementations.PVector;
import rlpark.plugin.rltoys.math.vector.implementations.Vectors;
import rlpark.plugin.rltoys.problems.stategraph.FSGAgentState;
import rlpark.plugin.rltoys.problems.stategraph.FiniteStateGraph;
import rlpark.plugin.rltoys.problems.stategraph.FiniteStateGraph.StepData;

public class FiniteStateGraphOnPolicy {

  static public interface OnPolicyTDFactory {
    OnPolicyTD create(int nbFeatures);
  }

  static public double distanceToSolution(double[] solution, PVector theta) {
    double max = 0;
    for (int i = 0; i < Math.max(solution.length, theta.size); i++)
      max = Math.max(max, Math.abs(solution[i] - theta.data[i]));
    return max;
  }

  public static void testTD(FiniteStateGraph problem, FiniteStateGraphOnPolicy.OnPolicyTDFactory tdFactory) {
    FSGAgentState agentState = new FSGAgentState(problem);
    OnPolicyTD td = tdFactory.create(agentState.size);
    int nbEpisode = 0;
    double[] solution = problem.expectedDiscountedSolution();
    RealVector x_t = null;
    while (distanceToSolution(solution, td.weights()) > 0.05) {
      StepData stepData = agentState.step();
      RealVector x_tp1 = agentState.currentFeatureState();
      td.update(x_t, x_tp1, stepData.r_tp1);
      if (stepData.s_tp1 == null) {
        nbEpisode += 1;
        Assert.assertTrue(nbEpisode < 100000);
      }
      x_t = x_tp1 != null ? x_tp1.copy() : null;
    }
    Assert.assertTrue(nbEpisode > 2);
    Assert.assertTrue(Vectors.checkValues(td.weights()));
  }
}
