package rlpark.plugin.rltoys.junit.experiments.supervised;

import java.util.List;

import rlpark.plugin.rltoys.experiments.parametersweep.parameters.Parameters;
import rlpark.plugin.rltoys.experiments.parametersweep.parameters.ParametersProvider;
import rlpark.plugin.rltoys.experiments.parametersweep.parameters.RunInfos;
import rlpark.plugin.rltoys.experiments.parametersweep.supervised.SupervisedParameters;
import rlpark.plugin.rltoys.experiments.parametersweep.supervised.SupervisedProblemFactory;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.math.vector.implementations.PVector;
import rlpark.plugin.rltoys.problems.SupervisedProblem;

final class SupervisedProblemFactoryJUnit implements SupervisedProblemFactory, ParametersProvider {
  private static final long serialVersionUID = 8772273496069908596L;
  public static final String Target = "Target";
  static final double NbLearningSteps = 10;
  static final double NbEvaluationSteps = 10;
  final double targetValue;

  public SupervisedProblemFactoryJUnit(double targetValue) {
    this.targetValue = targetValue;
  }

  @Override
  public String label() {
    return "dummyProblem" + targetValue;
  }

  @Override
  public SupervisedProblem createProblem(int counter, Parameters parameters) {
    return new SupervisedProblem() {
      private final PVector input = new PVector(1.0);

      @Override
      public void update() {
      }

      @Override
      public double target() {
        return targetValue;
      }

      @Override
      public RealVector input() {
        return input;
      }

      @Override
      public int inputDimension() {
        return input.getDimension();
      }
    };
  }

  @Override
  public List<Parameters> provideParameters(List<Parameters> parameters) {
    RunInfos.set(parameters, SupervisedParameters.NbLearningSteps, NbLearningSteps);
    RunInfos.set(parameters, SupervisedParameters.NbEvaluationSteps, NbEvaluationSteps);
    RunInfos.set(parameters, Target, targetValue);
    return parameters;
  }
}