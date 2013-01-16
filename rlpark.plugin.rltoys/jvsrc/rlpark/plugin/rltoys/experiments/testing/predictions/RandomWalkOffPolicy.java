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
    OffPolicyTD newTD(double lambda, double gamma, double vectorNorm, int vectorSize);
  }

  static public TestingResult<OffPolicyTD> testOffPolicyGTD(int nbEpisodeMax, double precision, double lambda,
      double gamma, double targetLeftProbability, double behaviourLeftProbability, OffPolicyTDFactory tdFactory) {
    Random random = new Random(0);
    ConstantPolicy behaviourPolicy = RandomWalk.newPolicy(random, behaviourLeftProbability);
    ConstantPolicy targetPolicy = RandomWalk.newPolicy(random, targetLeftProbability);
    RandomWalk problem = new RandomWalk(behaviourPolicy);
    FSGAgentState agentState = new FSGAgentState(problem);
    OffPolicyTD gtd = tdFactory.newTD(lambda, gamma, agentState.vectorNorm(), agentState.vectorSize());
    int nbEpisode = 0;
    double[] solution = agentState.computeSolution(targetPolicy, gamma, lambda);
    PVector phi_t = null;
    if (FiniteStateGraphOnPolicy.distanceToSolution(solution, gtd.weights()) <= precision)
      return new TestingResult<OffPolicyTD>(false, "Precision is incorrect!", gtd);
    while (FiniteStateGraphOnPolicy.distanceToSolution(solution, gtd.weights()) > precision) {
      StepData stepData = agentState.step();
      double rho = 0.0;
      if (stepData.a_t != null)
        rho = targetPolicy.pi(stepData.a_t) / behaviourPolicy.pi(stepData.a_t);
      PVector phi_tp1 = agentState.currentFeatureState();
      gtd.update(rho, phi_t, phi_tp1, stepData.r_tp1);
      if (stepData.s_tp1 == null) {
        nbEpisode += 1;
        if (nbEpisode > nbEpisodeMax)
          return new TestingResult<OffPolicyTD>(false, "Not learning fast enough. Distance to solution: "
              + FiniteStateGraphOnPolicy.distanceToSolution(solution, gtd.weights()), gtd);
        if (!Vectors.checkValues(gtd.weights()))
          return new TestingResult<OffPolicyTD>(false, "Weights are wrong", gtd);
      }
      phi_t = stepData.s_tp1 != null ? phi_tp1.copy() : null;
    }
    return new TestingResult<OffPolicyTD>(true, null, gtd);
  }
}
