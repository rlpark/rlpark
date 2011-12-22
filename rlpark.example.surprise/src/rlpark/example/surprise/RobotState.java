package rlpark.example.surprise;

import rltoys.algorithms.representations.actions.Action;
import rltoys.environments.envio.observations.Observation;
import rltoys.environments.envio.states.AgentState;
import rltoys.math.vector.RealVector;
import rltoys.math.vector.implementations.PVector;

public class RobotState implements AgentState {
  private static final long serialVersionUID = 6644415896368916415L;

  @Override
  public RealVector update(Action a_t, Observation o_tp1) {
    return new PVector(new double[] { 1.0 });
  }

  @Override
  public double stateNorm() {
    return 1;
  }

  @Override
  public int stateSize() {
    return 1;
  }
}
