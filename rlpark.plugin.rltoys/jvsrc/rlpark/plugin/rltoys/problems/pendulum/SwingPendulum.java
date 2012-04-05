package rlpark.plugin.rltoys.problems.pendulum;

import java.util.Random;

import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.actions.ActionArray;
import rlpark.plugin.rltoys.envio.observations.Legend;
import rlpark.plugin.rltoys.envio.rl.TRStep;
import rlpark.plugin.rltoys.math.ranges.Range;
import rlpark.plugin.rltoys.problems.ProblemBounded;
import rlpark.plugin.rltoys.problems.ProblemContinuousAction;
import rlpark.plugin.rltoys.problems.ProblemDiscreteAction;
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
  protected static final Legend Legend = new Legend(THETA, VELOCITY);
  public static final Range ThetaRange = new Range(-Math.PI, Math.PI);
  protected static final double Mass = 1.0;
  protected static final double Length = 1.0;
  protected static final double G = 9.8;
  protected static final double StepTime = 0.01; // seconds
  protected static final double RequiredUpTime = 10.0; // seconds
  protected static final double UpRange = Math.PI / 4.0; // seconds
  protected static final double MaxVelocity = (Math.PI / 4.0) / StepTime;
  public static final Range VelocityRange = new Range(-MaxVelocity, MaxVelocity);
  public static final Range InitialThetaRange = new Range(-Math.PI, Math.PI);
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
    assert Mass * Length * G > uMax;
    this.random = random;
    this.endOfEpisode = endOfEpisode;
  }

  protected void update(ActionArray action) {
    double torque = ActionRange.bound(ActionArray.toDouble(action));
    assert Utils.checkValue(torque);
    double thetaAcceleration = -StepTime * velocity + Mass * G * Length * Math.sin(theta) + torque;
    assert Utils.checkValue(thetaAcceleration);
    velocity = VelocityRange.bound(velocity + thetaAcceleration);
    theta += velocity * StepTime;
    adjustTheta();
    upTime = Math.abs(theta) > UpRange ? 0 : upTime + 1;
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
    return upTime + 1 >= RequiredUpTime / StepTime;
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
      theta = InitialThetaRange.choose(random);
      velocity = initialVelocity;
    }
    adjustTheta();
  }

  @Override
  public Legend legend() {
    return Legend;
  }

  @Override
  public Range[] getObservationRanges() {
    return new Range[] { ThetaRange, VelocityRange };
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
