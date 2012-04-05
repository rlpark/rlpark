package rlpark.plugin.rltoys.experiments.parametersweep.offpolicy;

import rlpark.plugin.rltoys.agents.offpolicy.OffPolicyAgentEvaluable;
import rlpark.plugin.rltoys.agents.representations.RepresentationFactory;
import rlpark.plugin.rltoys.experiments.helpers.ExperimentCounter;
import rlpark.plugin.rltoys.experiments.helpers.Runner;
import rlpark.plugin.rltoys.experiments.parametersweep.offpolicy.evaluation.OffPolicyEvaluation;
import rlpark.plugin.rltoys.experiments.parametersweep.offpolicy.internal.OffPolicyEvaluationContext;
import rlpark.plugin.rltoys.experiments.parametersweep.offpolicy.internal.SweepJob;
import rlpark.plugin.rltoys.experiments.parametersweep.onpolicy.internal.OnPolicyRewardMonitor;
import rlpark.plugin.rltoys.experiments.parametersweep.onpolicy.internal.RewardMonitorAverage;
import rlpark.plugin.rltoys.experiments.parametersweep.onpolicy.internal.RewardMonitorEpisode;
import rlpark.plugin.rltoys.experiments.parametersweep.parameters.Parameters;
import rlpark.plugin.rltoys.experiments.parametersweep.reinforcementlearning.AgentEvaluator;
import rlpark.plugin.rltoys.experiments.parametersweep.reinforcementlearning.OffPolicyAgentFactory;
import rlpark.plugin.rltoys.experiments.parametersweep.reinforcementlearning.OffPolicyProblemFactory;

public class ContextEvaluation extends AbstractContextOffPolicy implements OffPolicyEvaluationContext {
  private static final long serialVersionUID = -593900122821568271L;

  public ContextEvaluation(OffPolicyProblemFactory environmentFactory, RepresentationFactory projectorFactory,
      OffPolicyAgentFactory agentFactory, OffPolicyEvaluation evaluation) {
    super(environmentFactory, projectorFactory, agentFactory, evaluation);
  }

  @Override
  public Runnable createJob(Parameters parameters, ExperimentCounter counter) {
    return new SweepJob(this, parameters, counter);
  }

  private OnPolicyRewardMonitor createRewardMonitor(String prefix, int nbBins, Parameters parameters) {
    int nbEpisode = parameters.nbEpisode();
    int maxEpisodeTimeSteps = parameters.maxEpisodeTimeSteps();
    if (nbEpisode == 1)
      return new RewardMonitorAverage(prefix, nbBins, maxEpisodeTimeSteps);
    return new RewardMonitorEpisode(prefix, nbBins, nbEpisode);
  }

  @Override
  public AgentEvaluator connectBehaviourRewardMonitor(Runner runner, Parameters parameters) {
    OnPolicyRewardMonitor monitor = createRewardMonitor("Behaviour", evaluation.nbRewardCheckpoint(), parameters);
    monitor.connect(runner);
    return monitor;
  }

  @Override
  public AgentEvaluator connectTargetRewardMonitor(int counter, Runner runner, Parameters parameters) {
    OffPolicyAgentEvaluable agent = (OffPolicyAgentEvaluable) runner.agent();
    return evaluation.connectEvaluator(counter, runner, environmentFactory, projectorFactory, agent, parameters);
  }
}
