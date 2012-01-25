package rltoys.experiments.parametersweep.parameters;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class RunInfo implements Serializable {
  private static final long serialVersionUID = 4114752829910485352L;
  private final Map<String, Double> infos = new LinkedHashMap<String, Double>();

  public void enableFlag(String flag) {
    infos.put(flag, 1.0);
  }

  public boolean hasFlag(String flag) {
    return infos.containsKey(flag);
  }

  public void put(String label, double value) {
    infos.put(label, value);
  }

  @Override
  public int hashCode() {
    return FrozenParameters.computeHashcode(infos);
  }

  @Override
  public boolean equals(Object other) {
    if (super.equals(other))
      return true;
    if (other == null)
      return false;
    RunInfo o = (RunInfo) other;
    Set<String> keysToCheck = new HashSet<String>();
    keysToCheck.addAll(o.infos.keySet());
    keysToCheck.retainAll(infos.keySet());
    for (String key : keysToCheck) {
      Double thisValue = infos.get(key);
      Double otherValue = o.infos.get(key);
      if (thisValue == otherValue)
        continue;
      if (thisValue == null && otherValue != null)
        return false;
      if (otherValue == null && thisValue != null)
        return false;
      if (!thisValue.equals(otherValue))
        return false;
    }
    return true;
  }

  public String[] infoLabels() {
    String[] result = new String[infos.size()];
    infos.keySet().toArray(result);
    return result;
  }

  public double[] infoValues() {
    double[] result = new double[infos.size()];
    int index = 0;
    for (Double value : infos.values()) {
      result[index] = value;
      index++;
    }
    return result;
  }

  public Double get(String name) {
    return infos.get(name);
  }

  public boolean hasKey(String key) {
    return infos.containsKey(key);
  }

  @Override
  public String toString() {
    return infos.toString();
  }
}
