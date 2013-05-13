package rlpark.plugin.rltoys.experiments.parametersweep.parameters;


public class ParameterValues {
  public static final String StepSize = "StepSize";
  public static final String MetaStepSize = "MetaStepSize";

  static public double[] getStepSizeValues(boolean withZero) {
    double[] values = new double[] { .0001, .0005, .001, .005, .01, .05, .1, .5, 1. };
    if (withZero)
      values = ParameterValues.addZero(values);
    return values;
  }

  static public double[] getWideStepSizeValues(boolean withZero) {
    double[] values = new double[] { 1e-8, 1e-7, 1e-6, 1e-5, 1e-4, 1e-3, 1e-2, 1e-1, 1e0 };
    if (withZero)
      values = ParameterValues.addZero(values);
    return values;
  }

  private static double[] addZero(double[] withoutZero) {
    double[] result = new double[withoutZero.length + 1];
    System.arraycopy(withoutZero, 0, result, 1, withoutZero.length);
    result[0] = 0.0;
    return result;
  }

  public static double[] getFewStepSizeValues(boolean withZero) {
    double[] values = new double[] { 1e-8, 1e-4, 1 };
    if (withZero)
      values = ParameterValues.addZero(values);
    return values;
  }
}
