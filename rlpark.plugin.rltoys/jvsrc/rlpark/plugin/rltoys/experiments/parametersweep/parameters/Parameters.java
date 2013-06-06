package rlpark.plugin.rltoys.experiments.parametersweep.parameters;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Parameters extends AbstractParameters {
  private static final long serialVersionUID = -3022547944186532000L;
  public static final String ComputationTime = "ComputationTime";
  public static final String PerformanceCumulatedMeasured = "CumulatedMeasured";
  public static final String PerformanceSliceMeasured = "SliceMeasured";
  public static final String PerformanceStart = "Start";
  public static final String PerformanceNbCheckPoint = "NbCheckPoint";
  public static final int DefaultNbPerformanceCheckpoints = 20;

  public Parameters(RunInfo infos) {
    super(infos);
  }

  public Parameters(AbstractParameters parameters) {
    super(parameters.infos(), parameters.parameters, parameters.results);
  }

  public boolean putSweepParam(String label, double value) {
    return parameters.put(label, value) != null;
  }

  public static List<Parameters> combine(List<Parameters> existing, String label, double[] values) {
    assert existing.size() > 0;
    List<Parameters> combination = new ArrayList<Parameters>();
    for (Parameters parameters : existing) {
      for (double value : values) {
        Parameters combinedParameters = new Parameters(parameters);
        if (combinedParameters.putSweepParam(label, value))
          throw new RuntimeException(label + " already set");
        combination.add(combinedParameters);
      }
    }
    return combination;
  }

  public FrozenParameters froze() {
    return new FrozenParameters(infos(), parameters, results);
  }

  public static List<Parameters> filter(List<Parameters> parameters, String... filters) {
    Map<String, Double> filterMap = new LinkedHashMap<String, Double>();
    for (String filterString : filters) {
      int equalIndex = filterString.indexOf('=');
      filterMap.put(filterString.substring(0, equalIndex), Double.parseDouble(filterString.substring(equalIndex + 1)));
    }
    List<Parameters> result = new ArrayList<Parameters>();
    for (Parameters parameter : parameters) {
      boolean satisfy = true;
      for (Map.Entry<String, Double> entry : filterMap.entrySet()) {
        if (!parameter.hasKey(entry.getKey()))
          continue;
        double parameterValue = parameter.get(entry.getKey());
        if (parameterValue != entry.getValue()) {
          satisfy = false;
          break;
        }
      }
      if (satisfy)
        result.add(parameter);
    }
    return result;
  }

  public void setComputationTimeMillis(long computationTime) {
    putResult(ComputationTime, computationTime);
  }

  public long getComputationTimeMillis() {
    return (long) get(ComputationTime);
  }

  public static void set(List<Parameters> parameters, String label, double value) {
    for (Parameters p : parameters)
      p.putSweepParam(label, value);
  }
}