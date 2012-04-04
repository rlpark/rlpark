package rlpark.plugin.rltoys.envio.observations;

import java.util.List;

import rlpark.plugin.rltoys.envio.actions.ActionArray;
import rlpark.plugin.rltoys.utils.Utils;

public class ObsActionFilter extends ObsFilter {
  public static final String Action = "Action";
  private static final long serialVersionUID = 6117458144833700858L;
  private final int nbActions;

  public ObsActionFilter(Legend legend, int nbActions, String... labelPrefixes) {
    this(legend, nbActions, false, labelPrefixes);
  }

  public ObsActionFilter(Legend legend, int nbActions, List<String> labelPrefixes) {
    this(legend, nbActions, false, labelPrefixes);
  }

  public ObsActionFilter(Legend legend, int nbActions, boolean prefixesExcluded, String... labelPrefixes) {
    this(legend, nbActions, prefixesExcluded, Utils.asList(labelPrefixes));
  }

  public ObsActionFilter(Legend legend, int nbActions, boolean prefixesExcluded, List<String> labelPrefixes) {
    super(legend, nbActions, prefixesExcluded, labelPrefixes);
    this.nbActions = nbActions;
    for (int i = 0; i < nbActions; i++)
      this.legend.legend().put(Action + i, this.legend.nbLabels());
  }

  public double[] update(double[] o, ActionArray a) {
    if (o == null)
      return null;
    super.update(o);
    if (a == null)
      return filteredObs;
    assert a.actions.length == nbActions;
    for (int i = 0; i < nbActions; i++)
      filteredObs[i + selectedIndexes.length] = a.actions[i];
    return filteredObs;
  }

  @Override
  public int size() {
    return selectedIndexes.length + 1;
  }

}
