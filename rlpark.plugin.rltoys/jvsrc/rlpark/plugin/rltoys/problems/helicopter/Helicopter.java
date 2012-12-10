package rlpark.plugin.rltoys.problems.helicopter;

import java.util.Random;

import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.actions.ActionArray;
import rlpark.plugin.rltoys.envio.observations.Legend;
import rlpark.plugin.rltoys.envio.rl.TRStep;
import rlpark.plugin.rltoys.math.ranges.Range;
import rlpark.plugin.rltoys.problems.ProblemBounded;
import rlpark.plugin.rltoys.problems.ProblemContinuousAction;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

public class Helicopter implements ProblemBounded, ProblemContinuousAction {
  static final private Legend legend = new Legend("VelocityX", "VelocityY", "VelocityZ", "PositionX", "PositionY",
                                                  "PositionZ", "AngularVelocityX", "AngularVelocityY",
                                                  "AngularVelocityZ", "ErrorQuaternionX", "ErrorQuaternionY",
                                                  "ErrorQuaternionZ");
  @Monitor
  private final HelicopterDynamics heliDynamics;
  private TRStep step;
  private final int episodeLength;
  static private final int DefaultEpisodeLength = 6000;

  public Helicopter(Random random) {
    this(random, DefaultEpisodeLength);
  }

  public Helicopter(Random random, int episodeLength) {
    this.episodeLength = episodeLength;
    heliDynamics = new HelicopterDynamics(random);
  }

  @Override
  public TRStep initialize() {
    heliDynamics.reset();
    step = new TRStep(heliDynamics.getObservation(), computeReward());
    return step;
  }

  private double computeReward() {
    if (heliDynamics.isCrashed())
      return computeTerminalReward();
    double reward = 0;
    reward -= heliDynamics.velocity.x * heliDynamics.velocity.x;
    reward -= heliDynamics.velocity.y * heliDynamics.velocity.y;
    reward -= heliDynamics.velocity.z * heliDynamics.velocity.z;
    reward -= heliDynamics.position.x * heliDynamics.position.x;
    reward -= heliDynamics.position.y * heliDynamics.position.y;
    reward -= heliDynamics.position.z * heliDynamics.position.z;
    reward -= heliDynamics.angularRate.x * heliDynamics.angularRate.x;
    reward -= heliDynamics.angularRate.y * heliDynamics.angularRate.y;
    reward -= heliDynamics.angularRate.z * heliDynamics.angularRate.z;
    reward -= heliDynamics.q.x * heliDynamics.q.x;
    reward -= heliDynamics.q.y * heliDynamics.q.y;
    reward -= heliDynamics.q.z * heliDynamics.q.z;
    return reward;
  }

  private double computeTerminalReward() {
    double reward = -3.0f
        * HelicopterDynamics.MaxPos
        * HelicopterDynamics.MaxPos
        + -3.0f
        * HelicopterDynamics.MaxRate
        * HelicopterDynamics.MaxRate
        + -3.0f
        * HelicopterDynamics.MaxVel
        * HelicopterDynamics.MaxVel
        - (1.0f - HelicopterDynamics.MIN_QW_BEFORE_HITTING_TERMINAL_STATE
            * HelicopterDynamics.MIN_QW_BEFORE_HITTING_TERMINAL_STATE);
    reward *= episodeLength - step.time;
    return reward;
  }

  @Override
  public TRStep step(Action action) {
    heliDynamics.step((ActionArray) action);
    step = new TRStep(step, action, heliDynamics.getObservation(), computeReward());
    if (heliDynamics.isCrashed() || step.time == episodeLength)
      forceEndEpisode();
    return step;
  }

  @Override
  public TRStep forceEndEpisode() {
    step = step.createEndingStep();
    return step;
  }

  @Override
  public TRStep lastStep() {
    return step;
  }

  @Override
  public Legend legend() {
    return legend;
  }

  @Override
  public Range[] actionRanges() {
    return HelicopterDynamics.ActionRanges;
  }

  @Override
  public Range[] getObservationRanges() {
    return HelicopterDynamics.ObservationRanges;
  }
}
