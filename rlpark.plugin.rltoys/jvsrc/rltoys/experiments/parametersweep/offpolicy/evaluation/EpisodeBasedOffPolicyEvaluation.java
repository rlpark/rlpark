package rltoys.experiments.parametersweep.offpolicy.evaluation;

import rltoys.environments.envio.OffPolicyLearner;
import rltoys.environments.envio.RLAgent;
import rltoys.environments.envio.Runner;
import rltoys.environments.envio.Runner.RunnerEvent;
import rltoys.environments.envio.problems.RLProblem;
import rltoys.experiments.parametersweep.offpolicy.internal.OffPolicyEpisodeRewardMonitor;
import rltoys.experiments.parametersweep.parameters.Parameters;
import rltoys.experiments.parametersweep.reinforcementlearning.AgentEvaluator;
import rltoys.experiments.parametersweep.reinforcementlearning.OffPolicyProblemFactory;
import rltoys.experiments.parametersweep.reinforcementlearning.ProjectorFactory;
import zephyr.plugin.core.api.signals.Listener;

public class EpisodeBasedOffPolicyEvaluation extends AbstractOffPolicyEvaluation {
  private static final long serialVersionUID = -654783411988105997L;
  private final int maxTimeStepsPerEpisode;
  private final int nbEpisodePerEvaluation;

  public EpisodeBasedOffPolicyEvaluation(int nbRewardCheckpoint, int maxTimeStepsPerEpisode, int nbEpisodePerEvaluation) {
    super(nbRewardCheckpoint);
    this.maxTimeStepsPerEpisode = maxTimeStepsPerEpisode;
    this.nbEpisodePerEvaluation = nbEpisodePerEvaluation;
  }

  @Override
  public AgentEvaluator connectEvaluator(final int counter, Runner behaviourRunner,
      final OffPolicyProblemFactory problemFactory, final ProjectorFactory projectorFactory,
      final OffPolicyLearner learner, final Parameters parameters) {
    RLProblem problem = createEvaluationProblem(counter, problemFactory);
    RLAgent agent = createEvaluatedAgent(problem, projectorFactory, learner);
    Runner runner = new Runner(problem, agent, Integer.MAX_VALUE, maxTimeStepsPerEpisode);
    final OffPolicyEpisodeRewardMonitor rewardMonitor = new OffPolicyEpisodeRewardMonitor(runner, nbRewardCheckpoint,
                                                                                          parameters.nbEpisode(),
                                                                                          nbEpisodePerEvaluation);
    rewardMonitor.runEvaluationIFN(0);
    behaviourRunner.onEpisodeEnd.connect(new Listener<Runner.RunnerEvent>() {
      @Override
      public void listen(RunnerEvent eventInfo) {
        rewardMonitor.runEvaluationIFN(eventInfo.episode);
      }
    });
    return rewardMonitor;
  }
}
