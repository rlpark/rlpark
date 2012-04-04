package rlpark.plugin.rltoys.experiments.parametersweep.parameters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class FrozenParameters extends AbstractParameters {
  private static final long serialVersionUID = -1853925775244660996L;
  protected final int hashcode;

  public FrozenParameters(RunInfo infos, Map<String, Double> parameters, Map<String, Double> results) {
    super(infos);
    putAllSorted(parameters, this.parameters);
    putAllSorted(results, this.results);
    hashcode = computeHashcode(parameters);
  }

  private void putAllSorted(Map<String, Double> source, Map<String, Double> target) {
    List<String> sortedKeys = new ArrayList<String>(source.keySet());
    Collections.sort(sortedKeys);
    for (String key : sortedKeys)
      target.put(key, source.get(key));
  }

  static protected int computeHashcode(Map<String, Double> parameters) {
    int hashcode = 0;
    for (Map.Entry<String, Double> entry : parameters.entrySet())
      hashcode += entry.hashCode();
    return hashcode;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null)
      return false;
    if (super.equals(obj))
      return true;
    AbstractParameters other = (AbstractParameters) obj;
    return parameters.equals(other.parameters) && infos().equals(other.infos());
  }

  @Override
  public int hashCode() {
    return hashcode;
  }
}
