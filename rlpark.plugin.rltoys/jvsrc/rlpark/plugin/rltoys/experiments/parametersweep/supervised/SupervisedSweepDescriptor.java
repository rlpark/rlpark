package rlpark.plugin.rltoys.experiments.parametersweep.supervised;

import java.util.ArrayList;
import java.util.List;

import rlpark.plugin.rltoys.experiments.parametersweep.interfaces.Context;
import rlpark.plugin.rltoys.experiments.parametersweep.interfaces.SweepDescriptor;
import rlpark.plugin.rltoys.experiments.parametersweep.parameters.Parameters;

public class SupervisedSweepDescriptor implements SweepDescriptor {
  private final SupervisedProblemFactory[] problemFactories;
  private final SupervisedLearnerFactory[] learnerFactories;

  public SupervisedSweepDescriptor(SupervisedProblemFactory[] problemFactories,
      SupervisedLearnerFactory[] learnerFactories) {
    this.problemFactories = problemFactories;
    this.learnerFactories = learnerFactories;

  }

  @Override
  public List<? extends Context> provideContexts() {
    List<SupervisedSweepContext> contexts = new ArrayList<SupervisedSweepContext>();
    for (SupervisedProblemFactory problemFactory : problemFactories)
      for (SupervisedLearnerFactory learnerFactory : learnerFactories)
        contexts.add(new SupervisedSweepContext(problemFactory, learnerFactory));
    return contexts;
  }

  @Override
  public List<Parameters> provideParameters(Context context) {
    return ((SupervisedSweepContext) context).provideParameters();
  }
}
