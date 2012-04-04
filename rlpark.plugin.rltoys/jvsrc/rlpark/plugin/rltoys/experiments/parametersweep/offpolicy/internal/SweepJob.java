package rlpark.plugin.rltoys.experiments.parametersweep.offpolicy.internal;

import rlpark.plugin.rltoys.experiments.ExperimentCounter;
import rlpark.plugin.rltoys.experiments.Runner;
import rlpark.plugin.rltoys.experiments.parametersweep.interfaces.JobWithParameters;
import rlpark.plugin.rltoys.experiments.parametersweep.parameters.Parameters;
import rlpark.plugin.rltoys.experiments.parametersweep.reinforcementlearning.AgentEvaluator;
import zephyr.plugin.core.api.synchronization.Chrono;

public class SweepJob implements JobWithParameters {
  private static final long serialVersionUID = -563211383079107807L;
  private final Parameters parameters;
  private final OffPolicyEvaluationContext context;
  private long computationTime;
  private final int counter;

  public SweepJob(OffPolicyEvaluationContext context, Parameters parameters, ExperimentCounter counter) {
    this.context = context;
    this.parameters = parameters;
    this.counter = counter.currentIndex();
  }

  @Override
  public void run() {
    Runner runner = context.createRunner(counter, parameters);
    AgentEvaluator behaviourRewardMonitor = context.connectBehaviourRewardMonitor(runner, parameters);
    AgentEvaluator targetRewardMonitor = context.connectTargetRewardMonitor(counter, runner, parameters);
    Chrono chrono = new Chrono();
    boolean diverged = false;
    try {
      runner.run();
    } catch (Throwable e) {
      behaviourRewardMonitor.worstResultUntilEnd();
      targetRewardMonitor.worstResultUntilEnd();
      diverged = true;
    }
    computationTime = chrono.getCurrentMillis();
    behaviourRewardMonitor.putResult(parameters);
    targetRewardMonitor.putResult(parameters);
    parameters.putResult("computationTime", diverged ? -1 : computationTime);
    parameters.putResult("totalTimeStep", runner.runnerEvent().nbTotalTimeSteps);
  }

  @Override
  public Parameters parameters() {
    return parameters;
  }
}
