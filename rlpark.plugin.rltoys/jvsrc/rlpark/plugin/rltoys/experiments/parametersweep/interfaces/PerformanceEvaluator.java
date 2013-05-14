package rlpark.plugin.rltoys.experiments.parametersweep.interfaces;

import rlpark.plugin.rltoys.experiments.parametersweep.parameters.Parameters;


public interface PerformanceEvaluator {
  void worstResultUntilEnd();

  void putResult(Parameters parameters);
}