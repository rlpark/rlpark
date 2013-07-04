package rlpark.plugin.rltoys.junit.algorithms.control.actorcritic.policystructure;

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import rlpark.plugin.rltoys.algorithms.functions.policydistributions.PolicyDistribution;
import rlpark.plugin.rltoys.algorithms.functions.policydistributions.helpers.ScaledPolicyDistribution;
import rlpark.plugin.rltoys.algorithms.functions.policydistributions.structures.JointDistribution;
import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.actions.ActionArray;
import rlpark.plugin.rltoys.math.ranges.Range;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.math.vector.implementations.PVector;

public class ScaledPolicyDistributionTest {
  final Range policyRange = new Range(0, 1);

  @SuppressWarnings("serial")
  class PolicyDistributionTest implements PolicyDistribution {
    private final Random random = new Random(0);

    @Override
    public double pi(Action a) {
      ActionArray action = (ActionArray) a;
      Assert.assertEquals(1, action.actions.length);
      Assert.assertTrue(policyRange.in(action.actions[0]));
      return 1;
    }

    @Override
    public Action sampleAction() {
      return new ActionArray(policyRange.choose(random));
    }

    @Override
    public PVector[] createParameters(int nbFeatures) {
      return new PVector[] {};
    }

    @Override
    public RealVector[] computeGradLog(Action a_t) {
      ActionArray action = (ActionArray) a_t;
      Assert.assertEquals(1, action.actions.length);
      Assert.assertTrue(policyRange.in(action.actions[0]));
      return new PVector[] {};
    }

    @Override
    public int nbParameterVectors() {
      return 0;
    }

    @Override
    public void update(RealVector x) {
    }
  }

  @Test
  public void testScaledPolicy() {
    PolicyDistribution pi01 = new PolicyDistributionTest();
    PolicyDistribution pi02 = new PolicyDistributionTest();
    Range[] actionRanges = new Range[] { new Range(4, 5), new Range(-1000, -100) };
    ScaledPolicyDistribution[] scaledPolicies = new ScaledPolicyDistribution[] {
        new ScaledPolicyDistribution(pi01, policyRange, actionRanges[0]),
        new ScaledPolicyDistribution(pi02, policyRange, actionRanges[1]) };
    JointDistribution scaledPolicy = new JointDistribution(scaledPolicies);
    for (int n = 0; n < 10000; n++) {
      ActionArray a = scaledPolicy.sampleAction();
      for (int a_i = 0; a_i < actionRanges.length; a_i++)
        Assert.assertTrue(actionRanges[a_i].in(a.actions[a_i]));
      Assert.assertEquals(1.0, scaledPolicy.pi(a), 0);
      scaledPolicy.computeGradLog(a);
    }
  }
}
