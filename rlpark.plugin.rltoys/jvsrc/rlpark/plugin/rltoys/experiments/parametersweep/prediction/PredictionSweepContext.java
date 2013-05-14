package rlpark.plugin.rltoys.experiments.parametersweep.prediction;

import java.util.List;

import rlpark.plugin.rltoys.experiments.helpers.ExperimentCounter;
import rlpark.plugin.rltoys.experiments.parametersweep.parameters.Parameters;
import rlpark.plugin.rltoys.experiments.parametersweep.parameters.ParametersProvider;
import rlpark.plugin.rltoys.experiments.parametersweep.parameters.RunInfo;
import rlpark.plugin.rltoys.utils.Utils;

public abstract class PredictionSweepContext implements PredictionContext {
  private static final long serialVersionUID = 6250984799273140622L;
  private final PredictionProblemFactory problemFactory;
  private final PredictionLearnerFactory learnerFactory;

  public PredictionSweepContext(PredictionProblemFactory problemFactory, PredictionLearnerFactory learnerFactory) {
    this.problemFactory = problemFactory;
    this.learnerFactory = learnerFactory;
  }

  @Override
  public String folderPath() {
    return problemFactory.label() + "/" + learnerFactory.label();
  }

  @Override
  public String fileName() {
    return ExperimentCounter.DefaultFileName;
  }

  @Override
  public Runnable createJob(Parameters parameters, ExperimentCounter counter) {
    return new PredictionSweepJob(this, parameters, counter);
  }

  public List<Parameters> provideParameters() {
    RunInfo infos = new RunInfo();
    infos.enableFlag(problemFactory.label());
    infos.enableFlag(learnerFactory.label());
    infos.put(Parameters.PerformanceNbCheckPoint, Parameters.DefaultNbPerformanceCheckpoints);
    List<Parameters> parameters = Utils.asList(new Parameters(infos));
    if (problemFactory instanceof ParametersProvider)
      parameters = ((ParametersProvider) problemFactory).provideParameters(parameters);
    if (learnerFactory instanceof ParametersProvider)
      parameters = ((ParametersProvider) learnerFactory).provideParameters(parameters);
    return parameters;
  }

  @Override
  public PredictionProblemFactory problemFactory() {
    return problemFactory;
  }

  @Override
  public PredictionLearnerFactory learnerFactory() {
    return learnerFactory;
  }
}
