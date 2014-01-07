package rlpark.plugin.rltoys.experiments.parametersweep.onpolicy.internal;

import rlpark.plugin.rltoys.experiments.helpers.ExperimentCounter;
import rlpark.plugin.rltoys.experiments.parametersweep.interfaces.JobWithParameters;
import rlpark.plugin.rltoys.experiments.parametersweep.parameters.Parameters;
import rlpark.plugin.rltoys.experiments.runners.AbstractRunner;
import rlpark.plugin.rltoys.experiments.scheduling.interfaces.TimedJob;
import zephyr.plugin.core.api.synchronization.Chrono;

public class SweepJob implements JobWithParameters, TimedJob {
  private static final long serialVersionUID = -1636763888764939471L;
  private final Parameters parameters;
  private final OnPolicyEvaluationContext context;
  private final int counter;

  public SweepJob(OnPolicyEvaluationContext context, Parameters parameters, ExperimentCounter counter) {
    this.context = context;
    this.parameters = parameters;
    this.counter = counter.currentIndex();
  }

  @Override
  public void run() {
    AbstractRunner runner = context.createRunner(counter, parameters);
    OnPolicyRewardMonitor rewardMonitor = context.createRewardMonitor(parameters);
    rewardMonitor.connect(runner);
    Chrono chrono = new Chrono();
    try {
      runner.run();
    } catch (Throwable e) {
      e.printStackTrace(System.err);
      rewardMonitor.worstResultUntilEnd();
    }
    rewardMonitor.putResult(parameters);
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
