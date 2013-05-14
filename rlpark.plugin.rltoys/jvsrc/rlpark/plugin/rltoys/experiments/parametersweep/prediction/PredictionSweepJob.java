package rlpark.plugin.rltoys.experiments.parametersweep.prediction;

import rlpark.plugin.rltoys.algorithms.predictions.supervised.LearningAlgorithm;
import rlpark.plugin.rltoys.experiments.helpers.ExperimentCounter;
import rlpark.plugin.rltoys.experiments.parametersweep.interfaces.JobWithParameters;
import rlpark.plugin.rltoys.experiments.parametersweep.parameters.Parameters;
import rlpark.plugin.rltoys.experiments.scheduling.interfaces.TimedJob;
import rlpark.plugin.rltoys.problems.PredictionProblem;
import rlpark.plugin.rltoys.utils.Utils;
import zephyr.plugin.core.api.synchronization.Chrono;

public class PredictionSweepJob implements JobWithParameters, TimedJob {
  private static final long serialVersionUID = -1601304080766261525L;
  private final PredictionContext context;
  private final Parameters parameters;
  private final int counter;

  public PredictionSweepJob(PredictionContext context, Parameters parameters, ExperimentCounter counter) {
    this.context = context;
    this.parameters = parameters;
    this.counter = counter.currentIndex();
  }

  @Override
  public void run() {
    Chrono chrono = new Chrono();
    PredictionProblem problem = context.problemFactory().createProblem(counter, parameters);
    LearningAlgorithm learner = (LearningAlgorithm) context.learnerFactory()
        .createLearner(counter, problem, parameters);
    PredictorEvaluator evaluator = context.createPredictorEvaluator(parameters);
    int nbLearningSteps = PredictionParameters.nbLearningSteps(parameters);
    int nbEvaluationSteps = PredictionParameters.nbEvaluationSteps(parameters);
    try {
      boolean resultEnabled = run(null, problem, learner, nbLearningSteps)
          && run(evaluator, problem, learner, nbEvaluationSteps);
      if (!resultEnabled)
        evaluator.worstResultUntilEnd();
    } catch (Throwable e) {
      e.printStackTrace(System.err);
      evaluator.worstResultUntilEnd();
    }
    evaluator.putResult(parameters);
    parameters.setComputationTimeMillis(chrono.getCurrentMillis());
  }

  private boolean run(PredictorEvaluator evaluator, PredictionProblem problem, LearningAlgorithm learner, long nbSteps) {
    for (int t = 0; t < nbSteps; t++) {
      boolean update = problem.update();
      if (!update)
        return true;
      if (evaluator != null) {
        double prediction = learner.predict(problem.input());
        evaluator.registerPrediction(t, problem.target(), prediction);
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
