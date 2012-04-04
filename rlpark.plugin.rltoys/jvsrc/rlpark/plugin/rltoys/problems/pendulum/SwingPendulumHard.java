package rlpark.plugin.rltoys.problems.pendulum;

import java.util.Random;

import rlpark.plugin.rltoys.envio.observations.TRStep;
import rlpark.plugin.rltoys.math.ranges.Range;

public class SwingPendulumHard extends SwingPendulum {

  private final double rewardLimit = Math.cos(Math.PI / 8.0);
  protected final Range initialVelocityRange = velocityRange;

  public SwingPendulumHard(Random random) {
    super(random);
  }

  @Override
  protected double reward() {
    double currentReward = Math.cos(theta);
    if (currentReward < rewardLimit)
      return -1.0;
    return currentReward;
  }

  @Override
  public TRStep initialize() {
    upTime = 0;
    theta = initialThetaRange.choose(random);
    velocity = 0;
    adjustTheta();
    step = new TRStep(new double[] { theta, velocity }, -1);
    return step;
  }
}
