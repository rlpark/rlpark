package rltoys.experiments.parametersweep.reinforcementlearning;

import java.util.Random;

import rltoys.environments.envio.problems.RLProblem;

public interface OffPolicyProblemFactory extends ProblemFactory {
  RLProblem createEvaluationEnvironment(Random random);
}
