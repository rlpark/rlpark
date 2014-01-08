package rlpark.plugin.rltoys.experiments.parametersweep.reinforcementlearning;

import rlpark.plugin.rltoys.experiments.parametersweep.parameters.AbstractParameters;
import rlpark.plugin.rltoys.experiments.parametersweep.parameters.Parameters;
import rlpark.plugin.rltoys.experiments.parametersweep.prediction.PredictionParameters;

public class RLParameters {
  public static final String OnPolicyTimeStepsEvaluationFlag = "onPolicyTimeStepsEvaluationFlag";
  public static final String MaxEpisodeTimeSteps = "maxEpisodeTimeSteps";
  public static final String NbEpisode = "nbEpisode";
  public static final String AverageReward = "averageReward";
  public static final String TotalNumberOfTimeSteps = "totalNumberOfTimeSteps";
  public static final String AveRewardStepSize = "AveRewardStepSize";

  public static final String ActorPrefix = "Actor";
  public static final String CriticPrefix = "Critic";

  public static final String ActorStepSize = ActorPrefix + PredictionParameters.StepSize;
  public static final String CriticStepSize = CriticPrefix + PredictionParameters.StepSize;

  public static final String ValueFunctionSecondStepSize = "ValueFunctionSecondStepSize";
  public static final String Temperature = "Temperature";
  public static final String Epsilon = "Epsilon";
  public static final String NbEpisodePerEvaluation = "NbEpisodePerEvaluation";
  public static final String NbRewardCheckpoint = "NbRewardCheckpoint";

  final static public double[] getSoftmaxValues() {
    return new double[] { 100.0, 50.0, 10.0, 5.0, 1.0, .5, .1, .05, .01 };
  }

  static public int maxEpisodeTimeSteps(AbstractParameters parameters) {
    return (int) parameters.get(MaxEpisodeTimeSteps);
  }

  static public int nbEpisode(AbstractParameters parameters) {
    return (int) parameters.get(NbEpisode);
  }

  static public int totalNumberOfTimeSteps(AbstractParameters parameters) {
    return (int) parameters.get(TotalNumberOfTimeSteps);
  }

  public static int nbEpisodePerEvaluation(Parameters parameters) {
    return (int) parameters.get(NbEpisodePerEvaluation);
  }

  public static int nbRewardCheckpoint(Parameters parameters) {
    return (int) parameters.get(NbRewardCheckpoint);
  }
}
