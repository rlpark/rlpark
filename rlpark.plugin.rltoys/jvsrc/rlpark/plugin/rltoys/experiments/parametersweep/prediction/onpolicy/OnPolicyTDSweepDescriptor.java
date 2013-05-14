package rlpark.plugin.rltoys.experiments.parametersweep.prediction.onpolicy;

import rlpark.plugin.rltoys.experiments.parametersweep.prediction.PredictionLearnerFactory;
import rlpark.plugin.rltoys.experiments.parametersweep.prediction.PredictionProblemFactory;
import rlpark.plugin.rltoys.experiments.parametersweep.prediction.PredictionSweepContext;
import rlpark.plugin.rltoys.experiments.parametersweep.prediction.PredictionSweepDescriptor;

public class OnPolicyTDSweepDescriptor extends PredictionSweepDescriptor {
  public OnPolicyTDSweepDescriptor(PredictionProblemFactory[] problemFactories,
      PredictionLearnerFactory[] learnerFactories) {
    super(problemFactories, learnerFactories);
  }

  @Override
  protected PredictionSweepContext createContext(PredictionProblemFactory problemFactory,
      PredictionLearnerFactory learnerFactory) {
    return new OnPolicyTDSweepContext(problemFactory, learnerFactory);
  }
}
