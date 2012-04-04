package rlpark.plugin.rltoys.envio.observations;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import rlpark.plugin.rltoys.utils.Utils;
import zephyr.plugin.core.api.monitoring.annotations.LabelProvider;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

public class ObsFilter implements Serializable {
  private static final long serialVersionUID = -3006545157709546374L;
  protected final int[] selectedIndexes;
  protected final Legend legend;
  @Monitor(level = 1)
  protected final double[] filteredObs;

  public ObsFilter(Legend legend, String... labelPrefixes) {
    this(legend, false, labelPrefixes);
  }

  public ObsFilter(Legend legend, List<String> labelPrefixes) {
    this(legend, false, labelPrefixes);
  }

  public ObsFilter(Legend legend, boolean prefixesExcluded, String... labelPrefixes) {
    this(legend, prefixesExcluded, Utils.asList(labelPrefixes));
  }

  public ObsFilter(Legend legend, boolean prefixesExcluded, List<String> labelPrefixes) {
    this(legend, 0, prefixesExcluded, labelPrefixes);
  }

  protected ObsFilter(Legend legend, int additionalObs, boolean prefixesExcluded, List<String> labelPrefixes) {
    selectedIndexes = Legends.getSelectedIndexes(legend, prefixesExcluded, labelPrefixes);
    this.legend = Legends.createLegend(legend, selectedIndexes);
    filteredObs = new double[this.legend.nbLabels() + additionalObs];
  }

  @LabelProvider(ids = { "filteredObs" })
  protected String labelOf(int index) {
    return legend.label(index);
  }

  public double[] update(double[] o) {
    if (o == null)
      return null;
    for (int i = 0; i < selectedIndexes.length; i++)
      filteredObs[i] = o[selectedIndexes[i]];
    return filteredObs;
  }

  public int size() {
    return selectedIndexes.length;
  }

  public Legend legend() {
    return legend;
  }

  public void updateIndexes(Legend fromLegend) {
    for (Map.Entry<String, Integer> entry : legend.legend().entrySet()) {
      int inThisIndex = entry.getValue();
      String label = entry.getKey();
      int newIndex = fromLegend.indexOf(label);
      selectedIndexes[inThisIndex] = newIndex;
    }
  }
}