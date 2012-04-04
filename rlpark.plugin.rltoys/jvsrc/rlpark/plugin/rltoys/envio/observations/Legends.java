package rlpark.plugin.rltoys.envio.observations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rlpark.plugin.rltoys.utils.Utils;

public class Legends {
  static private boolean isSelected(List<String> prefixes, String label, boolean excluded) {
    for (String currentLabel : prefixes)
      if (label.startsWith(currentLabel))
        return !excluded;
    return excluded;
  }

  public static int[] getSelectedIndexes(Legend legend, boolean excluded, List<String> prefixes) {
    List<Integer> indexes = new ArrayList<Integer>();
    for (int i = 0; i < legend.nbLabels(); i++)
      if (isSelected(prefixes, legend.label(i), excluded))
        indexes.add(i);
    Collections.sort(indexes);
    int[] result = new int[indexes.size()];
    for (int i = 0; i < result.length; i++)
      result[i] = indexes.get(i);
    return result;
  }

  public static int[] getSelectedIndexes(Legend legend, boolean excluded, String... prefixes) {
    return getSelectedIndexes(legend, excluded, Utils.asList(prefixes));
  }

  static private List<String> getLabelList(Legend legend, int[] selectedIndexes) {
    List<String> labelList = new ArrayList<String>();
    for (Integer labelIndex : selectedIndexes)
      labelList.add(legend.label(labelIndex));
    return labelList;
  }

  static public List<String> getSelectedLabels(Legend legend, boolean excluded, String... prefixes) {
    int[] selectedIndexes = getSelectedIndexes(legend, excluded, prefixes);
    return getLabelList(legend, selectedIndexes);
  }

  public static Legend createLegend(Legend legend, int[] selectedIndexes) {
    return new Legend(getLabelList(legend, selectedIndexes));
  }
}
