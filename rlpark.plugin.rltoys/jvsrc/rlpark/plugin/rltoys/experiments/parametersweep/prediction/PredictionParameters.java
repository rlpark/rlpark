package rlpark.plugin.rltoys.experiments.parametersweep.prediction;

import java.util.List;

import rlpark.plugin.rltoys.experiments.parametersweep.parameters.AbstractParameters;
import rlpark.plugin.rltoys.experiments.parametersweep.parameters.Parameters;
import rlpark.plugin.rltoys.experiments.parametersweep.parameters.RunInfos;
import rlpark.plugin.rltoys.utils.Utils;

public class PredictionParameters {
  public static final String NbLearningSteps = "NbLearningSteps";
  public static final String NbEvaluationSteps = "NbEvaluationSteps";
  public static final String MSE = "MSE";
  public static final String StepSize = "StepSize";
  public static final String MetaStepSize = "MetaStepSize";
  public static final String Gamma = "gamma";

  public static final String Lambda = "Lambda";
  public static final String Tau = "Tau";

  static public int nbPerformanceCheckpoint(AbstractParameters parameters) {
    return (int) ((double) parameters.infos().get(Parameters.PerformanceNbCheckPoint));
  }

  static public int nbEvaluationSteps(AbstractParameters parameters) {
    return (int) ((double) parameters.infos().get(PredictionParameters.NbEvaluationSteps));
  }

  static public int nbLearningSteps(AbstractParameters parameters) {
    return (int) ((double) parameters.infos().get(PredictionParameters.NbLearningSteps));
  }

  final static public double[] getTauValues() {
    return new double[] { 1, 2, 4, 8, 16, 32 };
  }

  static public double[] getStepSizeValues(boolean withZero) {
    double[] values = new double[] { .0001, .0005, .001, .005, .01, .05, .1, .5, 1. };
    if (withZero)
      values = addZero(values);
    return values;
  }

  static public double[] getWideStepSizeValues(boolean withZero) {
    double[] values = new double[] { 1e-8, 1e-7, 1e-6, 1e-5, 1e-4, 1e-3, 1e-2, 1e-1, 1e0 };
    if (withZero)
      values = addZero(values);
    return values;
  }

  public static double[] getFewStepSizeValues(boolean withZero) {
    double[] values = new double[] { 1e-8, 1e-4, 1 };
    if (withZero)
      values = addZero(values);
    return values;
  }

  private static double[] addZero(double[] withoutZero) {
    double[] result = new double[withoutZero.length + 1];
    System.arraycopy(withoutZero, 0, result, 1, withoutZero.length);
    result[0] = 0.0;
    return result;
  }

  static public List<Parameters> provideLambdaParameters(List<Parameters> parameters) {
    return provideLambdaParameters(parameters, getTauValues());
  }

  public static List<Parameters> provideLambdaParameters(List<Parameters> parameters, double... tauValues) {
    List<Parameters> result = Parameters.combine(parameters, Tau, tauValues);
    for (Parameters p : result)
      p.putSweepParam(Lambda, Utils.timeStepsToDiscount((int) p.get(Tau)));
    return result;
  }

  public static List<Parameters> adjustForLocalTesting(List<Parameters> parameters) {
    Parameters selected = parameters.get(0);
    RunInfos.set(selected, PredictionParameters.NbLearningSteps, Math.min(10, nbLearningSteps(selected)));
    RunInfos.set(selected, PredictionParameters.NbEvaluationSteps,
                 Math.min(nbPerformanceCheckpoint(selected) * 2, nbEvaluationSteps(selected)));
    return Utils.asList(selected);
  }
}
