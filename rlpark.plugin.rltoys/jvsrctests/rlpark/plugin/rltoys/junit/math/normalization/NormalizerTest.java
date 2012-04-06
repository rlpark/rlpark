package rlpark.plugin.rltoys.junit.math.normalization;

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import rlpark.plugin.rltoys.math.normalization.IncMeanVarNormalizer;

public class NormalizerTest {

  private static final double EPSILON = 0.00001;
  static private Random random = new Random(0);

  @Test
  public void testNormalize() {
    double[] datas = getData(100, 45, 5);
    IncMeanVarNormalizer normalizer = new IncMeanVarNormalizer();
    for (double data : datas)
      normalizer.update(data);
    checkMeanVar(datas, normalizer);
  }

  private void checkMeanVar(double[] datas, IncMeanVarNormalizer normalizer) {
    int n = 0;
    double sum = 0;
    double sum_sqr = 0;

    for (double x : datas) {
      n = n + 1;
      sum = sum + x;
      sum_sqr = sum_sqr + x * x;
    }

    double mean = sum / n;
    double variance = (sum_sqr - n * mean * mean) / (n - 1);

    Assert.assertEquals(mean, normalizer.average().mean(), EPSILON);
    Assert.assertEquals(variance, normalizer.average().variance(1), EPSILON);
  }

  private double[] getData(int n, double m, double v) {
    double[] datas = new double[n];
    for (int i = 0; i < datas.length; i++)
      datas[i] = random.nextDouble() * v * 2 - v + m;
    return datas;
  }
}
