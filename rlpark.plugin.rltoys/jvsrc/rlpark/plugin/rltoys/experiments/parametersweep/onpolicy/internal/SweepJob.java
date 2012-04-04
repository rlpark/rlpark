package rlpark.plugin.rltoys.experiments.parametersweep.onpolicy.internal;

import rlpark.plugin.rltoys.experiments.ExperimentCounter;
import rlpark.plugin.rltoys.experiments.Runner;
import rlpark.plugin.rltoys.experiments.parametersweep.interfaces.JobWithParameters;
import rlpark.plugin.rltoys.experiments.parametersweep.parameters.Parameters;
import zephyr.plugin.core.api.synchronization.Chrono;

public class SweepJob implements JobWithParameters {
  private static final long serialVersionUID = -1636763888764939471L;
  private final Parameters parameters;
  private final OnPolicyEvaluationContext context;
  private long computationTime;
  private final int counter;

  public SweepJob(OnPolicyEvaluationContext context, Parameters parameters, ExperimentCounter counter) {
    this.context = context;
    this.parameters = parameters;
    this.counter = counter.currentIndex();
  }

  @Override
  public void run() {
    Runner runner = context.createRunner(counter, parameters);
    OnPolicyRewardMonitor rewardMonitor = context.createRewardMonitor(parameters);
    rewardMonitor.connect(runner);
    Chrono chrono = new Chrono();
    boolean diverged = false;
    try {
      runner.run();
    } catch (Throwable e) {
      rewardMonitor.worstResultUntilEnd();
      diverged = true;
    }
    computationTime = chrono.getCurrentMillis();
    rewardMonitor.putResult(parameters);
    parameters.putResult("computationTime", diverged ? -1 : computationTime);
    parameters.putResult("totalTimeStep", runner.runnerEvent().nbTotalTimeSteps);
  }

  @Override
  public Parameters parameters() {
    return parameters;
  }
}
