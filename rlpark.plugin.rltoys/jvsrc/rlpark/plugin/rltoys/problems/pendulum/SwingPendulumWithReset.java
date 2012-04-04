package rlpark.plugin.rltoys.problems.pendulum;

import java.util.Random;

import rlpark.plugin.rltoys.envio.actions.ActionArray;

public class SwingPendulumWithReset extends SwingPendulum {
  private final int resetPeriod;

  public SwingPendulumWithReset(Random random, int resetPeriod) {
    super(random, false);
    this.resetPeriod = resetPeriod;
  }

  @Override
  protected void update(ActionArray action) {
    if (step != null && step.time % resetPeriod == 0)
      initializeProblemData();
    else
      super.update(action);
  }
}
