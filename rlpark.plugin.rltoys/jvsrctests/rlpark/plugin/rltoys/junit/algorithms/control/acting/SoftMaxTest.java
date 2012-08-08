package rlpark.plugin.rltoys.junit.algorithms.control.acting;

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import rlpark.plugin.rltoys.algorithms.control.acting.SoftMax;
import rlpark.plugin.rltoys.algorithms.functions.Predictor;
import rlpark.plugin.rltoys.algorithms.functions.stateactions.TabularAction;
import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.math.vector.implementations.PVector;

@SuppressWarnings("serial")
public class SoftMaxTest {
  class ActionInteger implements Action {
    final int a;

    ActionInteger(int i) {
      this.a = i;
    }
  }

  class ActionPredictor implements Predictor {
    private final RealVector theta;

    ActionPredictor(RealVector theta) {
      this.theta = theta;
    }

    @Override
    public double predict(RealVector x) {
      return theta.dotProduct(x);
    }
  }

  @Test
  public void testSoftMaxUniform() {
    ActionInteger[] actions = createActions(10);
    SoftMax softMax = new SoftMax(new Random(0), new ActionPredictor(new PVector(actions.length)), actions,
                                  new TabularAction(actions, 1, 1));
    softMax.update(new PVector(1.0));
    double[] dist = pollActions(softMax, 100000);
    double expected = 1.0 / actions.length;
    for (int i = 0; i < dist.length; i++)
      Assert.assertEquals(expected, dist[i], 0.01);
  }

  @Test
  public void testSoftMax() {
    Action[] actions = createActions(2);
    final double qa1 = 0.1;
    final double qa2 = 0.2;
    SoftMax softMax = new SoftMax(new Random(0), new ActionPredictor(new PVector(qa1, qa2)), actions,
                                  new TabularAction(actions, 1, 1));
    softMax.update(new PVector(1.0));
    double[] actionDistribution = pollActions(softMax, 1000);
    Assert.assertEquals(Math.exp(qa1) / (Math.exp(qa1) + Math.exp(qa2)), actionDistribution[0], 0.1);
    Assert.assertEquals(Math.exp(qa2) / (Math.exp(qa1) + Math.exp(qa2)), actionDistribution[1], 0.1);
  }

  private double[] pollActions(SoftMax softMax, int nbPolls) {
    int[] dist = new int[softMax.actions().length];
    for (int i = 0; i < nbPolls; i++) {
      ActionInteger a = (ActionInteger) softMax.sampleAction();
      dist[a.a]++;
    }
    double[] result = new double[dist.length];
    for (int i = 0; i < dist.length; i++)
      result[i] = (double) dist[i] / nbPolls;
    return result;
  }

  private ActionInteger[] createActions(int nbAction) {
    ActionInteger[] actions = new ActionInteger[nbAction];
    for (int i = 0; i < actions.length; i++)
      actions[i] = new ActionInteger(i);
    return actions;
  }
}
