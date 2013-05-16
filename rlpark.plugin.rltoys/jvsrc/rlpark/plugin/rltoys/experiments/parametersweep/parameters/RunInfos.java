package rlpark.plugin.rltoys.experiments.parametersweep.parameters;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import rlpark.plugin.rltoys.utils.Utils;

public class RunInfos {
  public static List<Parameters> set(Parameters parameters, String label, double value) {
    return set(Utils.asList(parameters), label, value);
  }

  public static List<Parameters> set(List<Parameters> parameters, String label, double value) {
    Set<RunInfo> infoSet = new LinkedHashSet<RunInfo>();
    for (Parameters parameter : parameters) {
      final RunInfo infos = parameter.infos();
      if (infoSet.contains(infos))
        continue;
      infos.put(label, value);
      infoSet.add(infos);
    }
    return parameters;
  }
}
