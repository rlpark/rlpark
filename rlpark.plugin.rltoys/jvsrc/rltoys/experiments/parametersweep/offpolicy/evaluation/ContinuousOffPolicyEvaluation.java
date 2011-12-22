package rltoys.experiments.parametersweep.offpolicy.evaluation;

import rltoys.algorithms.representations.projectors.RepresentationFactory;
import rltoys.environments.envio.RLAgent;
import rltoys.environments.envio.Runner;
import rltoys.environments.envio.Runner.RunnerEvent;
import rltoys.environments.envio.offpolicy.OffPolicyAgentEvaluable;
import rltoys.environments.envio.problems.RLProblem;
import rltoys.experiments.parametersweep.onpolicy.internal.OnPolicyRewardMonitor;
import rltoys.experiments.parametersweep.onpolicy.internal.RewardMonitors;
import rltoys.experiments.parametersweep.parameters.Parameters;
import rltoys.experiments.parametersweep.reinforcementlearning.AgentEvaluator;
import rltoys.experiments.parametersweep.reinforcementlearning.OffPolicyProblemFactory;
import zephyr.plugin.core.api.signals.Listener;

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

  @Override
  public AgentEvaluator connectEvaluator(int counter, Runner behaviourRunner, OffPolicyProblemFactory problemFactory,
      RepresentationFactory projectorFactory, OffPolicyAgentEvaluable learningAgent, Parameters parameters) {
    if (parameters.nbEpisode() != 1)
      throw new RuntimeException("This evaluation does not support multiple episode for the behaviour");
    RLProblem problem = createEvaluationProblem(counter, problemFactory);
    RLAgent evaluatedAgent = learningAgent.createEvaluatedAgent();
    int nbEpisode = resetPeriod > 0 ? parameters.maxEpisodeTimeSteps() / nbRewardCheckpoint : 1;
    int nbTimeSteps = resetPeriod > 0 ? resetPeriod : parameters.maxEpisodeTimeSteps();
    final Runner runner = new Runner(problem, evaluatedAgent, nbEpisode, resetPeriod);
    OnPolicyRewardMonitor monitor = RewardMonitors.create("Target", nbRewardCheckpoint, nbTimeSteps, nbEpisode);
    monitor.connect(runner);
    behaviourRunner.onTimeStep.connect(new Listener<Runner.RunnerEvent>() {
      @Override
      public void listen(RunnerEvent eventInfo) {
        runner.step();
      }
    });
    return monitor;
  }
}
