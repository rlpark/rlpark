package rlpark.plugin.rltoys.experiments.parametersweep.onpolicy;

import rlpark.plugin.rltoys.agents.representations.RepresentationFactory;
import rlpark.plugin.rltoys.experiments.helpers.ExperimentCounter;
import rlpark.plugin.rltoys.experiments.parametersweep.onpolicy.internal.LearningCurveJob;
import rlpark.plugin.rltoys.experiments.parametersweep.parameters.Parameters;
import rlpark.plugin.rltoys.experiments.parametersweep.reinforcementlearning.AgentFactory;
import rlpark.plugin.rltoys.experiments.parametersweep.reinforcementlearning.ProblemFactory;

public class ContextLearningCurve extends AbstractContextOnPolicy {
  private static final long serialVersionUID = -5926779335932073094L;

  public ContextLearningCurve(ProblemFactory environmentFactory, AgentFactory agentFactory, RepresentationFactory representationFactory) {
    super(environmentFactory, representationFactory, agentFactory);
  }

  @Override
  public LearningCurveJob createJob(Parameters parameters, ExperimentCounter counter) {
    return new LearningCurveJob(this, parameters, counter);
  }
}
