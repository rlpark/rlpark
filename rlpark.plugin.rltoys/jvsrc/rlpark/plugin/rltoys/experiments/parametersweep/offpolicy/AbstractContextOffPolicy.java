package rlpark.plugin.rltoys.experiments.parametersweep.offpolicy;

import rlpark.plugin.rltoys.agents.representations.RepresentationFactory;
import rlpark.plugin.rltoys.experiments.helpers.ExperimentCounter;
import rlpark.plugin.rltoys.experiments.parametersweep.interfaces.PerformanceEvaluator;
import rlpark.plugin.rltoys.experiments.parametersweep.offpolicy.internal.OffPolicyEvaluationContext;
import rlpark.plugin.rltoys.experiments.parametersweep.offpolicy.internal.SweepJob;
import rlpark.plugin.rltoys.experiments.parametersweep.onpolicy.internal.OnPolicyRewardMonitor;
import rlpark.plugin.rltoys.experiments.parametersweep.onpolicy.internal.RewardMonitorAverage;
import rlpark.plugin.rltoys.experiments.parametersweep.onpolicy.internal.RewardMonitorEpisode;
import rlpark.plugin.rltoys.experiments.parametersweep.parameters.Parameters;
import rlpark.plugin.rltoys.experiments.parametersweep.parameters.RunInfo;
import rlpark.plugin.rltoys.experiments.parametersweep.reinforcementlearning.OffPolicyAgentFactory;
import rlpark.plugin.rltoys.experiments.parametersweep.reinforcementlearning.OffPolicyProblemFactory;
import rlpark.plugin.rltoys.experiments.parametersweep.reinforcementlearning.ProblemFactory;
import rlpark.plugin.rltoys.experiments.parametersweep.reinforcementlearning.RLParameters;
import rlpark.plugin.rltoys.experiments.runners.AbstractRunner;

public abstract class AbstractContextOffPolicy implements OffPolicyEvaluationContext {
  private static final long serialVersionUID = -6212106048889219995L;
  protected static final String BehaviourPrefix = "Behaviour";
  protected final OffPolicyAgentFactory agentFactory;
  protected final OffPolicyProblemFactory environmentFactory;
  protected final RepresentationFactory projectorFactory;

  protected AbstractContextOffPolicy(OffPolicyProblemFactory environmentFactory,
      RepresentationFactory projectorFactory, OffPolicyAgentFactory agentFactory) {
    this.projectorFactory = projectorFactory;
    this.environmentFactory = environmentFactory;
    this.agentFactory = agentFactory;
  }

  @Override
  public String fileName() {
    return ExperimentCounter.DefaultFileName;
  }

  @Override
  public String folderPath() {
    return environmentFactory.label() + "/" + agentFactory.label();
  }

  public OffPolicyAgentFactory agentFactory() {
    return agentFactory;
  }

  public ProblemFactory problemFactory() {
    return environmentFactory;
  }

  public Parameters contextParameters(RunInfo parentInfos) {
    RunInfo infos = parentInfos.clone();
    infos.enableFlag(agentFactory.label());
    infos.enableFlag(environmentFactory.label());
    Parameters parameters = new Parameters(infos);
    environmentFactory.setExperimentParameters(parameters);
    return parameters;
  }

  @Override
  public PerformanceEvaluator connectBehaviourRewardMonitor(AbstractRunner runner, Parameters parameters) {
    int nbEpisode = RLParameters.nbEpisode(parameters);
    int maxEpisodeTimeSteps = RLParameters.maxEpisodeTimeSteps(parameters);
    int nbBins = RLParameters.nbRewardCheckpoint(parameters);
    OnPolicyRewardMonitor monitor = null;
    if (nbEpisode == 1)
      monitor = new RewardMonitorAverage(BehaviourPrefix, nbBins, maxEpisodeTimeSteps);
    else
      monitor = new RewardMonitorEpisode(BehaviourPrefix, nbBins, nbEpisode);
    monitor.connect(runner);
    return monitor;
  }

  @Override
  public Runnable createJob(Parameters parameters, ExperimentCounter counter) {
    return new SweepJob(this, parameters, counter);
  }

  abstract public AbstractContextOffPolicy newContext(OffPolicyProblemFactory environmentFactory,
      RepresentationFactory projectorFactory, OffPolicyAgentFactory agentFactory);
}
