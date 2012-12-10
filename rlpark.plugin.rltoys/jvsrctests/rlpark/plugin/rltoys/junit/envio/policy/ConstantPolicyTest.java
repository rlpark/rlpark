package rlpark.plugin.rltoys.junit.envio.policy;

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.policy.ConstantPolicy;
import rlpark.plugin.rltoys.envio.policy.Policies;

@SuppressWarnings("serial")
public class ConstantPolicyTest {
  @Test
  public void testConstantDistribution() {
    Action a = new Action() {
    };
    Action b = new Action() {
    };
    Action c = new Action() {
    };
    double pa = 0.3, pb = 0.6, pc = 0.1;
    ConstantPolicy policy = new ConstantPolicy(new Random(0), new Action[] { a, b, c }, new double[] { pa, pb, pc });
    int nbSample = 1000;
    double na = 0, nb = 0, nc = 0;
    for (int i = 0; i < nbSample; i++) {
      Action action = Policies.decide(policy, null);
      if (action == a)
        na++;
      else if (action == b)
        nb++;
      else if (action == c)
        nc++;
    }
    Assert.assertEquals(pa, na / nbSample, 0.1);
    Assert.assertEquals(pb, nb / nbSample, 0.1);
    Assert.assertEquals(pc, nc / nbSample, 0.1);
  }
}
