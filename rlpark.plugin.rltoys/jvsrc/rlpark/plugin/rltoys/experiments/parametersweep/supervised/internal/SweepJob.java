package rlpark.plugin.rltoys.experiments.parametersweep.supervised.internal;

import rlpark.plugin.rltoys.algorithms.predictions.supervised.LearningAlgorithm;
import rlpark.plugin.rltoys.experiments.helpers.ExperimentCounter;
import rlpark.plugin.rltoys.experiments.parametersweep.interfaces.JobWithParameters;
import rlpark.plugin.rltoys.experiments.parametersweep.parameters.Parameters;
import rlpark.plugin.rltoys.experiments.scheduling.interfaces.TimedJob;
import rlpark.plugin.rltoys.problems.SupervisedProblem;
import rlpark.plugin.rltoys.utils.Utils;
import zephyr.plugin.core.api.synchronization.Chrono;

public class SweepJob implements JobWithParameters, TimedJob {
  public static final String NbLearningSteps = "NbLearningSteps";
  public static final String NbEvaluationSteps = "NbEvaluationSteps";
  private static final long serialVersionUID = -1601304080766261525L;
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
    ErrorMonitor errorMonitor = new ErrorMonitor();
    SupervisedProblem problem = context.createProblem(counter, parameters);
    LearningAlgorithm learner = context.createLearner(counter, problem, parameters);
    long nbLearningSteps = (long) ((double) parameters.infos().get(NbLearningSteps));
    long nbEvaluationSteps = (long) ((double) parameters.infos().get(NbEvaluationSteps));
    try {
      run(null, problem, learner, nbLearningSteps);
      boolean resultEnabled = run(errorMonitor, problem, learner, nbEvaluationSteps);
      if (!resultEnabled)
        errorMonitor.disableResult();
    } catch (Throwable e) {
      e.printStackTrace(System.err);
      errorMonitor.disableResult();
    }
    errorMonitor.putResult(parameters);
    parameters.setComputationTimeMillis(chrono.getCurrentMillis());
  }

  private boolean run(ErrorMonitor errorMonitor, SupervisedProblem problem, LearningAlgorithm learner, long nbSteps) {
    for (int t = 0; t < nbSteps; t++) {
      problem.update();
      if (errorMonitor != null) {
        double prediction = learner.predict(problem.input());
        errorMonitor.registerPrediction(problem.target(), prediction);
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
