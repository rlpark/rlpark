package rltoys.environments.continuousgridworld;

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import rltoys.environments.envio.actions.ActionArray;
import rltoys.environments.envio.observations.TRStep;
import rltoys.math.ranges.Range;

public class TestContinuousGridworld {
  class TestRewardFunction implements ContinuousFunction {
    private final int nbDim;

    public TestRewardFunction(int nbDim) {
      this.nbDim = nbDim;
    }

    @Override
    public double fun(double[] position) {
      double sum = 0.0;
      for (int i = 0; i < position.length; i++)
        sum += position[i];
      return sum;
    }

    @Override
    public int nbDimension() {
      return nbDim;
    }

    @Override
    public Range range() {
      return null;
    }
  }

  @Test
  public void testEpisode() {
    Random random = new Random(0);
    double[] start = new double[] { -49, -49 };
    double[] target = new double[] { 49, 49 };
    ContinuousGridworld world = new ContinuousGridworld(random, start, target, new TestRewardFunction(start.length));
    TRStep step = world.initialize();
    while (!step.isEpisodeEnding()) {
      Assert.assertEquals(step.o_tp1[0] + step.o_tp1[1], step.r_tp1, 0.0);
      step = world.step(new ActionArray(100, 100));
    }
    Assert.assertTrue(step.time > 70);
  }
}
