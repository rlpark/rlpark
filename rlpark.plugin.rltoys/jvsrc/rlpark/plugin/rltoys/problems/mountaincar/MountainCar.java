package rlpark.plugin.rltoys.problems.mountaincar;

import java.util.Random;

import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.actions.ActionArray;
import rlpark.plugin.rltoys.envio.observations.Legend;
import rlpark.plugin.rltoys.envio.rl.TRStep;
import rlpark.plugin.rltoys.math.ranges.Range;
import rlpark.plugin.rltoys.problems.ProblemBounded;
import rlpark.plugin.rltoys.problems.ProblemContinuousAction;
import rlpark.plugin.rltoys.problems.ProblemDiscreteAction;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

public class MountainCar implements ProblemBounded, ProblemDiscreteAction, ProblemContinuousAction {
  static private final double MaxActionValue = 1.0;
  public static final ActionArray LEFT = new ActionArray(-MaxActionValue);
  public static final ActionArray RIGHT = new ActionArray(MaxActionValue);
  public static final ActionArray STOP = new ActionArray(0.0);
  protected static final Action[] Actions = { LEFT, STOP, RIGHT };
  static public final Range ActionRange = new Range(-MaxActionValue, MaxActionValue);

  public static final String VELOCITY = "velocity";
  public static final String POSITION = "position";
  public static final Legend legend = new Legend(POSITION, VELOCITY);

  @Monitor
  protected double position;
  @Monitor
  protected double velocity = 0.0;
  protected static final Range positionRange = new Range(-1.2, 0.6);
  protected static final Range velocityRange = new Range(-0.07, 0.07);

  private static final double target = positionRange.max();
  private double throttleFactor = 1.0;
  private final Random random;
  private TRStep step;
  private final int episodeLengthMax;

  public MountainCar(Random random) {
    this(random, -1);
  }

  public MountainCar(Random random, int episodeLengthMax) {
    this.random = random;
    this.episodeLengthMax = episodeLengthMax;
  }

  protected void update(ActionArray action) {
    double actionThrottle = ActionRange.bound(ActionArray.toDouble(action));
    double throttle = actionThrottle * throttleFactor;
    velocity = velocityRange.bound(velocity + 0.001 * throttle - 0.0025 * Math.cos(3 * position));
    position += velocity;
    if (position < positionRange.min())
      velocity = 0.0;
    position = positionRange.bound(position);
  }

  @Override
  public TRStep step(Action action) {
    update((ActionArray) action);
    step = new TRStep(step, action, new double[] { position, velocity }, -1.0);
    if (isGoalReached())
      forceEndEpisode();
    return step;
  }

  @Override
  public TRStep forceEndEpisode() {
    step = step.createEndingStep();
    return step;
  }

  private boolean isGoalReached() {
    return position >= target || (episodeLengthMax > 0 && step != null && step.time > episodeLengthMax);
  }

  @Override
  public TRStep initialize() {
    if (random == null) {
      position = -0.5;
      velocity = 0.0;
    } else {
      position = positionRange.choose(random);
      velocity = velocityRange.choose(random);
    }
    step = new TRStep(new double[] { position, velocity }, -1);
    return step;
  }

  @Override
  public Legend legend() {
    return legend;
  }

  @Override
  public Action[] actions() {
    return Actions;
  }

  public void setThrottleFactor(double factor) {
    throttleFactor = factor;
  }

  @Override
  public Range[] getObservationRanges() {
    return new Range[] { positionRange, velocityRange };
  }

  @Override
  public Range[] actionRanges() {
    return new Range[] { ActionRange };
  }

  @Override
  public TRStep lastStep() {
    return step;
  }

  static public double height(double position) {
    return Math.sin(3.0 * position);
  }
}
