package rlpark.plugin.rltoys.algorithms.functions.states;

import java.io.Serializable;

import rlpark.plugin.rltoys.math.vector.RealVector;

public interface Projector extends Serializable {
  /**
   * Project an observation. If the observation is null, it should return a
   * non-null vector representing an absorbing state.
   * 
   * @param obs
   *          observation to project
   * @return a non-null vector
   */
  RealVector project(double[] obs);

  /**
   * @return size of the vector after projection
   */
  int vectorSize();

  /**
   * @return the expected norm of the vector after projection
   */
  double vectorNorm();
}
