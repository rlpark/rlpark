package rlpark.plugin.rltoys.junit.experiments.predictions.onpolicytd;

import java.util.List;

import rlpark.plugin.rltoys.algorithms.predictions.supervised.LearningAlgorithm;
import rlpark.plugin.rltoys.experiments.parametersweep.parameters.Parameters;
import rlpark.plugin.rltoys.experiments.parametersweep.parameters.ParametersProvider;
import rlpark.plugin.rltoys.experiments.parametersweep.prediction.PredictionLearnerFactory;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.problems.PredictionProblem;

public class OnPolicyTDLearnerFactoryJUnit implements PredictionLearnerFactory, ParametersProvider {
  private static final long serialVersionUID = 6310797873611293280L;
  public static final String Parameter = "Value";
  public static final String Divergence = "Divergence";
  private int divergeAt = Integer.MAX_VALUE;

  @Override
  public String label() {
    return "dummyLearner";
  }

  @Override
  public LearningAlgorithm createLearner(long seed, PredictionProblem problem, Parameters parameters) {
    final double value = parameters.get(Parameter);
    final double localDivergeAt = divergeAt;
    final boolean divergeAtLearning = parameters.get(Divergence) < 0;
    return new LearningAlgorithm() {
      private static final long serialVersionUID = 4119446402823858877L;
      private int nbUpdate = 0;

      @Override
      public double predict(RealVector x) {
        if (nbUpdate <= localDivergeAt)
          return value;
        return divergeAtLearning ? value : Double.NaN;
      }

      @Override
      public double learn(RealVector x_t, double y_tp1) {
        nbUpdate++;
        if (nbUpdate <= localDivergeAt)
          return 0;
        return divergeAtLearning ? Double.NaN : 0;
      }
    };
  }

  @Override
  public List<Parameters> provideParameters(List<Parameters> parameters) {
    List<Parameters> result = Parameters.combine(parameters, Parameter, new double[] { 2, 3 });
    result = Parameters.combine(result, Divergence, new double[] { -1, 1 });
    return result;
  }

  public void divergeAt(int divergeAt) {
    this.divergeAt = divergeAt;
  }
}
