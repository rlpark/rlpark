package rlpark.plugin.rltoys.junit.algorithms.control.actorcritic.policystructure;

import java.util.Random;

import org.junit.Assert;

import org.junit.Test;

import rlpark.plugin.rltoys.algorithms.functions.policydistributions.PolicyDistribution;
import rlpark.plugin.rltoys.algorithms.functions.policydistributions.structures.JointDistribution;
import rlpark.plugin.rltoys.algorithms.functions.policydistributions.structures.NormalDistribution;
import rlpark.plugin.rltoys.algorithms.functions.policydistributions.structures.UniformDistribution;
import rlpark.plugin.rltoys.envio.actions.ActionArray;
import rlpark.plugin.rltoys.math.normalization.IncMeanVarNormalizer;
import rlpark.plugin.rltoys.math.ranges.Range;
import rlpark.plugin.rltoys.math.vector.implementations.PVector;

public class TestJointDistribution {
  @Test
  public void testJointDistributionWithUniform() {
    Random random = new Random(0);
    UniformDistribution pi01 = new UniformDistribution(random, new Range(0, 1));
    UniformDistribution pi02 = new UniformDistribution(random, new Range(0, 1));
    JointDistribution jointDistribution = new JointDistribution(new PolicyDistribution[] { pi01, pi02 });
    Assert.assertEquals(1.0, jointDistribution.pi(new ActionArray(.5, .5)), 0);
    Assert.assertEquals(0.0, jointDistribution.pi(new ActionArray(-.5, .5)), 0);
    Assert.assertEquals(0.0, jointDistribution.pi(new ActionArray(.5, -.5)), 0);
  }


  @Test
  public void testJointDistributionWithMeanAndNormal() {
    Random random = new Random(0);
    NormalDistribution pi01 = new NormalDistribution(random, 0.25, 1);
    NormalDistribution pi02 = new NormalDistribution(random, 0.75, 1);
    JointDistribution jointDistribution = new JointDistribution(new PolicyDistribution[] { pi01, pi02 });
    Assert.assertEquals(4, jointDistribution.createParameters(1).length);
    IncMeanVarNormalizer[] normalizer = new IncMeanVarNormalizer[] { new IncMeanVarNormalizer(),
        new IncMeanVarNormalizer() };
    for (int t = 0; t < 10000; t++) {
      final PVector x_t = new PVector(new double[] { 1.0 });
      jointDistribution.update(x_t);
      ActionArray a_t = jointDistribution.sampleAction();
      Assert.assertEquals(4, jointDistribution.computeGradLog(a_t).length);
      double pi = jointDistribution.pi(a_t);
      Assert.assertTrue(pi >= 0 && pi <= 1);
      for (int i = 0; i < normalizer.length; i++)
        normalizer[i].update(a_t.actions[i]);
    }
    Assert.assertEquals(0.25, normalizer[0].mean(), .05);
    Assert.assertEquals(0.75, normalizer[1].mean(), .05);
  }
}
