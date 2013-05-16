package rlpark.plugin.rltoys.experiments.parametersweep.reinforcementlearning;

import rlpark.plugin.rltoys.experiments.parametersweep.parameters.AbstractParameters;

public class RLParameters {
  public static final String OnPolicyTimeStepsEvaluationFlag = "onPolicyTimeStepsEvaluationFlag";
  public static final String MaxEpisodeTimeSteps = "maxEpisodeTimeSteps";
  public static final String NbEpisode = "nbEpisode";
  public static final String AverageReward = "averageReward";
  public static final String AveRewardStepSize = "AveRewardStepSize";
  public static final String ActorStepSize = "ActorStepSize";
  public static final String ValueFunctionStepSize = "ValueFunctionStepSize";
  public static final String ValueFunctionSecondStepSize = "ValueFunctionSecondStepSize";
  public static final String Temperature = "Temperature";

  final static public double[] getSoftmaxValues() {
    return new double[] { 100.0, 50.0, 10.0, 5.0, 1.0, .5, .1, .05, .01 };
  }

  static public int maxEpisodeTimeSteps(AbstractParameters parameters) {
    return (int) parameters.get(MaxEpisodeTimeSteps);
  }

  static public int nbEpisode(AbstractParameters parameters) {
    return (int) parameters.get(NbEpisode);
  }
}
