package rlpark.plugin.rltoys.experiments.parametersweep.prediction;

import rlpark.plugin.rltoys.experiments.parametersweep.parameters.AbstractParameters;
import rlpark.plugin.rltoys.experiments.parametersweep.parameters.Parameters;

public class PredictionParameters {
  public static final String NbLearningSteps = "NbLearningSteps";
  public static final String NbEvaluationSteps = "NbEvaluationSteps";
  public static final String MSE = "MSE";
  public static final String Gamma = "gamma";

  static public int nbPerformanceCheckpoint(AbstractParameters parameters) {
    return (int) ((double) parameters.infos().get(Parameters.PerformanceNbCheckPoint));
  }

  static public int nbEvaluationSteps(AbstractParameters parameters) {
    return (int) ((double) parameters.infos().get(PredictionParameters.NbEvaluationSteps));
  }

  static public int nbLearningSteps(AbstractParameters parameters) {
    return (int) ((double) parameters.infos().get(PredictionParameters.NbLearningSteps));
  }
}
