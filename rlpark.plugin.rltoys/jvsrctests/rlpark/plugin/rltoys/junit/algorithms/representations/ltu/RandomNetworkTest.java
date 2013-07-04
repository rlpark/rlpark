package rlpark.plugin.rltoys.junit.algorithms.representations.ltu;

import static rlpark.plugin.rltoys.math.vector.implementations.BVector.toBVector;

import java.util.Random;

import org.junit.Assert;

import org.junit.Test;

import rlpark.plugin.rltoys.algorithms.predictions.supervised.Adaline;
import rlpark.plugin.rltoys.algorithms.predictions.supervised.LearningAlgorithm;
import rlpark.plugin.rltoys.algorithms.representations.ltu.networks.AutoRegulatedNetwork;
import rlpark.plugin.rltoys.algorithms.representations.ltu.networks.RandomNetwork;
import rlpark.plugin.rltoys.algorithms.representations.ltu.networks.RandomNetworks;
import rlpark.plugin.rltoys.algorithms.representations.ltu.units.LTUAdaptive;
import rlpark.plugin.rltoys.algorithms.representations.ltu.units.LTUThreshold;
import rlpark.plugin.rltoys.math.vector.BinaryVector;
import rlpark.plugin.rltoys.math.vector.RealVector;


@SuppressWarnings("serial")
public class RandomNetworkTest {
  @Test
  public void testLTUUpdate() {
    int inputSize = 8;
    RandomNetwork randomNetwork = new RandomNetwork(inputSize, 16);
    LTUThreshold ltu = new LTUThreshold(15, new int[] { 0, 2, 4 }, new byte[] { 1, -1, 1 });
    randomNetwork.addLTU(ltu);
    BinaryVector projected = randomNetwork.project(toBVector(inputSize, new int[] {}));
    Assert.assertEquals(0, (int) projected.getEntry(15));
    projected = randomNetwork.project(toBVector(inputSize, new int[] { 0 }));
    Assert.assertEquals(1, (int) projected.getEntry(15));
    projected = randomNetwork.project(toBVector(inputSize, new int[] { 0, 2 }));
    Assert.assertEquals(0, (int) projected.getEntry(15));
    randomNetwork.dispose();
  }

  private BinaryTargetProblem createProblem() {
    return new BinaryTargetProblem(new Random(0), 4, 16);
  }

  @Test
  public void testBinaryInputProblem() {
    BinaryTargetProblem problem = createProblem();
    double mse = problem.evaluateLearning(10000, new LearningAlgorithm() {
      @Override
      public double predict(RealVector x) {
        return 0;
      }

      @Override
      public double learn(RealVector x_t, double y_tp1) {
        return 0;
      }
    });
    Assert.assertTrue(mse > 1.48);
  }

  protected void evaluateRepresentation(BinaryTargetProblem problem, RandomNetwork representation, double maxMse) {
    Adaline lms = new Adaline(representation.outputSize, .05 / representation.outputSize);
    LearningAlgorithm learningAlgorithm = createLearningAlgorithm(representation, lms);
    double mse = problem.evaluateLearning(10000, learningAlgorithm);
    Assert.assertTrue(mse < maxMse);
  }

  private LearningAlgorithm createLearningAlgorithm(final RandomNetwork representation, final LearningAlgorithm learner) {
    return new LearningAlgorithm() {
      private RealVector x_tm1 = null;
      private RealVector ex_tm1 = null;

      private RealVector extend(RealVector x) {
        if (x != x_tm1) {
          x_tm1 = x;
          ex_tm1 = representation.project((BinaryVector) x);
        }
        return ex_tm1;
      }

      @Override
      public double predict(RealVector x) {
        return learner.predict(extend(x));
      }

      @Override
      public double learn(RealVector x_t, double y_tp1) {
        return learner.learn(extend(x_t), y_tp1);
      }
    };
  }

  @Test
  public void testRandomRepresentationWithLMS() {
    BinaryTargetProblem problem = createProblem();
    RandomNetwork randomNetwork = new RandomNetwork(problem.inputSize, 1000);
    RandomNetworks.fullyConnect(new Random(0), randomNetwork, new LTUThreshold());
    evaluateRepresentation(problem, randomNetwork, 0.09);
    randomNetwork.dispose();
  }

  @Test
  public void testRandomRepresentationWithLMSAndAdaptiveLTUs() {
    BinaryTargetProblem problem = createProblem();
    final LTUAdaptive ltu = new LTUAdaptive(0.2, 0.3, 0.99, .001);
    RandomNetwork randomNetwork = new RandomNetwork(problem.inputSize, 1000);
    RandomNetworks.fullyConnect(new Random(0), randomNetwork, ltu);
    evaluateRepresentation(problem, randomNetwork, 0.08);
    randomNetwork.dispose();
  }

  @Test
  public void testAdaptiveRandomRepresentationWithLMSAndAdaptiveLTUs() {
    BinaryTargetProblem problem = createProblem();
    final LTUAdaptive ltu = new LTUAdaptive(0.2, 0.3, 0.99, .001);
    RandomNetwork randomNetwork = new AutoRegulatedNetwork(new Random(0), problem.inputSize, 1000, .2, .3);
    RandomNetworks.fullyConnect(new Random(0), randomNetwork, ltu);
    evaluateRepresentation(problem, randomNetwork, 0.08);
    randomNetwork.dispose();
  }

}
