package rlpark.plugin.rltoys.junit.problems.mountaincar;

import java.util.Random;

import junit.framework.Assert;

import org.junit.Test;

import rlpark.plugin.rltoys.envio.rl.RLAgent;
import rlpark.plugin.rltoys.envio.rl.TRStep;
import rlpark.plugin.rltoys.problems.mountaincar.MountainCar;
import rlpark.plugin.rltoys.problems.mountaincar.MountainCarBehaviourPolicy;


public class MountainCarBehaviourPolicyTest {

  @Test
  public void testMountainCarBehaviourPolicyAgent() {
    Random random = new Random(0);
    MountainCar mcar = new MountainCar(random);
    RLAgent agent = new MountainCarBehaviourPolicy(mcar, random, 0.1);
    TRStep step;
    for (int i = 0; i < 10; i++) {
      step = mcar.initialize();
      do {
        step = mcar.step(agent.getAtp1(step));
        Assert.assertTrue(step.time < 200);
      } while (step.isEpisodeEnding());
    }
  }

}
