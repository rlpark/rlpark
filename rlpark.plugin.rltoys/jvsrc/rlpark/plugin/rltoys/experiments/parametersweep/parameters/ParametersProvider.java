package rlpark.plugin.rltoys.experiments.parametersweep.parameters;

import java.util.List;

public interface ParametersProvider {
  List<Parameters> provideParameters(List<Parameters> parameters);
}
