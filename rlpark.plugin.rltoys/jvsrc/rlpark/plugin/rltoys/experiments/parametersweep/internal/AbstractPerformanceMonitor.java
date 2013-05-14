package rlpark.plugin.rltoys.experiments.parametersweep.internal;

import rlpark.plugin.rltoys.experiments.parametersweep.interfaces.PerformanceEvaluator;
import rlpark.plugin.rltoys.experiments.parametersweep.parameters.Parameters;
import rlpark.plugin.rltoys.experiments.parametersweep.parameters.RunInfo;

public abstract class AbstractPerformanceMonitor implements PerformanceEvaluator {
  protected int currentSlice;
  protected final int[] starts;
  protected final int[] sizes;
  private final double[] slices;
  private final String prefix;
  private final String performanceLabel;

  public AbstractPerformanceMonitor(String prefix, String performanceLabel, int[] starts) {
    this.prefix = prefix;
    this.performanceLabel = performanceLabel;
    this.starts = starts;
    slices = new double[starts.length];
    sizes = new int[starts.length];
  }

  static protected int[] createStartingPoints(int nbBins, int nbMeasurements) {
    int[] starts = new int[nbBins];
    double binSize = (double) nbMeasurements / nbBins;
    for (int i = 0; i < starts.length; i++)
      starts[i] = (int) (i * binSize);
    return starts;
  }

  private double divideBySize(double value, int size) {
    return value != worstValue() ? value / size : worstValue();
  }

  protected String criterionLabel(String label, int sliceIndex) {
    return String.format("%s%s%02d", prefix, label, sliceIndex);
  }

  @Override
  public void putResult(Parameters parameters) {
    RunInfo infos = parameters.infos();
    infos.put(prefix + performanceLabel + Parameters.PerformanceNbCheckPoint, starts.length);
    for (int i = 0; i < starts.length; i++) {
      String startLabel = criterionLabel(performanceLabel + Parameters.PerformanceStart, i);
      infos.put(startLabel, starts[i]);
      String sliceLabel = criterionLabel(performanceLabel + Parameters.PerformanceSliceMeasured, i);
      parameters.putResult(sliceLabel, divideBySize(slices[i], sizes[i]));
    }
    double cumulatedReward = 0.0;
    int cumulatedSize = 0;
    for (int i = starts.length - 1; i >= 0; i--) {
      cumulatedSize += sizes[i];
      if (slices[i] != worstValue())
        cumulatedReward += slices[i];
      else
        cumulatedReward = worstValue();
      String rewardLabel = criterionLabel(performanceLabel + Parameters.PerformanceCumulatedMeasured, i);
      parameters.putResult(rewardLabel, divideBySize(cumulatedReward, cumulatedSize));
    }
  }

  protected void registerMeasurement(long measurementIndex, double reward) {
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
      slices[i] = worstValue();
      sizes[i] = 1;
    }
  }

  abstract protected double worstValue();
}
