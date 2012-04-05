package rlpark.plugin.rltoys.experiments.parametersweep.offpolicy.evaluation;

import rlpark.plugin.rltoys.experiments.helpers.ExperimentCounter;
import rlpark.plugin.rltoys.experiments.parametersweep.reinforcementlearning.OffPolicyProblemFactory;
import rlpark.plugin.rltoys.problems.RLProblem;

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
