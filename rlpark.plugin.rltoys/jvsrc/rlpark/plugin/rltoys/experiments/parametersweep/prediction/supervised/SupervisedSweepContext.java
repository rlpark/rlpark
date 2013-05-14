package rlpark.plugin.rltoys.experiments.parametersweep.prediction.supervised;

import rlpark.plugin.rltoys.experiments.helpers.ExperimentCounter;
import rlpark.plugin.rltoys.experiments.parametersweep.parameters.Parameters;
import rlpark.plugin.rltoys.experiments.parametersweep.prediction.PredictionLearnerFactory;
import rlpark.plugin.rltoys.experiments.parametersweep.prediction.PredictionProblemFactory;
import rlpark.plugin.rltoys.experiments.parametersweep.prediction.PredictionSweepContext;

public class SupervisedSweepContext extends PredictionSweepContext {
  private static final long serialVersionUID = -3512756368836614504L;

  public SupervisedSweepContext(PredictionProblemFactory problemFactory, PredictionLearnerFactory learnerFactory) {
    super(problemFactory, learnerFactory);
  }

  @Override
  public Runnable createJob(Parameters parameters, ExperimentCounter counter) {
    return new SweepJob(this, parameters, counter);
  }
}
