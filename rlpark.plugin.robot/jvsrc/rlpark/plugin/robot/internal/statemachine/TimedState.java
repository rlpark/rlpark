package rlpark.plugin.robot.internal.statemachine;

import rlpark.plugin.rltoys.envio.rl.TRStep;

public abstract class TimedState implements StateNode<TRStep> {
  final private int nbTimeSteps;
  private int currentTimeSteps;

  public TimedState(int nbTimeSteps) {
    this.nbTimeSteps = nbTimeSteps;
  }

  @Override
  public boolean isDone() {
    return currentTimeSteps >= nbTimeSteps;
  }

  @Override
  public void start() {
    currentTimeSteps = 0;
  }

  @Override
  public void step(TRStep step) {
    currentTimeSteps++;
  }

  protected int currentTimeSteps() {
    return currentTimeSteps;
  }
}
