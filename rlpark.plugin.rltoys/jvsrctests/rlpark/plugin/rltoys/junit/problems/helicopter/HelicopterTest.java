package rlpark.plugin.rltoys.junit.problems.helicopter;

import java.util.Random;

import junit.framework.Assert;

import org.junit.Test;

import rlpark.plugin.rltoys.envio.actions.ActionArray;
import rlpark.plugin.rltoys.envio.rl.TRStep;
import rlpark.plugin.rltoys.problems.helicopter.Helicopter;

public class HelicopterTest {
  @Test
  public void testCrash() {
    Helicopter helicopter = new Helicopter(new Random(0));
    Assert.assertEquals(4, helicopter.actionRanges().length);
    final ActionArray action = new ActionArray(0, 0, 0, 0);
    TRStep step = helicopter.initialize();
    while (!step.isEpisodeEnding()) {
      step = helicopter.step(action);
      Assert.assertTrue(step.r_tp1 <= 0);
    }
  }

  @Test
  public void testEndEpisode() {
    Helicopter helicopter = new Helicopter(new Random(0), 2);
    Assert.assertEquals(4, helicopter.actionRanges().length);
    final ActionArray action = new ActionArray(0, 0, 0, 0);
    TRStep step = helicopter.initialize();
    while (!step.isEpisodeEnding()) {
      step = helicopter.step(action);
      Assert.assertTrue(step.r_tp1 <= 0);
    }
    Assert.assertEquals(step.time, 2);
  }

}
