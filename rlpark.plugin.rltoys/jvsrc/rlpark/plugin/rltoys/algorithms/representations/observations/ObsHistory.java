package rlpark.plugin.rltoys.algorithms.representations.observations;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import rlpark.plugin.rltoys.envio.observations.Legend;
import rlpark.plugin.rltoys.math.ranges.Range;
import zephyr.plugin.core.api.monitoring.annotations.LabelProvider;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

public class ObsHistory implements Serializable {
  private static final long serialVersionUID = 7843542344680953444L;
  public int nbTimeSteps;
  private final int obsVectorSize;
  private final double[] oh_t;
  @Monitor(level = 2)
  private final double[] oh_tp1;
  private final Legend legend;
  private final Range[] ranges;

  public ObsHistory(int nbStepHistory, Legend legend) {
    this(nbStepHistory, legend, null);
  }

  public ObsHistory(int nbStepHistory, Legend legend, Range[] ranges) {
    assert nbStepHistory >= 0;
    assert ranges == null || legend.nbLabels() == ranges.length;
    nbTimeSteps = nbStepHistory + 1;
    obsVectorSize = legend.nbLabels();
    oh_t = new double[nbTimeSteps * obsVectorSize];
    oh_tp1 = new double[nbTimeSteps * obsVectorSize];
    this.legend = buildLegend(legend);
    this.ranges = ranges;
  }

  @LabelProvider(ids = { "oh_tp1" })
  protected String labelOf(int index) {
    return legend.label(index);
  }

  private Legend buildLegend(Legend legend) {
    List<String> obsLabel = legend.getLabels();
    String[] labels = new String[nbTimeSteps * obsLabel.size()];
    for (int i = 0; i < nbTimeSteps; i++) {
      int timeOffset = nbTimeSteps - i - 1;
      for (int j = 0; j < obsLabel.size(); j++) {
        String label = obsLabel.get(j);
        label += toTimeLabel(timeOffset);
        labels[i * obsLabel.size() + j] = label;
      }
    }
    return new Legend(labels);
  }

  static protected String toTimeLabel(int timeOffset) {
    return "[t-" + timeOffset + "]";
  }

  public Legend legend() {
    return legend;
  }

  public int historyVectorSize() {
    return nbTimeSteps * obsVectorSize;
  }

  public double[] update(double[] o_tp1) {
    if (o_tp1 == null)
      return null;
    System.arraycopy(oh_tp1, 0, oh_t, 0, oh_t.length);
    int historyObsLength = (nbTimeSteps - 1) * obsVectorSize;
    System.arraycopy(oh_t, obsVectorSize, oh_tp1, 0, historyObsLength);
    System.arraycopy(o_tp1, 0, oh_tp1, historyObsLength, obsVectorSize);
    return oh_tp1;
  }

  public int[] selectIndexes(int timeOffset, String... prefixes) {
    List<String> selectedLabels = selectLabels(timeOffset, prefixes);
    int[] indexes = new int[selectedLabels.size()];
    for (int i = 0; i < indexes.length; i++)
      indexes[i] = legend.indexOf(selectedLabels.get(i));
    return indexes;
  }

  public List<String> selectLabels(int timeOffset, String... prefixes) {
    List<String> result = new ArrayList<String>();
    String timeStringLabel = toTimeLabel(timeOffset);
    for (String label : legend.getLabels()) {
      if (!label.endsWith(timeStringLabel))
        continue;
      for (String prefix : prefixes)
        if (label.startsWith(prefix)) {
          result.add(label);
          break;
        }
    }
    return result;
  }

  public Range[] getRanges() {
    if (ranges == null || legend.nbLabels() / nbTimeSteps != ranges.length)
      return null;
    Range[] result = new Range[historyVectorSize()];
    for (int i = 0; i < result.length; i++)
      result[i] = ranges[i % ranges.length];
    return result;
  }
}
