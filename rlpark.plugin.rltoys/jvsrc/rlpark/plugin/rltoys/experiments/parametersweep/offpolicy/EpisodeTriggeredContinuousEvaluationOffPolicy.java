package rlpark.plugin.rltoys.experiments.parametersweep.offpolicy;

import rlpark.plugin.rltoys.agents.offpolicy.OffPolicyAgent;
import rlpark.plugin.rltoys.agents.offpolicy.OffPolicyAgentEvaluable;
import rlpark.plugin.rltoys.agents.representations.RepresentationFactory;
import rlpark.plugin.rltoys.envio.rl.RLAgent;
import rlpark.plugin.rltoys.experiments.helpers.ExperimentCounter;
import rlpark.plugin.rltoys.experiments.parametersweep.interfaces.PerformanceEvaluator;
import rlpark.plugin.rltoys.experiments.parametersweep.onpolicy.internal.OnPolicyRewardMonitor;
import rlpark.plugin.rltoys.experiments.parametersweep.onpolicy.internal.RewardMonitorAverage;
import rlpark.plugin.rltoys.experiments.parametersweep.onpolicy.internal.RewardMonitorEpisode;
import rlpark.plugin.rltoys.experiments.parametersweep.parameters.Parameters;
import rlpark.plugin.rltoys.experiments.parametersweep.reinforcementlearning.OffPolicyAgentFactory;
import rlpark.plugin.rltoys.experiments.parametersweep.reinforcementlearning.OffPolicyProblemFactory;
import rlpark.plugin.rltoys.experiments.parametersweep.reinforcementlearning.RLParameters;
import rlpark.plugin.rltoys.experiments.runners.AbstractRunner;
import rlpark.plugin.rltoys.experiments.runners.Runner;
import rlpark.plugin.rltoys.problems.RLProblem;
import zephyr.plugin.core.api.signals.Listener;

public class EpisodeTriggeredContinuousEvaluationOffPolicy extends AbstractContextOffPolicy {
  private static final long serialVersionUID = -593900122821568271L;
  private final int resetPeriod;

  public EpisodeTriggeredContinuousEvaluationOffPolicy() {
    this(-1);
  }

  public EpisodeTriggeredContinuousEvaluationOffPolicy(int resetPeriod) {
    this(null, null, null, resetPeriod);
  }

  private EpisodeTriggeredContinuousEvaluationOffPolicy(OffPolicyProblemFactory environmentFactory,
      RepresentationFactory projectorFactory, OffPolicyAgentFactory agentFactory, int resetPeriod) {
    super(environmentFactory, projectorFactory, agentFactory);
    this.resetPeriod = resetPeriod;
  }

  private OnPolicyRewardMonitor createRewardMonitor(String prefix, int nbBins, int nbTimeSteps, int nbEpisode) {
    if (nbEpisode == 1)
      return new RewardMonitorAverage(prefix, nbBins, nbTimeSteps);
    return new RewardMonitorEpisode(prefix, nbBins, nbEpisode);
  }

  @Override
  public PerformanceEvaluator connectTargetRewardMonitor(int counter, AbstractRunner behaviourRunner,
      Parameters parameters) {
    OffPolicyAgentEvaluable learningAgent = (OffPolicyAgentEvaluable) behaviourRunner.agent();
    if (RLParameters.nbEpisode(parameters) != 1)
      throw new RuntimeException("This evaluation does not support multiple episode for the behaviour");
    RLProblem problem = environmentFactory.createEvaluationEnvironment(ExperimentCounter.newRandom(counter));
    RLAgent evaluatedAgent = learningAgent.createEvaluatedAgent();
    int nbRewardCheckpoint = RLParameters.nbRewardCheckpoint(parameters);
    int nbEpisode = resetPeriod > 0 ? RLParameters.maxEpisodeTimeSteps(parameters) / nbRewardCheckpoint : 1;
    int nbTimeSteps = resetPeriod > 0 ? resetPeriod : RLParameters.maxEpisodeTimeSteps(parameters);
    final Runner runner = new Runner(problem, evaluatedAgent, nbEpisode, resetPeriod);
    OnPolicyRewardMonitor monitor = createRewardMonitor("Target", nbRewardCheckpoint, nbTimeSteps, nbEpisode);
    monitor.connect(runner);
    behaviourRunner.onTimeStep.connect(new Listener<AbstractRunner.RunnerEvent>() {
      @Override
      public void listen(AbstractRunner.RunnerEvent eventInfo) {
        runner.step();
      }
    });
    return monitor;
  }

  @Override
  public AbstractRunner createRunner(int seed, Parameters parameters) {
    RLProblem problem = environmentFactory.createEnvironment(ExperimentCounter.newRandom(seed));
    OffPolicyAgent agent = agentFactory.createAgent(seed, problem, parameters, projectorFactory);
    int nbEpisode = RLParameters.nbEpisode(parameters);
    int maxEpisodeTimeSteps = RLParameters.maxEpisodeTimeSteps(parameters);
    return new Runner(problem, agent, nbEpisode, maxEpisodeTimeSteps);
  }

  @Override
  public AbstractContextOffPolicy newContext(OffPolicyProblemFactory environmentFactory,
      RepresentationFactory projectorFactory, OffPolicyAgentFactory agentFactory) {
    return new EpisodeTriggeredContinuousEvaluationOffPolicy(environmentFactory, projectorFactory, agentFactory,
                                                             resetPeriod);
  }
}
