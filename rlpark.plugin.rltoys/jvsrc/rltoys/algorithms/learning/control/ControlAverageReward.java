package rltoys.algorithms.learning.control;

import rltoys.algorithms.representations.actions.Action;
import rltoys.environments.envio.control.ControlLearner;
import rltoys.math.vector.RealVector;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

@Monitor
public class ControlAverageReward implements ControlLearner {
  private static final long serialVersionUID = -8378404594670911282L;
  private final AverageReward averageReward;
  private final ControlLearner control;

  public ControlAverageReward(AverageReward averageReward, ControlLearner control) {
    this.averageReward = averageReward;
    this.control = control;
  }

  @Override
  public Action step(RealVector x_t, Action a_t, RealVector x_tp1, double r_tp1) {
    return control.step(x_t, a_t, x_tp1, averageReward.average(r_tp1));
  }

  @Override
  public Action proposeAction(RealVector x) {
    return control.proposeAction(x);
  }
}