package rlpark.plugin.rltoys.experiments.parametersweep.supervised;

import rlpark.plugin.rltoys.experiments.parametersweep.supervised.internal.ErrorMonitor;
import rlpark.plugin.rltoys.experiments.parametersweep.supervised.internal.SweepJob;

public class SupervisedParameters {
  public static final String NbLearningSteps = SweepJob.NbLearningSteps;
  public static final String NbEvaluationSteps = SweepJob.NbEvaluationSteps;
  public static final String MSE = ErrorMonitor.MSE;
}
