package rltoys.experiments.parametersweep.reinforcementlearning.internal;

import rltoys.experiments.parametersweep.parameters.Parameters;
import rltoys.experiments.parametersweep.parameters.RunInfo;
import rltoys.experiments.parametersweep.reinforcementlearning.AgentEvaluator;

public abstract class AbstractRewardMonitor implements AgentEvaluator {
  protected int currentSlice;
  protected final int[] starts;
  protected final int[] sizes;
  private final double[] slices;
  private final String prefix;

  public AbstractRewardMonitor(String prefix, int[] starts) {
    this.prefix = prefix;
    this.starts = starts;
    slices = new double[starts.length];
    sizes = new int[starts.length];
  }

  static protected int[] createStartingPoints(int nbBins, int nbMeasurements) {
    int[] starts = new int[nbBins];
    double binSize = nbMeasurements / nbBins;
    for (int i = 0; i < starts.length; i++)
      starts[i] = (int) (i * binSize);
    return starts;
  }

  private double divideBySize(double value, int size) {
    return value != -Float.MAX_VALUE ? value / size : -Float.MAX_VALUE;
  }

  protected String criterionLabel(String label, int sliceIndex) {
    return String.format("%s%s%02d", prefix, label, sliceIndex);
  }

  @Override
  public void putResult(Parameters parameters) {
    RunInfo infos = parameters.infos();
    infos.put(prefix + "RewardNbCheckPoint", starts.length);
    for (int i = 0; i < starts.length; i++) {
      String startLabel = criterionLabel("RewardStart", i);
      infos.put(startLabel, starts[i]);
      String sliceLabel = criterionLabel("RewardSliceMeasured", i);
      parameters.putResult(sliceLabel, divideBySize(slices[i], sizes[i]));
    }
    double cumulatedReward = 0.0;
    int cumulatedSize = 0;
    for (int i = starts.length - 1; i >= 0; i--) {
      cumulatedSize += sizes[i];
      if (slices[i] != -Float.MAX_VALUE)
        cumulatedReward += slices[i];
      else
        cumulatedReward = -Float.MAX_VALUE;
      String rewardLabel = criterionLabel("RewardCumulatedMeasured", i);
      parameters.putResult(rewardLabel, divideBySize(cumulatedReward, cumulatedSize));
    }
  }

  public void registerMeasurement(long measurementIndex, double reward) {
    updateCurrentSlice(measurementIndex);
    slices[currentSlice] += reward;
    sizes[currentSlice]++;
  }

  private void updateCurrentSlice(long measurementIndex) {
    if (currentSlice < starts.length - 1 && measurementIndex >= starts[currentSlice + 1])
      currentSlice++;
  }

  @Override
  public void worstResultUntilEnd() {
    for (int i = currentSlice; i < starts.length; i++) {
      slices[i] = -Float.MAX_VALUE;
      sizes[i] = 1;
    }
  }
}
