package rlpark.plugin.rltoys.agents.rl;

import rlpark.plugin.rltoys.algorithms.control.ControlLearner;
import rlpark.plugin.rltoys.algorithms.functions.states.Projector;
import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.rl.RLAgent;
import rlpark.plugin.rltoys.envio.rl.TRStep;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.math.vector.implementations.BVector;
import rlpark.plugin.rltoys.math.vector.implementations.Vectors;
import zephyr.plugin.core.api.monitoring.annotations.IgnoreMonitor;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

@Monitor
public class LearnerAgentFA implements RLAgent {
  private static final long serialVersionUID = -8694734303900854141L;
  protected final ControlLearner control;
  protected final Projector projector;
  protected double reward;
  @IgnoreMonitor
  protected RealVector x_t;
  private final BVector absorbingState;

  public LearnerAgentFA(ControlLearner control, Projector projector) {
    this.control = control;
    this.projector = projector;
    absorbingState = new BVector(projector.vectorSize());
  }

  @Override
  public Action getAtp1(TRStep step) {
    RealVector x_tp1 = !step.isEpisodeEnding() ? projector.project(step.o_tp1) : absorbingState;
    reward = step.r_tp1;
    Action a_tp1 = control.step(step.o_t != null ? x_t : null, step.a_t, x_tp1, reward);
    x_t = Vectors.bufferedCopy(x_tp1, x_t);
    return a_tp1;
  }

  public ControlLearner control() {
    return control;
  }

  public Projector projector() {
    return projector;
  }

  public RealVector lastState() {
    return x_t;
  }
}
