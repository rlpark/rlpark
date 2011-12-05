package rltoys.algorithms.learning.control.actorcritic.policystructure;

import java.util.Random;

import junit.framework.Assert;

import org.junit.Test;

import rltoys.algorithms.learning.control.actorcritic.policystructure.JointDistribution;
import rltoys.algorithms.learning.control.actorcritic.policystructure.NormalDistribution;
import rltoys.algorithms.representations.acting.PolicyDistribution;
import rltoys.environments.envio.actions.ActionArray;
import rltoys.math.normalization.IncMeanVarNormalizer;
import rltoys.math.vector.implementations.PVector;

public class TestJointDistribution {
  @Test
  public void testJointDistribution() {
    Random random = new Random(0);
    NormalDistribution pi01 = new NormalDistribution(random, 0.25, 1, 0);
    NormalDistribution pi02 = new NormalDistribution(random, 0.75, 1, 0);
    JointDistribution jointDistribution = new JointDistribution(new PolicyDistribution[] { pi01, pi02 });
    Assert.assertEquals(4, jointDistribution.createParameters(1).length);
    IncMeanVarNormalizer[] normalizer = new IncMeanVarNormalizer[] { new IncMeanVarNormalizer(),
        new IncMeanVarNormalizer() };
    for (int t = 0; t < 10000; t++) {
      final PVector x_t = new PVector(new double[] { 1.0 });
      ActionArray a_t = jointDistribution.decide(x_t);
      Assert.assertEquals(4, jointDistribution.getGradLog(x_t, a_t).length);
      double pi = jointDistribution.pi(x_t, a_t);
      Assert.assertTrue(pi >= 0 && pi <= 1);
      for (int i = 0; i < normalizer.length; i++)
        normalizer[i].update(a_t.actions[i]);
    }
    Assert.assertEquals(0.25, normalizer[0].mean(), .05);
    Assert.assertEquals(0.75, normalizer[1].mean(), .05);
  }
}
