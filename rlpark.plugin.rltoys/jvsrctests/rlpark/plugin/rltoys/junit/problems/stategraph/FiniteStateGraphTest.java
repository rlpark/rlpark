package rlpark.plugin.rltoys.junit.problems.stategraph;

import static org.junit.Assert.assertEquals;

import java.util.Random;

import org.junit.Test;

import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.policy.Policy;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.problems.stategraph.FSGAgentState;
import rlpark.plugin.rltoys.problems.stategraph.LineProblem;
import rlpark.plugin.rltoys.problems.stategraph.RandomWalk;
import rlpark.plugin.rltoys.problems.stategraph.FiniteStateGraph.StepData;

public class FiniteStateGraphTest {
  static private final Policy leftPolicy = new Policy() {
    private static final long serialVersionUID = -3697408235150317492L;

    @Override
    public Action decide(RealVector state) {
      return RandomWalk.left;
    }

    @Override
    public double pi(RealVector s, Action a) {
      return a == RandomWalk.left ? 1 : 0;
    }
  };
  static private final Policy rightPolicy = new Policy() {
    private static final long serialVersionUID = -5534122517914442095L;

    @Override
    public Action decide(RealVector state) {
      return RandomWalk.right;
    }

    @Override
    public double pi(RealVector s, Action a) {
      return a == RandomWalk.right ? 1 : 0;
    }
  };

  @Test
  public void testSimpleProblemTrajectory() {
    LineProblem sp = new LineProblem();
    assertEquals(new StepData(0, null, null, LineProblem.A, 0.0, LineProblem.a), sp.step());
    assertEquals(new StepData(1, LineProblem.A, LineProblem.a, LineProblem.B, 0.0, LineProblem.a), sp.step());
    assertEquals(new StepData(2, LineProblem.B, LineProblem.a, LineProblem.C, 0.0, LineProblem.a), sp.step());
    assertEquals(new StepData(3, LineProblem.C, LineProblem.a, null, 1.0, null), sp.step());
    assertEquals(new StepData(4, null, null, LineProblem.A, 0.0, LineProblem.a), sp.step());
    assertEquals(new StepData(5, LineProblem.A, LineProblem.a, LineProblem.B, 0.0, LineProblem.a), sp.step());
  }

  @Test
  public void testRandomWalkRightTrajectory() {
    RandomWalk sp = new RandomWalk(rightPolicy);
    assertEquals(new StepData(0, null, null, RandomWalk.C, 0.0, RandomWalk.right), sp.step());
    assertEquals(new StepData(1, RandomWalk.C, RandomWalk.right, RandomWalk.D, 0.0, RandomWalk.right), sp.step());
    assertEquals(new StepData(2, RandomWalk.D, RandomWalk.right, RandomWalk.E, 0.0, RandomWalk.right), sp.step());
    assertEquals(new StepData(3, RandomWalk.E, RandomWalk.right, null, 1.0, null), sp.step());
    assertEquals(new StepData(4, null, null, RandomWalk.C, 0.0, RandomWalk.right), sp.step());
    assertEquals(new StepData(5, RandomWalk.C, RandomWalk.right, RandomWalk.D, 0.0, RandomWalk.right), sp.step());
  }

  @Test
  public void testRandomWalkLeftTrajectory() {
    RandomWalk sp = new RandomWalk(leftPolicy);
    assertEquals(new StepData(0, null, null, RandomWalk.C, 0.0, RandomWalk.left), sp.step());
    assertEquals(new StepData(1, RandomWalk.C, RandomWalk.left, RandomWalk.B, 0.0, RandomWalk.left), sp.step());
    assertEquals(new StepData(2, RandomWalk.B, RandomWalk.left, RandomWalk.A, 0.0, RandomWalk.left), sp.step());
    assertEquals(new StepData(3, RandomWalk.A, RandomWalk.left, null, 0.0, null), sp.step());
    assertEquals(new StepData(4, null, null, RandomWalk.C, 0.0, RandomWalk.left), sp.step());
    assertEquals(new StepData(5, RandomWalk.C, RandomWalk.left, RandomWalk.B, 0.0, RandomWalk.left), sp.step());
  }

  @Test
  public void testComputeSolution() {
    RandomWalk sp = new RandomWalk(new Random(0));
    FSGAgentState state = new FSGAgentState(sp);
    double[] solution = state.computeSolution(sp.policy(), 0.9, 0.0);
    checkEquals(sp.expectedDiscountedSolution(), solution);
    checkEquals(new double[] { 1 / 6.0, 2 / 6.0, 3 / 6.0, 4 / 6.0, 5 / 6.0 },
                state.computeSolution(sp.policy(), 1.0, 0.5));
  }

  private void checkEquals(double[] expected, double[] solution) {
    for (int i = 1; i < solution.length; i++)
      assertEquals(expected[i], solution[i], 0.1);
  }
}
