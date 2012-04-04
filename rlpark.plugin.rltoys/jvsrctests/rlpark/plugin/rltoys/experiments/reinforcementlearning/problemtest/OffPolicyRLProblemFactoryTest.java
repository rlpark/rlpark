package rlpark.plugin.rltoys.experiments.reinforcementlearning.problemtest;

import java.util.Random;

import rlpark.plugin.rltoys.envio.problems.RLProblem;
import rlpark.plugin.rltoys.experiments.parametersweep.reinforcementlearning.OffPolicyProblemFactory;

@SuppressWarnings("serial")
public class OffPolicyRLProblemFactoryTest extends AbstractRLProblemFactoryTest implements OffPolicyProblemFactory {
  public OffPolicyRLProblemFactoryTest(int nbEpisode, int nbTimeSteps) {
    super(nbEpisode, nbTimeSteps);
  }

  @Override
  public RLProblem createEnvironment(Random random) {
    return new TestRLProblemForSweep(5.0);
  }

  @Override
  public RLProblem createEvaluationEnvironment(Random random) {
    return new TestRLProblemForSweep(null);
  }
}