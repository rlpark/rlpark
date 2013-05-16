package rlpark.plugin.rltoys.experiments.parametersweep.prediction;

import java.util.ArrayList;
import java.util.List;

import rlpark.plugin.rltoys.experiments.parametersweep.interfaces.Context;
import rlpark.plugin.rltoys.experiments.parametersweep.interfaces.SweepDescriptor;
import rlpark.plugin.rltoys.experiments.parametersweep.parameters.Parameters;

public abstract class PredictionSweepDescriptor implements SweepDescriptor {
  private static boolean testingMode = false;
  private final PredictionProblemFactory[] problemFactories;
  private final PredictionLearnerFactory[] learnerFactories;

  public PredictionSweepDescriptor(PredictionProblemFactory[] problemFactories,
      PredictionLearnerFactory[] learnerFactories) {
    this.problemFactories = problemFactories;
    this.learnerFactories = learnerFactories;

  }

  @Override
  public List<? extends Context> provideContexts() {
    List<PredictionSweepContext> contexts = new ArrayList<PredictionSweepContext>();
    for (PredictionProblemFactory problemFactory : problemFactories)
      for (PredictionLearnerFactory learnerFactory : learnerFactories)
        contexts.add(createContext(problemFactory, learnerFactory));
    return contexts;
  }

  @Override
  public List<Parameters> provideParameters(Context context) {
    List<Parameters> parameters = ((PredictionSweepContext) context).provideParameters();
    if (testingMode)
      return PredictionParameters.adjustForLocalTesting(parameters);
    return parameters;
  }

  abstract protected PredictionSweepContext createContext(PredictionProblemFactory problemFactory,
      PredictionLearnerFactory learnerFactory);

  static public void setupTestingMode() {
    testingMode = true;
  }
}
