package rlpark.plugin.rltoys.experiments.parametersweep.interfaces;

import java.util.List;

import rlpark.plugin.rltoys.experiments.parametersweep.parameters.Parameters;

public interface SweepDescriptor {
  public List<? extends Context> provideContexts();

  List<Parameters> provideParameters(Context context);
}
