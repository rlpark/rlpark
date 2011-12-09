package rltoys.experiments.parametersweep.offpolicy.evaluation;

import rltoys.algorithms.representations.Projector;
import rltoys.environments.envio.OffPolicyLearner;
import rltoys.environments.envio.RLAgent;
import rltoys.environments.envio.problems.RLProblem;
import rltoys.experiments.ExperimentCounter;
import rltoys.experiments.parametersweep.reinforcementlearning.OffPolicyProblemFactory;
import rltoys.experiments.parametersweep.reinforcementlearning.ProjectorFactory;
import rltoys.experiments.parametersweep.reinforcementlearning.internal.EvaluatedOffPolicyLearner;

public abstract class AbstractOffPolicyEvaluation implements OffPolicyEvaluation {
  private static final long serialVersionUID = -4691992115680346327L;
  protected final int nbRewardCheckpoint;

  protected AbstractOffPolicyEvaluation(int nbRewardCheckpoint) {
    this.nbRewardCheckpoint = nbRewardCheckpoint;
  }

  protected RLAgent createEvaluatedAgent(RLProblem problem, ProjectorFactory projectorFactory, OffPolicyLearner learner) {
    Projector projector = projectorFactory.createProjector(problem);
    RLAgent agent = new EvaluatedOffPolicyLearner(projector, learner);
    return agent;
  }

  protected RLProblem createEvaluationProblem(int counter, OffPolicyProblemFactory problemFactory) {
    return problemFactory.createEvaluationEnvironment(ExperimentCounter.newRandom(counter));
  }

  @Override
  public int nbRewardCheckpoint() {
    return nbRewardCheckpoint;
  }
}
