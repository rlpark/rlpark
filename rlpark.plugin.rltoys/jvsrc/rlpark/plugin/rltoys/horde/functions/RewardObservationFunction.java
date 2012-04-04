package rlpark.plugin.rltoys.horde.functions;

import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.observations.Legend;
import rlpark.plugin.rltoys.envio.observations.ObsAsDoubles;
import rlpark.plugin.rltoys.envio.observations.Observation;
import rlpark.plugin.rltoys.math.vector.RealVector;
import zephyr.plugin.core.api.labels.Labeled;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

public class RewardObservationFunction implements RewardFunction, Labeled, HordeUpdatable {
  private static final long serialVersionUID = -5930168576876015871L;
  @Monitor
  protected double reward;
  private final int observationIndex;
  private final String label;

  public RewardObservationFunction(Legend legend, String label) {
    this.label = label;
    observationIndex = legend.indexOf(label);
    if (observationIndex < 0)
      throw new RuntimeException(label + " not found in the legend");
  }

  public void update(double[] o) {
    reward = o != null ? o[observationIndex] : 0.0;
  }

  @Override
  public double reward() {
    return reward;
  }

  @Override
  public String label() {
    return label;
  }

  @Override
  public void update(Observation o_tp1, RealVector x_t, Action a_t, RealVector x_tp1) {
    update(((ObsAsDoubles) o_tp1).doubleValues());
  }
}
