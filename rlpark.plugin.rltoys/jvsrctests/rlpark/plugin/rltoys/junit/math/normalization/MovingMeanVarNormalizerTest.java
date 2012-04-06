package rlpark.plugin.rltoys.junit.math.normalization;

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import rlpark.plugin.rltoys.math.normalization.IncMeanVarNormalizer;
import rlpark.plugin.rltoys.math.normalization.MovingMeanVarNormalizer;


public class MovingMeanVarNormalizerTest {
  static private final int TrackingSpeed = 1000;
  static private final double Tolerance = 1.0;

  @Test
  public void testMovingMeanVarNormalizer() {
    MovingMeanVarNormalizer normalizer = new MovingMeanVarNormalizer(TrackingSpeed);
    Random random = new Random(0);
    checkNormalizer(random, normalizer, 6.0, 8.0);
    checkNormalizer(random, normalizer, 80.0, 2.0);
  }

  private void checkNormalizer(Random random, MovingMeanVarNormalizer normalizer, double mu, double sigma) {
    for (int i = 0; i < TrackingSpeed * 1000; i++) {
      double x = nextGaussian(random, mu, sigma);
      normalizer.update(x);
    }
    Assert.assertEquals(mu, normalizer.mean(), Tolerance);
    Assert.assertEquals(sigma * sigma, normalizer.var(), Tolerance);
    IncMeanVarNormalizer referenceNormalizer = new IncMeanVarNormalizer();
    for (int i = 0; i < 1000; i++) {
      double x = nextGaussian(random, mu, sigma);
      referenceNormalizer.update(normalizer.normalize(x));
    }
    Assert.assertEquals(0.0, referenceNormalizer.mean(), Tolerance);
    Assert.assertEquals(1.0, referenceNormalizer.var(), Tolerance);
  }

  protected double nextGaussian(Random random, double mu, double sigma) {
    return random.nextGaussian() * sigma + mu;
  }
}
