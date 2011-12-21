package rltoys.experiments.parametersweep.reinforcementlearning.internal;

import rltoys.experiments.parametersweep.parameters.Parameters;

public abstract class AbstractEpisodeRewardMonitor extends AbstractRewardMonitor {
  private final int[] nbTimeSteps;

  public AbstractEpisodeRewardMonitor(String prefix, int[] starts) {
    super(prefix, starts);
    nbTimeSteps = new int[starts.length];
  }

  public void registerMeasurement(int episode, double episodeReward, long nbEpisodeTimeSteps) {
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
}
