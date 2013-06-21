package rlpark.plugin.rltoys.junit.problems.stategraph;

import static org.junit.Assert.assertEquals;

import java.util.Random;

import org.junit.Test;

import rlpark.plugin.rltoys.envio.policy.SingleActionPolicy;
import rlpark.plugin.rltoys.envio.policy.Policy;
import rlpark.plugin.rltoys.problems.stategraph.FSGAgentState;
import rlpark.plugin.rltoys.problems.stategraph.FiniteStateGraph.StepData;
import rlpark.plugin.rltoys.problems.stategraph.LineProblem;
import rlpark.plugin.rltoys.problems.stategraph.RandomWalk;

public class FiniteStateGraphTest {
  static private final Policy leftPolicy = new SingleActionPolicy(RandomWalk.Left);
  static private final Policy rightPolicy = new SingleActionPolicy(RandomWalk.Right);

  @Test
  public void testSimpleProblemTrajectory() {
    LineProblem sp = new LineProblem();
    assertEquals(new StepData(0, null, null, LineProblem.A, 0.0, LineProblem.Move), sp.step());
    assertEquals(new StepData(1, LineProblem.A, LineProblem.Move, LineProblem.B, 0.0, LineProblem.Move), sp.step());
    assertEquals(new StepData(2, LineProblem.B, LineProblem.Move, LineProblem.C, 0.0, LineProblem.Move), sp.step());
    assertEquals(new StepData(3, LineProblem.C, LineProblem.Move, null, 1.0, null), sp.step());
    assertEquals(new StepData(4, null, null, LineProblem.A, 0.0, LineProblem.Move), sp.step());
    assertEquals(new StepData(5, LineProblem.A, LineProblem.Move, LineProblem.B, 0.0, LineProblem.Move), sp.step());
  }

  @Test
  public void testRandomWalkRightTrajectory() {
    RandomWalk sp = new RandomWalk(rightPolicy);
    assertEquals(new StepData(0, null, null, RandomWalk.C, 0.0, RandomWalk.Right), sp.step());
    assertEquals(new StepData(1, RandomWalk.C, RandomWalk.Right, RandomWalk.D, 0.0, RandomWalk.Right), sp.step());
    assertEquals(new StepData(2, RandomWalk.D, RandomWalk.Right, RandomWalk.E, 0.0, RandomWalk.Right), sp.step());
    assertEquals(new StepData(3, RandomWalk.E, RandomWalk.Right, null, 1.0, null), sp.step());
    assertEquals(new StepData(4, null, null, RandomWalk.C, 0.0, RandomWalk.Right), sp.step());
    assertEquals(new StepData(5, RandomWalk.C, RandomWalk.Right, RandomWalk.D, 0.0, RandomWalk.Right), sp.step());
  }

  @Test
  public void testRandomWalkLeftTrajectory() {
    RandomWalk sp = new RandomWalk(leftPolicy);
    assertEquals(new StepData(0, null, null, RandomWalk.C, 0.0, RandomWalk.Left), sp.step());
    assertEquals(new StepData(1, RandomWalk.C, RandomWalk.Left, RandomWalk.B, 0.0, RandomWalk.Left), sp.step());
    assertEquals(new StepData(2, RandomWalk.B, RandomWalk.Left, RandomWalk.A, 0.0, RandomWalk.Left), sp.step());
    assertEquals(new StepData(3, RandomWalk.A, RandomWalk.Left, null, 0.0, null), sp.step());
    assertEquals(new StepData(4, null, null, RandomWalk.C, 0.0, RandomWalk.Left), sp.step());
    assertEquals(new StepData(5, RandomWalk.C, RandomWalk.Left, RandomWalk.B, 0.0, RandomWalk.Left), sp.step());
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
