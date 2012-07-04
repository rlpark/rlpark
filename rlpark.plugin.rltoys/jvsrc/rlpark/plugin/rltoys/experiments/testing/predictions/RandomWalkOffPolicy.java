package rlpark.plugin.rltoys.experiments.testing.predictions;

import java.util.Random;

import rlpark.plugin.rltoys.algorithms.predictions.td.OffPolicyTD;
import rlpark.plugin.rltoys.envio.policy.ConstantPolicy;
import rlpark.plugin.rltoys.experiments.testing.results.TestingResult;
import rlpark.plugin.rltoys.math.vector.implementations.PVector;
import rlpark.plugin.rltoys.math.vector.implementations.Vectors;
import rlpark.plugin.rltoys.problems.stategraph.FSGAgentState;
import rlpark.plugin.rltoys.problems.stategraph.FiniteStateGraph.StepData;
import rlpark.plugin.rltoys.problems.stategraph.RandomWalk;

public class RandomWalkOffPolicy {
  public interface OffPolicyTDFactory {
    OffPolicyTD newTD(double lambda, double gamma, int vectorSize);
  }

  static public TestingResult<OffPolicyTD> testOffPolicyGTD(double lambda, double gamma, double targetLeftProbability,
      double behaviourLeftProbability, OffPolicyTDFactory tdFactory) {
    Random random = new Random(0);
    ConstantPolicy behaviourPolicy = RandomWalk.newPolicy(random, behaviourLeftProbability);
    ConstantPolicy targetPolicy = RandomWalk.newPolicy(random, targetLeftProbability);
    RandomWalk problem = new RandomWalk(behaviourPolicy);
    FSGAgentState agentState = new FSGAgentState(problem);
    OffPolicyTD gtd = tdFactory.newTD(lambda, gamma, agentState.size);
    int nbEpisode = 0;
    double[] solution = agentState.computeSolution(targetPolicy, gamma, lambda);
    PVector phi_t = null;
    while (distanceToSolution(solution, gtd.weights()) > 0.05) {
      StepData stepData = agentState.step();
      double rho = 0.0;
      if (stepData.a_t != null)
        rho = targetPolicy.pi(stepData.v_t(), stepData.a_t) / behaviourPolicy.pi(stepData.v_t(), stepData.a_t);
      PVector phi_tp1 = agentState.currentFeatureState();
      gtd.update(rho, phi_t, phi_tp1, stepData.r_tp1);
      if (stepData.s_tp1 == null) {
        nbEpisode += 1;
        if (nbEpisode > 100000)
          return new TestingResult<OffPolicyTD>(false, "Did not learn fast enough", gtd);
        if (!Vectors.checkValues(gtd.weights()))
          return new TestingResult<OffPolicyTD>(false, "Weights are wrong", gtd);
      }
      phi_t = phi_tp1 != null ? phi_tp1.copy() : null;
    }
    if (nbEpisode < 10)
      return new TestingResult<OffPolicyTD>(false, "That was too quick!", gtd);
    return new TestingResult<OffPolicyTD>(true, null, gtd);
  }

  static public double distanceToSolution(double[] solution, PVector theta) {
    double max = 0;
    for (int i = 0; i < Math.max(solution.length, theta.size); i++)
      max = Math.max(max, Math.abs(solution[i] - theta.data[i]));
    return max;
  }

}
