package rlpark.plugin.rltoys.experiments.testing.predictions;

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import rlpark.plugin.rltoys.algorithms.predictions.td.OnPolicyTD;
import rlpark.plugin.rltoys.experiments.testing.predictions.FiniteStateGraphOnPolicy.OnPolicyTDFactory;
import rlpark.plugin.rltoys.experiments.testing.results.TestingResult;
import rlpark.plugin.rltoys.problems.stategraph.FiniteStateGraph;
import rlpark.plugin.rltoys.problems.stategraph.LineProblem;
import rlpark.plugin.rltoys.problems.stategraph.RandomWalk;

public abstract class OnPolicyTests {
  private final LineProblem lineProblem = new LineProblem();
  private final RandomWalk randomWalkProblem = new RandomWalk(new Random(0));

  protected OnPolicyTDFactory[] onPolicyFactories() {
    return new OnPolicyTDFactory[] { new OnPolicyTDFactory() {
      @Override
      public OnPolicyTD create(double lambda, double gamma, double vectorNorm, int vectorSize) {
        return newOnPolicyTD(lambda, gamma, vectorNorm, vectorSize);
      }
    } };
  }

  @Test
  public void testOnLineProblem() {
    for (OnPolicyTDFactory factory : onPolicyFactories())
      testTD(0, lineProblem, factory);
  }

  @Test
  public void testOnRandomWalkProblem() {
    for (OnPolicyTDFactory factory : onPolicyFactories())
      testTD(0, randomWalkProblem, factory);
  }

  @Test
  public void testOnLineProblemWithLambda() {
    for (OnPolicyTDFactory factory : onPolicyFactories())
      for (double lambda : lambdaValues())
        testTD(lambda, lineProblem, factory);
  }

  @Test
  public void testOnRandomWalkProblemWithLambda() {
    for (OnPolicyTDFactory factory : onPolicyFactories())
      for (double lambda : lambdaValues())
        testTD(lambda, randomWalkProblem, factory);
  }

  private void testTD(double lambda, FiniteStateGraph problem, OnPolicyTDFactory factory) {
    TestingResult<OnPolicyTD> result = FiniteStateGraphOnPolicy.testTD(lambda, problem, factory, nbEpisodeMax(),
                                                                       precision());
    Assert.assertTrue(result.message, result.passed);
  }

  protected int nbEpisodeMax() {
    return 100000;
  }

  abstract protected OnPolicyTD newOnPolicyTD(double lambda, double gamma, double vectorNorm, int vectorSize);

  abstract protected double[] lambdaValues();

  abstract protected double precision();
}
