package rltoys.experiments.parametersweep.parameters;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Parameters extends AbstractParameters {
  private static final long serialVersionUID = -3022547944186532000L;

  public Parameters(RunInfo infos) {
    super(infos);
  }

  public Parameters(AbstractParameters parameters) {
    super(parameters.infos(), parameters.parameters, parameters.results);
  }

  public void putSweepParam(String label, double value) {
    parameters.put(label, value);
  }

  public static List<Parameters> combine(List<Parameters> existing, String label, double[] values) {
    assert existing.size() > 0;
    List<Parameters> combination = new ArrayList<Parameters>();
    for (Parameters parameters : existing) {
      for (double value : values) {
        Parameters combinedParameters = new Parameters(parameters);
        combinedParameters.putSweepParam(label, value);
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
}