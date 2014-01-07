package rlpark.plugin.rltoys.experiments.parametersweep.offpolicy;

import rlpark.plugin.rltoys.agents.offpolicy.OffPolicyAgent;
import rlpark.plugin.rltoys.agents.offpolicy.OffPolicyAgentEvaluable;
import rlpark.plugin.rltoys.agents.representations.RepresentationFactory;
import rlpark.plugin.rltoys.experiments.helpers.ExperimentCounter;
import rlpark.plugin.rltoys.experiments.parametersweep.interfaces.PerformanceEvaluator;
import rlpark.plugin.rltoys.experiments.parametersweep.offpolicy.evaluation.OffPolicyEvaluation;
import rlpark.plugin.rltoys.experiments.parametersweep.parameters.Parameters;
import rlpark.plugin.rltoys.experiments.parametersweep.reinforcementlearning.OffPolicyAgentFactory;
import rlpark.plugin.rltoys.experiments.parametersweep.reinforcementlearning.OffPolicyProblemFactory;
import rlpark.plugin.rltoys.experiments.parametersweep.reinforcementlearning.RLParameters;
import rlpark.plugin.rltoys.experiments.runners.AbstractRunner;
import rlpark.plugin.rltoys.experiments.runners.Runner;
import rlpark.plugin.rltoys.problems.RLProblem;

public class EpisodeContextOffPolicy extends AbstractContextOffPolicy {
  private static final long serialVersionUID = -593900122821568271L;

  public EpisodeContextOffPolicy(OffPolicyProblemFactory environmentFactory, RepresentationFactory projectorFactory,
      OffPolicyAgentFactory agentFactory, OffPolicyEvaluation evaluation) {
    super(environmentFactory, projectorFactory, agentFactory, evaluation);
  }

  @Override
  public PerformanceEvaluator connectTargetRewardMonitor(int counter, AbstractRunner runner, Parameters parameters) {
    OffPolicyAgentEvaluable agent = (OffPolicyAgentEvaluable) runner.agent();
    return evaluation.connectEvaluator(counter, runner, runner.onEpisodeEnd, environmentFactory, projectorFactory,
                                       agent, parameters);
  }

  @Override
  public AbstractRunner createRunner(int seed, Parameters parameters) {
    RLProblem problem = environmentFactory.createEnvironment(ExperimentCounter.newRandom(seed));
    OffPolicyAgent agent = agentFactory.createAgent(seed, problem, parameters, projectorFactory);
    int nbEpisode = RLParameters.nbEpisode(parameters);
    int maxEpisodeTimeSteps = RLParameters.maxEpisodeTimeSteps(parameters);
    return new Runner(problem, agent, nbEpisode, maxEpisodeTimeSteps);
  }
}
