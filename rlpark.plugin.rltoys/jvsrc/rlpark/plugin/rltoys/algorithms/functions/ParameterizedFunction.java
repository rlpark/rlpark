package rlpark.plugin.rltoys.algorithms.functions;

import java.io.Serializable;

import rlpark.plugin.rltoys.math.vector.implementations.PVector;

public interface ParameterizedFunction extends Serializable {
  PVector weights();
}
