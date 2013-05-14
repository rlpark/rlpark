package rlpark.plugin.rltoys.experiments.parametersweep.prediction.onpolicy;

import rlpark.plugin.rltoys.experiments.parametersweep.parameters.Parameters;
import rlpark.plugin.rltoys.experiments.parametersweep.prediction.PredictionLearnerFactory;
import rlpark.plugin.rltoys.experiments.parametersweep.prediction.PredictionParameters;
import rlpark.plugin.rltoys.experiments.parametersweep.prediction.PredictionProblemFactory;
import rlpark.plugin.rltoys.experiments.parametersweep.prediction.PredictionSweepContext;
import rlpark.plugin.rltoys.experiments.parametersweep.prediction.PredictorEvaluator;

public class OnPolicyTDSweepContext extends PredictionSweepContext {
  private static final long serialVersionUID = -3512756368836614504L;

  public OnPolicyTDSweepContext(PredictionProblemFactory problemFactory, PredictionLearnerFactory learnerFactory) {
    super(problemFactory, learnerFactory);
  }

  @Override
  public PredictorEvaluator createPredictorEvaluator(Parameters parameters) {
    int nbEvaluationSteps = PredictionParameters.nbEvaluationSteps(parameters);
    int nbPerformanceCheckpoints = PredictionParameters.nbPerformanceCheckpoint(parameters);
    double gamma = parameters.get(PredictionParameters.Gamma);
    return new OnPolicyTDErrorMonitor(gamma, nbPerformanceCheckpoints, nbEvaluationSteps);
  }
}
