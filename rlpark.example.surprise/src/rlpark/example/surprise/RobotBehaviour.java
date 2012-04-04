package rlpark.example.surprise;

import java.util.Random;

import rlpark.plugin.rltoys.algorithms.representations.acting.Policy;
import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.utils.Utils;
import zephyr.plugin.core.api.synchronization.Chrono;

public class RobotBehaviour implements Policy {
  private static final long serialVersionUID = -6590692682863168325L;
  private final Chrono chrono = new Chrono();
  private final Action[] actions;
  private final double actionDuration;
  private Action currentAction = null;
  private final Random random;

  public RobotBehaviour(Random random, double time, Action[] actions) {
    this.random = random;
    this.actionDuration = time;
    this.actions = actions;
  }

  @Override
  public double pi(RealVector s, Action a) {
    return 1.0;
  }

  @Override
  public Action decide(RealVector s) {
    if (currentAction != null && chrono.getCurrentChrono() < actionDuration)
      return currentAction;
    currentAction = Utils.choose(random, actions);
    chrono.start();
    return currentAction;
  }
}
