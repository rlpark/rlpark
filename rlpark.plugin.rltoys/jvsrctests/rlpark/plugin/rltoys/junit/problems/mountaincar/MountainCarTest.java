package rlpark.plugin.rltoys.junit.problems.mountaincar;

import java.util.Random;

import org.junit.Assert;

import org.junit.Test;

import rlpark.plugin.rltoys.envio.rl.TRStep;
import rlpark.plugin.rltoys.math.ranges.Range;
import rlpark.plugin.rltoys.problems.mountaincar.MountainCar;
import rlpark.plugin.rltoys.utils.Utils;


public class MountainCarTest {
  static private final int velocityIndex = MountainCar.legend.indexOf(MountainCar.VELOCITY);

  @Test
  public void testComeBack() {
    Range range = new Range(-1e-3, 1e-3);
    MountainCar mcar = new MountainCar(new Random(0));
    double velocity;
    for (int i = 0; i < 10; i++) {
      mcar.initialize();
      velocity = Double.MAX_VALUE;
      while (!range.in(velocity)) {
        TRStep rewObs = mcar.step(MountainCar.STOP);
        if (rewObs.o_t == null || rewObs.isEpisodeEnding())
          break;
        velocity = rewObs.o_t[velocityIndex];
      }
    }
  }

  @Test
  public void testEndEpisode() {
    MountainCar mcar = new MountainCar(null);
    mcar.setThrottleFactor(3.0);
    for (int i = 0; i < 10; i++) {
      TRStep step = mcar.initialize();
      Assert.assertTrue(step.isEpisodeStarting());
      do {
        step = mcar.step(MountainCar.RIGHT);
        if (!step.isEpisodeEnding())
          Assert.assertTrue(step.time < 200);
        else
          Assert.assertTrue(step.time > 4);
      } while (!step.isEpisodeEnding());
    }
  }

  @Test
  public void testRandomAgent() {
    Random random = new Random(0);
    MountainCar mcar = new MountainCar(random);
    TRStep step;
    for (int i = 0; i < 10; i++) {
      step = mcar.initialize();
      do {
        step = mcar.step(Utils.choose(random, mcar.actions()));
        Assert.assertTrue(step.time < 200000);
      } while (!step.isEpisodeEnding());
    }
  }
}
