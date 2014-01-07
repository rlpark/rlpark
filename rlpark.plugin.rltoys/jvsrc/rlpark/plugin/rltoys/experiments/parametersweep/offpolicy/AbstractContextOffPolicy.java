package rlpark.plugin.rltoys.experiments.parametersweep.offpolicy;

import rlpark.plugin.rltoys.agents.offpolicy.OffPolicyAgentEvaluable;
import rlpark.plugin.rltoys.agents.representations.RepresentationFactory;
import rlpark.plugin.rltoys.experiments.helpers.ExperimentCounter;
import rlpark.plugin.rltoys.experiments.parametersweep.interfaces.PerformanceEvaluator;
import rlpark.plugin.rltoys.experiments.parametersweep.offpolicy.evaluation.OffPolicyEvaluation;
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
import rlpark.plugin.rltoys.experiments.runners.Runner;

public abstract class AbstractContextOffPolicy implements OffPolicyEvaluationContext {
  private static final long serialVersionUID = -6212106048889219995L;
  protected final OffPolicyAgentFactory agentFactory;
  protected final OffPolicyProblemFactory environmentFactory;
  protected final OffPolicyEvaluation evaluation;
  protected final RepresentationFactory projectorFactory;

  public AbstractContextOffPolicy(OffPolicyProblemFactory environmentFactory, RepresentationFactory projectorFactory,
      OffPolicyAgentFactory agentFactory, OffPolicyEvaluation evaluation) {
    this.evaluation = evaluation;
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

  public Parameters contextParameters() {
    RunInfo infos = new RunInfo();
    infos.enableFlag(agentFactory.label());
    infos.enableFlag(environmentFactory.label());
    Parameters parameters = new Parameters(infos);
    environmentFactory.setExperimentParameters(parameters);
    return parameters;
  }

  private OnPolicyRewardMonitor createRewardMonitor(String prefix, int nbBins, Parameters parameters) {
    int nbEpisode = RLParameters.nbEpisode(parameters);
    int maxEpisodeTimeSteps = RLParameters.maxEpisodeTimeSteps(parameters);
    if (nbEpisode == 1)
      return new RewardMonitorAverage(prefix, nbBins, maxEpisodeTimeSteps);
    return new RewardMonitorEpisode(prefix, nbBins, nbEpisode);
  }

  @Override
  public PerformanceEvaluator connectBehaviourRewardMonitor(Runner runner, Parameters parameters) {
    OnPolicyRewardMonitor monitor = createRewardMonitor("Behaviour", evaluation.nbRewardCheckpoint(), parameters);
    monitor.connect(runner);
    return monitor;
  }

  @Override
  public PerformanceEvaluator connectTargetRewardMonitor(int counter, Runner runner, Parameters parameters) {
    OffPolicyAgentEvaluable agent = (OffPolicyAgentEvaluable) runner.agent();
    return evaluation.connectEvaluator(counter, runner, environmentFactory, projectorFactory, agent, parameters);
  }

  @Override
  public Runnable createJob(Parameters parameters, ExperimentCounter counter) {
    return new SweepJob(this, parameters, counter);
  }
}
