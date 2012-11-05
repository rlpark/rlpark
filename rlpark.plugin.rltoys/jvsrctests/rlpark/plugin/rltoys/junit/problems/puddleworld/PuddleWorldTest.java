package rlpark.plugin.rltoys.junit.problems.puddleworld;

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import rlpark.plugin.rltoys.algorithms.functions.ContinuousFunction;
import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.actions.ActionArray;
import rlpark.plugin.rltoys.envio.rl.TRStep;
import rlpark.plugin.rltoys.math.ranges.Range;
import rlpark.plugin.rltoys.problems.puddleworld.PuddleWorld;
import rlpark.plugin.rltoys.problems.puddleworld.TargetReachedL2NormTermination;
import rlpark.plugin.rltoys.utils.Utils;

public class PuddleWorldTest {
  class TestRewardFunction implements ContinuousFunction {
    @Override
    public double value(double[] position) {
      double sum = 0.0;
      for (int i = 0; i < position.length; i++)
        sum += position[i];
      return sum;
    }
  }

  private PuddleWorld createProblem(Random random) {
    Range observationRange = new Range(-50, 50);
    Range actionRange = new Range(-1, 1);
    double noise = .1;
    PuddleWorld world = new PuddleWorld(random, 2, observationRange, actionRange, noise);
    world.setStart(new double[] { -49, -49 });
    world.setRewardFunction(new TestRewardFunction());
    world.setTermination(new TargetReachedL2NormTermination(new double[] { 49, 49 }, actionRange.max() + 2 * noise));
    return world;
  }

  @Test
  public void testDiscreteActions() {
    Random random = new Random(0);
    Range observationRange = new Range(-50, 50);
    Range actionRange = new Range(-1, 1);
    PuddleWorld world = new PuddleWorld(random, 2, observationRange, actionRange, .1);
    Action[] actions = world.actions();
    Assert.assertEquals(5, actions.length);
    Assert.assertArrayEquals(new double[] { -1, 0 }, toArray(actions[0]), 0);
    Assert.assertArrayEquals(new double[] { 1, 0 }, toArray(actions[1]), 0);
    Assert.assertArrayEquals(new double[] { 0, -1 }, toArray(actions[2]), 0);
    Assert.assertArrayEquals(new double[] { 0, 1 }, toArray(actions[3]), 0);
    Assert.assertArrayEquals(new double[] { 0, 0 }, toArray(actions[4]), 0);
  }

  private double[] toArray(Action action) {
    return ((ActionArray) action).actions;
  }

  @Test
  public void testEpisodeWithContinuousAction() {
    Random random = new Random(0);
    PuddleWorld world = createProblem(random);
    TRStep step = world.initialize();
    while (!step.isEpisodeEnding()) {
      Assert.assertEquals(step.o_tp1[0] + step.o_tp1[1], step.r_tp1, 0.0);
      step = world.step(new ActionArray(100, 100));
    }
    Assert.assertTrue(step.time > 70);
  }

  @Test
  public void testEpisodeWithDiscreteAction() {
    Random random = new Random(0);
    PuddleWorld world = createProblem(random);
    Action[] actions = world.actions();
    TRStep step = world.initialize();
    while (!step.isEpisodeEnding()) {
      Assert.assertEquals(step.o_tp1[0] + step.o_tp1[1], step.r_tp1, 0.0);
      step = world.step(Utils.choose(random, actions));
    }
    Assert.assertTrue(step.time > 70);
  }
}
