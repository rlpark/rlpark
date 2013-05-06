package rlpark.plugin.rltoys.experiments.parametersweep.supervised.internal;

import rlpark.plugin.rltoys.experiments.parametersweep.parameters.Parameters;
import rlpark.plugin.rltoys.math.averages.IncrementalAverage;

public class ErrorMonitor {
  public static final String MSE = "MSE";
  private final IncrementalAverage average = new IncrementalAverage();
  private boolean resultEnabled = true;

  public void disableResult() {
    resultEnabled = false;
  }

  public void putResult(Parameters parameters) {
    parameters.putResult(MSE, resultEnabled ? average.mean() : Float.MAX_VALUE);
  }

  public void registerPrediction(double target, double prediction) {
    double diff = target - prediction;
    average.update(diff * diff);
  }
}
