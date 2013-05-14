package rlpark.plugin.rltoys.experiments.parametersweep.supervised;

import java.util.List;

import rlpark.plugin.rltoys.algorithms.predictions.supervised.LearningAlgorithm;
import rlpark.plugin.rltoys.experiments.helpers.ExperimentCounter;
import rlpark.plugin.rltoys.experiments.parametersweep.parameters.Parameters;
import rlpark.plugin.rltoys.experiments.parametersweep.parameters.ParametersProvider;
import rlpark.plugin.rltoys.experiments.parametersweep.parameters.RunInfo;
import rlpark.plugin.rltoys.experiments.parametersweep.supervised.internal.SupervisedContext;
import rlpark.plugin.rltoys.experiments.parametersweep.supervised.internal.SweepJob;
import rlpark.plugin.rltoys.problems.SupervisedProblem;
import rlpark.plugin.rltoys.utils.Utils;

public class SupervisedSweepContext implements SupervisedContext {
  private static final long serialVersionUID = 6250984799273140622L;
  private final SupervisedProblemFactory problemFactory;
  private final SupervisedLearnerFactory learnerFactory;

  public SupervisedSweepContext(SupervisedProblemFactory problemFactory, SupervisedLearnerFactory learnerFactory) {
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
    return new SweepJob(this, parameters, counter);
  }

  @Override
  public SupervisedProblem createProblem(int counter, Parameters parameters) {
    return problemFactory.createProblem(counter, parameters);
  }

  @Override
  public LearningAlgorithm createLearner(int counter, SupervisedProblem problem, Parameters parameters) {
    return learnerFactory.createLearner(counter, problem, parameters);
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
}
