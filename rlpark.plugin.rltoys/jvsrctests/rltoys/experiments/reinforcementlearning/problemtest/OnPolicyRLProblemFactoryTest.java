package rltoys.experiments.reinforcementlearning.problemtest;

import java.util.Random;

import rltoys.environments.envio.problems.RLProblem;

@SuppressWarnings("serial")
public class OnPolicyRLProblemFactoryTest extends AbstractRLProblemFactoryTest {
  public OnPolicyRLProblemFactoryTest(int nbEpisode, int nbTimeSteps) {
    super(nbEpisode, nbTimeSteps);
  }

  @Override
  public RLProblem createEnvironment(Random random) {
    return new TestRLProblemForSweep(null);
  }
}