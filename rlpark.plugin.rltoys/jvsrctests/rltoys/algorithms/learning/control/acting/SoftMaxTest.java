package rltoys.algorithms.learning.control.acting;

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import rltoys.algorithms.learning.predictions.Predictor;
import rltoys.algorithms.representations.actions.Action;
import rltoys.algorithms.representations.actions.TabularAction;
import rltoys.math.vector.RealVector;
import rltoys.math.vector.implementations.PVector;

@SuppressWarnings("serial")
public class SoftMaxTest {
  static final private Action a1 = new Action() {
  };
  static final private Action a2 = new Action() {
  };
  static final private double qa1 = 0.1;
  static final private double qa2 = 0.2;
  static final private Predictor predictor = new Predictor() {
    PVector theta = new PVector(qa1, qa2);

    @Override
    public double predict(RealVector x) {
      return theta.dotProduct(x);
    }
  };

  @Test
  public void testSoftMax() {
    Action[] actions = new Action[] { a1, a2 };
    SoftMax softMax = new SoftMax(new Random(0), predictor, actions, new TabularAction(actions, 1, 1));
    int nbA1 = 0;
    int nbA2 = 0;
    int nbPolls = 1000;
    for (int i = 0; i < nbPolls; i++)
      if (softMax.decide(new PVector(1.0)) == a1)
        nbA1++;
      else
        nbA2++;
    Assert.assertEquals(nbA1 + nbA2, nbPolls);
    Assert.assertEquals(Math.exp(qa1) / (Math.exp(qa1) + Math.exp(qa2)), (double) nbA1 / nbPolls, 0.1);
    Assert.assertEquals(Math.exp(qa2) / (Math.exp(qa1) + Math.exp(qa2)), (double) nbA2 / nbPolls, 0.1);
  }
}
