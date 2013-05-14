package rlpark.plugin.rltoys.experiments.parametersweep.supervised.internal;

import rlpark.plugin.rltoys.algorithms.predictions.supervised.LearningAlgorithm;
import rlpark.plugin.rltoys.experiments.helpers.ExperimentCounter;
import rlpark.plugin.rltoys.experiments.parametersweep.interfaces.JobWithParameters;
import rlpark.plugin.rltoys.experiments.parametersweep.parameters.AbstractParameters;
import rlpark.plugin.rltoys.experiments.parametersweep.parameters.Parameters;
import rlpark.plugin.rltoys.experiments.scheduling.interfaces.TimedJob;
import rlpark.plugin.rltoys.problems.SupervisedProblem;
import rlpark.plugin.rltoys.utils.Utils;
import zephyr.plugin.core.api.synchronization.Chrono;

public class SweepJob implements JobWithParameters, TimedJob {
  private static final long serialVersionUID = -1601304080766261525L;
  public static final String NbLearningSteps = "NbLearningSteps";
  public static final String NbEvaluationSteps = "NbEvaluationSteps";
  private final SupervisedContext context;
  private final Parameters parameters;
  private final int counter;

  public SweepJob(SupervisedContext context, Parameters parameters, ExperimentCounter counter) {
    this.context = context;
    this.parameters = parameters;
    this.counter = counter.currentIndex();
  }

  @Override
  public void run() {
    Chrono chrono = new Chrono();
    SupervisedProblem problem = context.createProblem(counter, parameters);
    LearningAlgorithm learner = context.createLearner(counter, problem, parameters);
    int nbLearningSteps = nbLearningSteps(parameters);
    int nbEvaluationSteps = nbEvaluationSteps(parameters);
    int nbPerformanceCheckpoints = nbPerformanceCheckpoint(parameters);
    ErrorMonitor errorMonitor = new ErrorMonitor(nbPerformanceCheckpoints, nbEvaluationSteps);
    try {
      boolean resultEnabled = run(null, problem, learner, nbLearningSteps)
          && run(errorMonitor, problem, learner, nbEvaluationSteps);
      if (!resultEnabled)
        errorMonitor.worstResultUntilEnd();
    } catch (Throwable e) {
      e.printStackTrace(System.err);
      errorMonitor.worstResultUntilEnd();
    }
    errorMonitor.putResult(parameters);
    parameters.setComputationTimeMillis(chrono.getCurrentMillis());
  }

  static public int nbPerformanceCheckpoint(AbstractParameters parameters) {
    return (int) ((double) parameters.infos().get(Parameters.PerformanceNbCheckPoint));
  }

  static public int nbEvaluationSteps(AbstractParameters parameters) {
    return (int) ((double) parameters.infos().get(NbEvaluationSteps));
  }

  static public int nbLearningSteps(AbstractParameters parameters) {
    return (int) ((double) parameters.infos().get(NbLearningSteps));
  }

  private boolean run(ErrorMonitor errorMonitor, SupervisedProblem problem, LearningAlgorithm learner, long nbSteps) {
    for (int t = 0; t < nbSteps; t++) {
      boolean update = problem.update();
      if (!update)
        return true;
      if (errorMonitor != null) {
        double prediction = learner.predict(problem.input());
        errorMonitor.registerPrediction(t, problem.target(), prediction);
        if (!Utils.checkValue(prediction))
          return false;
      }
      double error = learner.learn(problem.input(), problem.target());
      if (!Utils.checkValue(error))
        return false;
    }
    return true;
  }

  @Override
  public long getComputationTimeMillis() {
    return parameters.getComputationTimeMillis();
  }

  @Override
  public Parameters parameters() {
    return parameters;
  }
}
