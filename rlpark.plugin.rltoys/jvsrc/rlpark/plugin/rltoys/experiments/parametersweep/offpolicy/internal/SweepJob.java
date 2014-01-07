package rlpark.plugin.rltoys.experiments.parametersweep.offpolicy.internal;

import rlpark.plugin.rltoys.experiments.helpers.ExperimentCounter;
import rlpark.plugin.rltoys.experiments.parametersweep.interfaces.JobWithParameters;
import rlpark.plugin.rltoys.experiments.parametersweep.interfaces.PerformanceEvaluator;
import rlpark.plugin.rltoys.experiments.parametersweep.parameters.Parameters;
import rlpark.plugin.rltoys.experiments.runners.AbstractRunner;
import rlpark.plugin.rltoys.experiments.scheduling.interfaces.TimedJob;
import zephyr.plugin.core.api.synchronization.Chrono;

public class SweepJob implements JobWithParameters, TimedJob {
  private static final long serialVersionUID = -563211383079107807L;
  private final Parameters parameters;
  private final OffPolicyEvaluationContext context;
  private final int counter;

  public SweepJob(OffPolicyEvaluationContext context, Parameters parameters, ExperimentCounter counter) {
    this.context = context;
    this.parameters = parameters;
    this.counter = counter.currentIndex();
  }

  @Override
  public void run() {
    AbstractRunner runner = context.createRunner(counter, parameters);
    PerformanceEvaluator behaviourRewardMonitor = context.connectBehaviourRewardMonitor(runner, parameters);
    PerformanceEvaluator targetRewardMonitor = context.connectTargetRewardMonitor(counter, runner, parameters);
    Chrono chrono = new Chrono();
    try {
      runner.run();
    } catch (Throwable e) {
      e.printStackTrace(System.err);
      behaviourRewardMonitor.worstResultUntilEnd();
      targetRewardMonitor.worstResultUntilEnd();
    }
    behaviourRewardMonitor.putResult(parameters);
    targetRewardMonitor.putResult(parameters);
    parameters.putResult("totalTimeStep", runner.runnerEvent().nbTotalTimeSteps);
    parameters.setComputationTimeMillis(chrono.getCurrentMillis());
  }

  @Override
  public Parameters parameters() {
    return parameters;
  }

  @Override
  public long getComputationTimeMillis() {
    return parameters.getComputationTimeMillis();
  }
}
