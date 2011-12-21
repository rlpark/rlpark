package rltoys.experiments.parametersweep.reinforcementlearning;

import rltoys.algorithms.representations.Projector;
import rltoys.algorithms.representations.actions.Action;
import rltoys.environments.envio.OffPolicyLearner;
import rltoys.environments.envio.RLAgent;
import rltoys.environments.envio.observations.TRStep;

public class EvaluatedOffPolicyLearner implements RLAgent {
  private static final long serialVersionUID = 1863728076381568361L;
  private final OffPolicyLearner learner;
  private final Projector projector;

  public EvaluatedOffPolicyLearner(Projector projector, OffPolicyLearner learner) {
    this.projector = projector;
    this.learner = learner;
  }

  @Override
  public Action getAtp1(TRStep step) {
    if (step.isEpisodeEnding())
      return null;
    return learner.proposeAction(projector.project(step.o_tp1));
  }
}