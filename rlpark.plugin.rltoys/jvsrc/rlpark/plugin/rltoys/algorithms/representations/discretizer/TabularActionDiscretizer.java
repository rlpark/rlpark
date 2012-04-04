package rlpark.plugin.rltoys.algorithms.representations.discretizer;

import java.util.LinkedHashMap;
import java.util.Map;

import rlpark.plugin.rltoys.envio.actions.Action;

public class TabularActionDiscretizer implements ActionDiscretizer {
  private static final long serialVersionUID = 9160060017385719505L;
  protected final Map<Action, Double> actionToDouble = new LinkedHashMap<Action, Double>();

  public TabularActionDiscretizer(Action[] actions) {
    for (int i = 0; i < actions.length; i++)
      actionToDouble.put(actions[i], (double) i);
  }

  @Override
  public double[] discretize(Action action) {
    return new double[] { actionToDouble.get(action) };
  }

  @Override
  public int nbOutput() {
    return 1;
  }

  @Override
  public Discretizer[] actionDiscretizers() {
    return new Discretizer[] { new Discretizer() {
      private static final long serialVersionUID = -1484841782654103099L;

      @Override
      public int resolution() {
        return actionToDouble.size();
      }

      @Override
      public int discretize(double input) {
        return (int) input;
      }
    } };
  }
}
