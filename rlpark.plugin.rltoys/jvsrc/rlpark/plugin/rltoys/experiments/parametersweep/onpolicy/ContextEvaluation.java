package rlpark.plugin.rltoys.experiments.parametersweep.onpolicy;

import rlpark.plugin.rltoys.agents.representations.RepresentationFactory;
import rlpark.plugin.rltoys.experiments.helpers.ExperimentCounter;
import rlpark.plugin.rltoys.experiments.parametersweep.onpolicy.internal.OnPolicyEvaluationContext;
import rlpark.plugin.rltoys.experiments.parametersweep.onpolicy.internal.OnPolicyRewardMonitor;
import rlpark.plugin.rltoys.experiments.parametersweep.onpolicy.internal.RewardMonitorAverage;
import rlpark.plugin.rltoys.experiments.parametersweep.onpolicy.internal.RewardMonitorEpisode;
import rlpark.plugin.rltoys.experiments.parametersweep.onpolicy.internal.SweepJob;
import rlpark.plugin.rltoys.experiments.parametersweep.parameters.Parameters;
import rlpark.plugin.rltoys.experiments.parametersweep.reinforcementlearning.AgentFactory;
import rlpark.plugin.rltoys.experiments.parametersweep.reinforcementlearning.ProblemFactory;
import rlpark.plugin.rltoys.experiments.parametersweep.reinforcementlearning.RLParameters;

public class ContextEvaluation extends AbstractContextOnPolicy implements OnPolicyEvaluationContext {
  private static final long serialVersionUID = -5926779335932073094L;
  private final int nbRewardCheckpoint;

  public ContextEvaluation(ProblemFactory environmentFactory, RepresentationFactory representationFactory,
      AgentFactory agentFactory, int nbRewardCheckpoint) {
    super(environmentFactory, representationFactory, agentFactory);
    this.nbRewardCheckpoint = nbRewardCheckpoint;
  }

  @Override
  public Runnable createJob(Parameters parameters, ExperimentCounter counter) {
    return new SweepJob(this, parameters, counter);
  }

  private OnPolicyRewardMonitor createRewardMonitor(String prefix, int nbBins, Parameters parameters) {
    int nbEpisode = RLParameters.nbEpisode(parameters);
    int maxEpisodeTimeSteps = RLParameters.maxEpisodeTimeSteps(parameters);
    if (nbEpisode == 1 || parameters.hasFlag(RLParameters.OnPolicyTimeStepsEvaluationFlag))
      return new RewardMonitorAverage(prefix, nbBins, maxEpisodeTimeSteps);
    return new RewardMonitorEpisode(prefix, nbBins, nbEpisode);
  }


  @Override
  public OnPolicyRewardMonitor createRewardMonitor(Parameters parameters) {
    return createRewardMonitor("", nbRewardCheckpoint, parameters);
  }
}
