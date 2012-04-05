package rlpark.plugin.rltoys.experiments.parametersweep.reinforcementlearning;

import java.util.Random;

import rlpark.plugin.rltoys.problems.RLProblem;

public interface OffPolicyProblemFactory extends ProblemFactory {
  RLProblem createEvaluationEnvironment(Random random);
}
