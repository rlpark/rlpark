package rlpark.plugin.rltoys.problems.pendulum;

import java.util.Random;

import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.actions.ActionArray;
import rlpark.plugin.rltoys.envio.observations.Legend;
import rlpark.plugin.rltoys.envio.observations.TRStep;
import rlpark.plugin.rltoys.envio.problems.ProblemBounded;
import rlpark.plugin.rltoys.envio.problems.ProblemContinuousAction;
import rlpark.plugin.rltoys.envio.problems.ProblemDiscreteAction;
import rlpark.plugin.rltoys.math.ranges.Range;
import rlpark.plugin.rltoys.utils.Utils;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

public class SwingPendulum implements ProblemBounded, ProblemDiscreteAction, ProblemContinuousAction {
  public static final double uMax = 2.0;
  public boolean constantEpisodeTime = true;
  public static final ActionArray STOP = new ActionArray(0);
  public static final ActionArray RIGHT = new ActionArray(uMax);
  public static final ActionArray LEFT = new ActionArray(-uMax);
  private static final Action[] Actions = new Action[] { LEFT, STOP, RIGHT };
  public static final Range ActionRange = new Range(-uMax, uMax);
  protected static final String VELOCITY = "velocity";
  protected static final String THETA = "theta";
  protected static final Legend legend = new Legend(THETA, VELOCITY);
  protected static final Range thetaRange = new Range(-Math.PI, Math.PI);
  protected static final double mass = 1.0;
  protected static final double length = 1.0;
  protected static final double g = 9.8;
  protected static final double stepTime = 0.01; // seconds
  protected static final double requiredUpTime = 10.0; // seconds
  protected static final double isUpRange = Math.PI / 4.0; // seconds
  protected static final double maxVelocity = (Math.PI / 4.0) / stepTime;
  protected static final Range velocityRange = new Range(-maxVelocity, maxVelocity);
  protected static final Range initialThetaRange = new Range(-Math.PI, Math.PI);
  protected static final double initialVelocity = 0.0;

  final private boolean endOfEpisode;
  @Monitor
  protected double theta = 0.0;
  @Monitor
  protected double velocity = 0.0;
  protected final Random random;
  protected TRStep step;
  protected int upTime = 0;

  public SwingPendulum(Random random) {
    this(random, true);
  }

  public SwingPendulum(Random random, boolean endOfEpisode) {
    assert mass * length * g > uMax;
    this.random = random;
    this.endOfEpisode = endOfEpisode;
  }

  protected void update(ActionArray action) {
    double torque = ActionRange.bound(ActionArray.toDouble(action));
    assert Utils.checkValue(torque);
    double thetaAcceleration = -stepTime * velocity + mass * g * length * Math.sin(theta) + torque;
    assert Utils.checkValue(thetaAcceleration);
    velocity = velocityRange.bound(velocity + thetaAcceleration);
    theta += velocity * stepTime;
    adjustTheta();
    upTime = Math.abs(theta) > isUpRange ? 0 : upTime + 1;
    assert Utils.checkValue(theta);
    assert Utils.checkValue(velocity);
  }

  protected void adjustTheta() {
    if (theta >= Math.PI)
      theta -= 2 * Math.PI;
    if (theta < -Math.PI)
      theta += 2 * Math.PI;
  }

  @Override
  public TRStep step(Action action) {
    assert !step.isEpisodeEnding();
    update((ActionArray) action);
    step = new TRStep(step, action, new double[] { theta, velocity }, reward());
    if (isGoalReached())
      forceEndEpisode();
    return step;
  }

  protected double reward() {
    return Math.cos(theta);
  }

  private boolean isGoalReached() {
    if (!endOfEpisode)
      return false;
    if (constantEpisodeTime)
      return false;
    return upTime + 1 >= requiredUpTime / stepTime;
  }

  @Override
  public TRStep forceEndEpisode() {
    step = step.createEndingStep();
    return step;
  }

  @Override
  public TRStep initialize() {
    initializeProblemData();
    step = new TRStep(new double[] { theta, velocity }, -1);
    return step;
  }

  protected void initializeProblemData() {
    upTime = 0;
    if (random == null) {
      theta = Math.PI / 2;
      velocity = 0.0;
    } else {
      theta = initialThetaRange.choose(random);
      velocity = initialVelocity;
    }
    adjustTheta();
  }

  @Override
  public Legend legend() {
    return legend;
  }

  @Override
  public Range[] getObservationRanges() {
    return new Range[] { thetaRange, velocityRange };
  }

  public double theta() {
    return theta;
  }

  public double velocity() {
    return velocity;
  }

  @Override
  public Action[] actions() {
    return Actions;
  }

  @Override
  public Range[] actionRanges() {
    return new Range[] { ActionRange };
  }

  @Override
  public TRStep lastStep() {
    return step;
  }

}
