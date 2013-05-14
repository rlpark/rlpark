package rlpark.plugin.rltoys.experiments.parametersweep.reinforcementlearning.internal;

import rlpark.plugin.rltoys.experiments.parametersweep.internal.AbstractPerformanceMonitor;
import rlpark.plugin.rltoys.experiments.parametersweep.parameters.Parameters;

public abstract class AbstractEpisodeRewardMonitor extends AbstractPerformanceMonitor {
  private final int[] nbTimeSteps;

  public AbstractEpisodeRewardMonitor(String prefix, int[] starts) {
    super(prefix, "Reward", starts);
    nbTimeSteps = new int[starts.length];
  }

  public void registerMeasurement(int episode, double episodeReward, long nbEpisodeTimeSteps) {
    assert nbEpisodeTimeSteps > 0;
    super.registerMeasurement(episode, episodeReward);
    nbTimeSteps[currentSlice] += nbEpisodeTimeSteps;
  }

  private double divideBySize(int value, int size) {
    return value != Integer.MAX_VALUE ? value / size : Integer.MAX_VALUE;
  }

  @Override
  public void putResult(Parameters parameters) {
    super.putResult(parameters);
    for (int i = 0; i < starts.length; i++) {
      String sliceLabel = criterionLabel("NbTimeStepSliceMeasured", i);
      parameters.putResult(sliceLabel, divideBySize(nbTimeSteps[i], sizes[i]));
    }
  }

  @Override
  public void worstResultUntilEnd() {
    super.worstResultUntilEnd();
    for (int i = currentSlice; i < starts.length; i++)
      nbTimeSteps[i] = Integer.MAX_VALUE;
  }

  @Override
  protected double worstValue() {
    return -Float.MAX_VALUE;
  }
}
