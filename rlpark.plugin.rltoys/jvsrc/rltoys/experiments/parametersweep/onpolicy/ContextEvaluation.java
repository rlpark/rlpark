package rltoys.experiments.parametersweep.onpolicy;

import rltoys.experiments.ExperimentCounter;
import rltoys.experiments.parametersweep.onpolicy.internal.OnPolicyEvaluationContext;
import rltoys.experiments.parametersweep.onpolicy.internal.OnPolicyRewardMonitor;
import rltoys.experiments.parametersweep.onpolicy.internal.RewardMonitorAverage;
import rltoys.experiments.parametersweep.onpolicy.internal.RewardMonitorEpisode;
import rltoys.experiments.parametersweep.onpolicy.internal.SweepJob;
import rltoys.experiments.parametersweep.parameters.Parameters;
import rltoys.experiments.parametersweep.reinforcementlearning.AgentFactory;
import rltoys.experiments.parametersweep.reinforcementlearning.ProblemFactory;
import rltoys.experiments.parametersweep.reinforcementlearning.RLParameters;

public class ContextEvaluation extends AbstractContextOnPolicy implements OnPolicyEvaluationContext {
  private static final long serialVersionUID = -5926779335932073094L;
  private final int nbRewardCheckpoint;

  public ContextEvaluation(ProblemFactory environmentFactory, AgentFactory agentFactory, int nbRewardCheckpoint) {
    super(environmentFactory, agentFactory);
    this.nbRewardCheckpoint = nbRewardCheckpoint;
  }

  @Override
  public Runnable createJob(Parameters parameters, ExperimentCounter counter) {
    return new SweepJob(this, parameters, counter);
  }

  private OnPolicyRewardMonitor createRewardMonitor(String prefix, int nbBins, Parameters parameters) {
    int nbEpisode = parameters.nbEpisode();
    int maxEpisodeTimeSteps = parameters.maxEpisodeTimeSteps();
    if (nbEpisode == 1 || parameters.hasFlag(RLParameters.OnPolicyTimeStepsEvaluationFlag))
      return new RewardMonitorAverage(prefix, nbBins, maxEpisodeTimeSteps);
    return new RewardMonitorEpisode(prefix, nbBins, nbEpisode);
  }


  @Override
  public OnPolicyRewardMonitor createRewardMonitor(Parameters parameters) {
    return createRewardMonitor("", nbRewardCheckpoint, parameters);
  }
}
