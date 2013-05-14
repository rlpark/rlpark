package rlpark.plugin.rltoys.junit.algorithms.predictions.td;

import java.util.Arrays;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import rlpark.plugin.rltoys.algorithms.predictions.td.TDErrorMonitor;

public class TDErrorMonitorTest {
  static private final int RewardVariance = 10;
  private final Random random = new Random(0);

  private double[] createProblem(int problemSize) {
    double[] problem = new double[problemSize];
    for (int i = 0; i < problem.length; i++)
      problem[i] = (random.nextDouble() - .5) * RewardVariance;
    return problem;
  }

  private double[] computeSolution(double gamma, double[] problem) {
    double[] solution = new double[problem.length];
    for (int i = 0; i < solution.length - 1; i++) {
      double currentGamma = 1.0;
      for (int j = i + 1; j < solution.length; j++) {
        solution[i] += currentGamma * problem[j];
        currentGamma *= gamma;
      }
    }
    return solution;
  }

  @Test
  public void testNextStepPrediction() {
    testTDErrorMonitor(0, 1e-6, 10);
  }

  @Test
  public void testTDPrediction() {
    testTDErrorMonitor(0.99, 1e-6, 100);
  }

  private void testTDErrorMonitor(double gamma, double precision, int problemSize) {
    double[] problem = createProblem(problemSize);
    double[] solution = computeSolution(gamma, problem);
    TDErrorMonitor monitor = new TDErrorMonitor(gamma, precision);
    problem = Arrays.copyOf(problem, problemSize + 3 * monitor.bufferSize());
    Arrays.fill(problem, problemSize + monitor.bufferSize(), problemSize + 2 * monitor.bufferSize(), 2.0);
    solution = Arrays.copyOf(solution, problem.length);
    checkOverOneEpisode(precision, problemSize, problem, solution, monitor);
    checkOverOneEpisode(precision, problemSize, problem, solution, monitor);
  }

  private void checkOverOneEpisode(double precision, int problemSize, double[] problem, double[] solution,
      TDErrorMonitor monitor) {
    int nbPredictionChecked = 0;
    for (int state = 0; state < problem.length - 1; state++) {
      monitor.update(solution[state], problem[state + 1], state == problem.length - 2);
      if (!monitor.errorComputed())
        continue;
      if (nbPredictionChecked < problemSize) {
        Assert.assertEquals(0, monitor.error(), precision);
        nbPredictionChecked++;
      } else
        Assert.assertTrue(monitor.error() > 0);
    }
    Assert.assertEquals(problemSize, nbPredictionChecked);
  }
}
