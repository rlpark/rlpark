package rlpark.plugin.rltoys.experiments.parametersweep.parameters;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class AbstractParameters implements Comparable<AbstractParameters>, Serializable {
  private static final long serialVersionUID = 8135997315567194984L;
  protected final Map<String, Double> parameters = new LinkedHashMap<String, Double>();
  protected final Map<String, Double> results = new LinkedHashMap<String, Double>();
  private final RunInfo infos;

  public AbstractParameters(RunInfo infos) {
    this(infos, new HashMap<String, Double>(), new HashMap<String, Double>());
    assert infos != null;
  }

  public AbstractParameters(RunInfo infos, Map<String, Double> parameters, Map<String, Double> results) {
    this.infos = infos;
    this.parameters.putAll(parameters);
    this.results.putAll(results);
  }

  public void putResult(String parameterName, double parameterValue) {
    results.put(parameterName, parameterValue);
  }

  public boolean hasKey(String key) {
    return parameters.containsKey(key) || infos.hasKey(key);
  }

  public double get(String name, double defaultValue) {
    Double value = getDouble(name);
    return value != null ? value : defaultValue;
  }

  public double get(String name) {
    return getDouble(name);
  }

  public Double getDouble(String name) {
    Double parameterValue = parameters.get(name);
    if (parameterValue != null)
      return parameterValue;
    parameterValue = infos().get(name);
    if (parameterValue != null)
      return parameterValue;
    return results.get(name);
  }

  @Override
  public String toString() {
    StringBuilder result = new StringBuilder(parameters.toString());
    if (!results.isEmpty())
      result.append("=" + results.toString());
    return result.toString();
  }

  private Map<String, Double> getAllSweepValues() {
    Map<String, Double> all = new LinkedHashMap<String, Double>(parameters);
    all.putAll(results);
    return all;
  }

  public String[] labels() {
    Map<String, Double> all = getAllSweepValues();
    String[] result = new String[all.size()];
    all.keySet().toArray(result);
    return result;
  }

  public double[] values() {
    Map<String, Double> all = getAllSweepValues();
    double[] result = new double[all.size()];
    int index = 0;
    for (Double value : all.values()) {
      result[index] = value;
      index++;
    }
    return result;
  }

  @Override
  public int compareTo(AbstractParameters other) {
    for (Map.Entry<String, Double> entry : other.parameters.entrySet()) {
      Double d1 = parameters.get(entry.getKey());
      if (d1 == null)
        return -1;
      Double d2 = entry.getValue();
      if (d2 == null)
        return 1;
      int compared = Double.compare(d1, d2);
      if (compared != 0)
        return compared;
    }
    return 0;
  }

  public RunInfo infos() {
    return infos;
  }

  public boolean hasFlag(String flag) {
    return infos().hasFlag(flag);
  }
}