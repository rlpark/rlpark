package rlpark.plugin.rltoys.junit.demons;

import org.junit.Assert;
import org.junit.Test;

import rlpark.plugin.rltoys.algorithms.predictions.td.TD;
import rlpark.plugin.rltoys.algorithms.predictions.td.TDErrorMonitor;
import rlpark.plugin.rltoys.horde.demons.PredictionDemon;
import rlpark.plugin.rltoys.horde.demons.PredictionDemonVerifier;
import rlpark.plugin.rltoys.horde.functions.RewardFunction;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.math.vector.implementations.BVector;
import rlpark.plugin.rltoys.math.vector.implementations.PVector;


@SuppressWarnings("serial")
public class PredictionDemonTest {
  static interface RewardFunctionTest extends RewardFunction {
    void update(int time);
  }

  static final private RewardFunction RewardFunction01 = new RewardFunctionTest() {
    @Override
    public double reward() {
      return 2.0;
    }

    @Override
    public void update(int time) {
    }
  };

  static class CustomRewardFunction implements RewardFunctionTest {
    private final int bufferSize;
    private double reward;

    public CustomRewardFunction(int bufferSize) {
      this.bufferSize = bufferSize;
    }

    @Override
    public void update(int time) {
      reward = (time % bufferSize) * 100 + 100;
    }

    @Override
    public double reward() {
      return reward;
    }
  }

  static interface TimeToState {
    RealVector get(int time);
  }

  private final TimeToState noState = new TimeToState() {
    @Override
    public RealVector get(int time) {
      return new PVector(1.0);
    }
  };

  @Test
  public void testPredictionDemon() {
    TD td = new TD(0.0, 0.1, 1);
    PredictionDemon predictionDemon = new PredictionDemon(RewardFunction01, td);
    PredictionDemonVerifier verifier = new PredictionDemonVerifier(predictionDemon);
    runExperiment(predictionDemon, verifier);
    Assert.assertEquals(RewardFunction01.reward(), predictionDemon.prediction(), 1.0);
  }

  private void runExperiment(PredictionDemon predictionDemon, PredictionDemonVerifier verifier) {
    runExperiment(predictionDemon, verifier, noState, 1000);
  }

  protected void runExperiment(PredictionDemon predictionDemon, PredictionDemonVerifier demonVerifier,
      TimeToState timeToState, int maxStep) {
    RealVector x_t = null;
    int time = 0;
    TDErrorMonitor verifier = demonVerifier.errorMonitor();
    while (!verifier.errorComputed() || Math.abs(verifier.error()) >= verifier.precision()) {
      RealVector x_tp1 = timeToState.get(time);
      ((RewardFunctionTest) predictionDemon.rewardFunction()).update(time);
      predictionDemon.update(x_t, null, x_tp1);
      demonVerifier.update(false);
      x_t = x_tp1;
      time++;
      Assert.assertTrue(time < maxStep);
    }
  }

  @Test
  public void testPredictionDemonGamma09() {
    double gamma = 0.9;
    TD td = new TD(gamma, 0.1, 1);
    PredictionDemon predictionDemon = new PredictionDemon(RewardFunction01, td);
    PredictionDemonVerifier verifier = new PredictionDemonVerifier(predictionDemon);
    runExperiment(predictionDemon, verifier);
    Assert.assertEquals(RewardFunction01.reward() / (1 - gamma), predictionDemon.prediction(), 1.0);
  }

  @Test
  public void testPredictionDemonGamma09MultipleState() {
    final int bufferSize = 50;
    double gamma = 0.9;
    TD td = new TD(gamma, 0.1, bufferSize);
    CustomRewardFunction rewardFunction = new CustomRewardFunction(bufferSize);
    PredictionDemon predictionDemon = new PredictionDemon(rewardFunction, td);
    PredictionDemonVerifier verifier = new PredictionDemonVerifier(predictionDemon);
    TimeToState timeToState = new TimeToState() {
      @Override
      public RealVector get(int time) {
        return BVector.toBVector(bufferSize, new int[] { time % bufferSize });
      }
    };
    runExperiment(predictionDemon, verifier, timeToState, 1000 * bufferSize);
  }
}
