package rlpark.plugin.rltoys.agents.rl;

import rlpark.plugin.rltoys.algorithms.control.ControlLearner;
import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.rl.RLAgent;
import rlpark.plugin.rltoys.envio.rl.TRStep;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.math.vector.implementations.PVector;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

public class LearnerAgent implements RLAgent {
  private static final long serialVersionUID = -8694734303900854141L;
  @Monitor
  protected final ControlLearner control;
  protected RealVector x_t;

  public LearnerAgent(ControlLearner control) {
    this.control = control;
  }

  @Override
  public Action getAtp1(TRStep step) {
    if (step.isEpisodeStarting())
      x_t = null;
    RealVector x_tp1 = step.o_tp1 != null ? new PVector(step.o_tp1) : null;
    Action a_tp1 = control.step(x_t, step.a_t, x_tp1, step.r_tp1);
    x_t = x_tp1;
    return a_tp1;
  }

  public ControlLearner control() {
    return control;
  }
}
