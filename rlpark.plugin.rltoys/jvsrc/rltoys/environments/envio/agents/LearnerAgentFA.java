package rltoys.environments.envio.agents;

import rltoys.algorithms.representations.actions.Action;
import rltoys.environments.envio.RLAgent;
import rltoys.environments.envio.control.ControlLearner;
import rltoys.environments.envio.observations.TRStep;
import rltoys.environments.envio.states.Projector;
import rltoys.math.vector.RealVector;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

public class LearnerAgentFA implements RLAgent {
  private static final long serialVersionUID = -8694734303900854141L;
  @Monitor
  protected final ControlLearner control;
  @Monitor
  protected final Projector projector;
  protected RealVector x_t;

  public LearnerAgentFA(ControlLearner control, Projector projector) {
    this.control = control;
    this.projector = projector;
  }

  @Override
  public Action getAtp1(TRStep step) {
    if (step.isEpisodeStarting())
      x_t = null;
    RealVector x_tp1 = projector.project(step.o_tp1);
    Action a_tp1 = control.step(x_t, step.a_t, x_tp1, step.r_tp1);
    x_t = x_tp1;
    return a_tp1;
  }

  public ControlLearner control() {
    return control;
  }

  public Projector projector() {
    return projector;
  }
}
