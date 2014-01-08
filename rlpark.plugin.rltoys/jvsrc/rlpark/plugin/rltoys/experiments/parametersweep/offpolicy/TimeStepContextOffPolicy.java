package rlpark.plugin.rltoys.experiments.parametersweep.offpolicy;

import rlpark.plugin.rltoys.agents.offpolicy.OffPolicyAgent;
import rlpark.plugin.rltoys.agents.offpolicy.OffPolicyAgentEvaluable;
import rlpark.plugin.rltoys.agents.representations.RepresentationFactory;
import rlpark.plugin.rltoys.envio.rl.RLAgent;
import rlpark.plugin.rltoys.experiments.helpers.ExperimentCounter;
import rlpark.plugin.rltoys.experiments.parametersweep.interfaces.PerformanceEvaluator;
import rlpark.plugin.rltoys.experiments.parametersweep.offpolicy.internal.OffPolicyRewardMonitor;
import rlpark.plugin.rltoys.experiments.parametersweep.onpolicy.internal.RewardMonitorAverage;
import rlpark.plugin.rltoys.experiments.parametersweep.parameters.Parameters;
import rlpark.plugin.rltoys.experiments.parametersweep.reinforcementlearning.OffPolicyAgentFactory;
import rlpark.plugin.rltoys.experiments.parametersweep.reinforcementlearning.OffPolicyProblemFactory;
import rlpark.plugin.rltoys.experiments.parametersweep.reinforcementlearning.RLParameters;
import rlpark.plugin.rltoys.experiments.runners.AbstractRunner;
import rlpark.plugin.rltoys.experiments.runners.Runner;
import rlpark.plugin.rltoys.experiments.runners.RunnerTimeSteps;
import rlpark.plugin.rltoys.problems.RLProblem;
import zephyr.plugin.core.api.signals.Listener;

public class TimeStepContextOffPolicy extends AbstractContextOffPolicy {
  private static final long serialVersionUID = -593900122821568271L;

  public TimeStepContextOffPolicy() {
    super(null, null, null);
  }

  private TimeStepContextOffPolicy(OffPolicyProblemFactory environmentFactory, RepresentationFactory projectorFactory,
      OffPolicyAgentFactory agentFactory) {
    super(environmentFactory, projectorFactory, agentFactory);
  }

  @Override
  public PerformanceEvaluator connectBehaviourRewardMonitor(AbstractRunner runner, Parameters parameters) {
    int nbBins = RLParameters.nbRewardCheckpoint(parameters);
    int totalNumberOfTimeSteps = RLParameters.totalNumberOfTimeSteps(parameters);
    RewardMonitorAverage monitor = new RewardMonitorAverage(BehaviourPrefix, nbBins, totalNumberOfTimeSteps);
    monitor.connect(runner);
    return monitor;
  }

  @Override
  public PerformanceEvaluator connectTargetRewardMonitor(int counter, AbstractRunner behaviourRunner,
      Parameters parameters) {
    OffPolicyAgentEvaluable learningAgent = (OffPolicyAgentEvaluable) behaviourRunner.agent();
    RLProblem problem = environmentFactory.createEvaluationEnvironment(ExperimentCounter.newRandom(counter));
    RLAgent evaluatedAgent = learningAgent.createEvaluatedAgent();
    Runner runner = new Runner(problem, evaluatedAgent, Integer.MAX_VALUE, RLParameters.maxEpisodeTimeSteps(parameters));
    int nbRewardCheckpoint = RLParameters.nbRewardCheckpoint(parameters);
    int nbEpisodePerEvaluation = RLParameters.nbEpisodePerEvaluation(parameters);
    int nbTotalTimeSteps = RLParameters.totalNumberOfTimeSteps(parameters);
    final OffPolicyRewardMonitor rewardMonitor = new OffPolicyRewardMonitor(runner, nbRewardCheckpoint,
                                                                            nbTotalTimeSteps, nbEpisodePerEvaluation);
    rewardMonitor.runEvaluationIFN(0);
    behaviourRunner.onTimeStep.connect(new Listener<AbstractRunner.RunnerEvent>() {
      @Override
      public void listen(AbstractRunner.RunnerEvent eventInfo) {
        rewardMonitor.runEvaluationIFN(eventInfo.nbTotalTimeSteps);
      }
    });
    return rewardMonitor;
  }

  @Override
  public AbstractRunner createRunner(int seed, Parameters parameters) {
    RLProblem problem = environmentFactory.createEnvironment(ExperimentCounter.newRandom(seed));
    OffPolicyAgent agent = agentFactory.createAgent(seed, problem, parameters, projectorFactory);
    int totalNumberOfTimeSteps = RLParameters.totalNumberOfTimeSteps(parameters);
    int maxEpisodeTimeSteps = RLParameters.maxEpisodeTimeSteps(parameters);
    return new RunnerTimeSteps(problem, agent, maxEpisodeTimeSteps, totalNumberOfTimeSteps);
  }

  @Override
  public AbstractContextOffPolicy newContext(OffPolicyProblemFactory environmentFactory,
      RepresentationFactory projectorFactory, OffPolicyAgentFactory agentFactory) {
    return new TimeStepContextOffPolicy(environmentFactory, projectorFactory, agentFactory);
  }
}
