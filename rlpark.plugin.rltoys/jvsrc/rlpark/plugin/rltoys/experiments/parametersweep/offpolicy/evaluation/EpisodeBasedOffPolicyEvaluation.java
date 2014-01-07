package rlpark.plugin.rltoys.experiments.parametersweep.offpolicy.evaluation;

import rlpark.plugin.rltoys.agents.offpolicy.OffPolicyAgentEvaluable;
import rlpark.plugin.rltoys.agents.representations.RepresentationFactory;
import rlpark.plugin.rltoys.envio.rl.RLAgent;
import rlpark.plugin.rltoys.experiments.parametersweep.interfaces.PerformanceEvaluator;
import rlpark.plugin.rltoys.experiments.parametersweep.offpolicy.internal.OffPolicyRewardMonitor;
import rlpark.plugin.rltoys.experiments.parametersweep.parameters.Parameters;
import rlpark.plugin.rltoys.experiments.parametersweep.reinforcementlearning.OffPolicyProblemFactory;
import rlpark.plugin.rltoys.experiments.parametersweep.reinforcementlearning.RLParameters;
import rlpark.plugin.rltoys.experiments.runners.AbstractRunner;
import rlpark.plugin.rltoys.experiments.runners.Runner;
import rlpark.plugin.rltoys.problems.RLProblem;
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
  public PerformanceEvaluator connectEvaluator(final int counter, Runner behaviourRunner,
      final OffPolicyProblemFactory problemFactory, final RepresentationFactory projectorFactory,
      final OffPolicyAgentEvaluable learningAgent, final Parameters parameters) {
    RLProblem problem = createEvaluationProblem(counter, problemFactory);
    RLAgent evaluatedAgent = learningAgent.createEvaluatedAgent();
    Runner runner = new Runner(problem, evaluatedAgent, Integer.MAX_VALUE, maxTimeStepsPerEpisode);
    final int nbEpisode = RLParameters.nbEpisode(parameters);
    final OffPolicyRewardMonitor rewardMonitor = new OffPolicyRewardMonitor(runner, nbRewardCheckpoint,
                                                                                          nbEpisode,
                                                                                          nbEpisodePerEvaluation);
    rewardMonitor.runEvaluationIFN(0);
    behaviourRunner.onEpisodeEnd.connect(new Listener<AbstractRunner.RunnerEvent>() {
      @Override
      public void listen(AbstractRunner.RunnerEvent eventInfo) {
        rewardMonitor.runEvaluationIFN(eventInfo.nbEpisodeDone);
      }
    });
    return rewardMonitor;
  }
}
