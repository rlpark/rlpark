package rlpark.plugin.rltoys.experiments.parametersweep.offpolicy.evaluation;

import rlpark.plugin.rltoys.agents.offpolicy.OffPolicyAgentEvaluable;
import rlpark.plugin.rltoys.agents.representations.RepresentationFactory;
import rlpark.plugin.rltoys.envio.rl.RLAgent;
import rlpark.plugin.rltoys.experiments.parametersweep.interfaces.PerformanceEvaluator;
import rlpark.plugin.rltoys.experiments.parametersweep.onpolicy.internal.OnPolicyRewardMonitor;
import rlpark.plugin.rltoys.experiments.parametersweep.onpolicy.internal.RewardMonitorAverage;
import rlpark.plugin.rltoys.experiments.parametersweep.onpolicy.internal.RewardMonitorEpisode;
import rlpark.plugin.rltoys.experiments.parametersweep.parameters.Parameters;
import rlpark.plugin.rltoys.experiments.parametersweep.reinforcementlearning.OffPolicyProblemFactory;
import rlpark.plugin.rltoys.experiments.parametersweep.reinforcementlearning.RLParameters;
import rlpark.plugin.rltoys.experiments.runners.AbstractRunner;
import rlpark.plugin.rltoys.experiments.runners.AbstractRunner.RunnerEvent;
import rlpark.plugin.rltoys.experiments.runners.Runner;
import rlpark.plugin.rltoys.problems.RLProblem;
import zephyr.plugin.core.api.signals.Listener;
import zephyr.plugin.core.api.signals.Signal;

public class ContinuousOffPolicyEvaluation extends AbstractOffPolicyEvaluation {
  private static final long serialVersionUID = -654783411988105997L;
  private final int resetPeriod;

  public ContinuousOffPolicyEvaluation(int nbRewardCheckpoint) {
    this(nbRewardCheckpoint, -1);
  }

  public ContinuousOffPolicyEvaluation(int nbRewardCheckpoint, int resetPeriod) {
    super(nbRewardCheckpoint);
    this.resetPeriod = resetPeriod;
  }

  private OnPolicyRewardMonitor createRewardMonitor(String prefix, int nbBins, int nbTimeSteps, int nbEpisode) {
    if (nbEpisode == 1)
      return new RewardMonitorAverage(prefix, nbBins, nbTimeSteps);
    return new RewardMonitorEpisode(prefix, nbBins, nbEpisode);
  }

  @Override
  public PerformanceEvaluator connectEvaluator(int counter, AbstractRunner behaviourRunner, Signal<RunnerEvent> signal,
      OffPolicyProblemFactory problemFactory, RepresentationFactory projectorFactory,
      OffPolicyAgentEvaluable learningAgent, Parameters parameters) {
    if (RLParameters.nbEpisode(parameters) != 1)
      throw new RuntimeException("This evaluation does not support multiple episode for the behaviour");
    RLProblem problem = createEvaluationProblem(counter, problemFactory);
    RLAgent evaluatedAgent = learningAgent.createEvaluatedAgent();
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
}
