package rltoys.experiments.parametersweep.offpolicy.evaluation;

import rltoys.environments.envio.problems.RLProblem;
import rltoys.experiments.ExperimentCounter;
import rltoys.experiments.parametersweep.reinforcementlearning.OffPolicyProblemFactory;

public abstract class AbstractOffPolicyEvaluation implements OffPolicyEvaluation {
  private static final long serialVersionUID = -4691992115680346327L;
  protected final int nbRewardCheckpoint;

  protected AbstractOffPolicyEvaluation(int nbRewardCheckpoint) {
    this.nbRewardCheckpoint = nbRewardCheckpoint;
  }

  protected RLProblem createEvaluationProblem(int counter, OffPolicyProblemFactory problemFactory) {
    return problemFactory.createEvaluationEnvironment(ExperimentCounter.newRandom(counter));
  }

  @Override
  public int nbRewardCheckpoint() {
    return nbRewardCheckpoint;
  }
}
