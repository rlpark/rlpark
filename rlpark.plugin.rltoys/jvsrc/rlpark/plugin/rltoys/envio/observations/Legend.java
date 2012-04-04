package rlpark.plugin.rltoys.envio.observations;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Legend implements Serializable {
  private static final long serialVersionUID = -3941386571561190239L;
  final private Map<String, Integer> legend = new LinkedHashMap<String, Integer>();

  public Legend(String... labels) {
    this(Arrays.asList(labels));
  }

  public Legend(List<String> labels) {
    for (int i = 0; i < labels.size(); i++) {
      String label = labels.get(i);
      legend.put(label, i);
    }
  }

  final public int indexOf(String label) {
    Integer index = legend.get(label);
    return index != null ? index : -1;
  }

  public boolean hasLabel(String label) {
    return legend.containsKey(label);
  }

  public int nbLabels() {
    return legend.size();
  }

  public Map<String, Integer> legend() {
    return legend;
  }

  public String label(int i) {
    for (Map.Entry<String, Integer> entry : legend.entrySet())
      if (entry.getValue() == i)
        return entry.getKey();
    return "unknown";
  }

  public List<String> getLabels() {
    return new ArrayList<String>(legend.keySet());
  }

  @Override
  public String toString() {
    return String.valueOf(legend);
  }

  public void replace(String orig, String dest) {
    int index = indexOf(orig);
    legend.remove(orig);
    legend.put(dest, index);
  }
}
