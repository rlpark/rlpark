package rlpark.plugin.rltoys.experiments.parametersweep.interfaces;

import java.io.Serializable;

import rlpark.plugin.rltoys.experiments.parametersweep.parameters.Parameters;

public interface JobWithParameters extends Serializable, Runnable {
  Parameters parameters();
}
