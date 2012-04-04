package rlpark.plugin.rltoys.experiments.parametersweep.interfaces;

import java.io.Serializable;

import rlpark.plugin.rltoys.experiments.ExperimentCounter;
import rlpark.plugin.rltoys.experiments.parametersweep.parameters.Parameters;

public interface Context extends Serializable {
  String folderPath();

  String fileName();

  Runnable createJob(Parameters parameters, ExperimentCounter counter);
}
