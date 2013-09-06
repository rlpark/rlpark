package rlpark.plugin.rltoys.algorithms.representations.observations;

import java.io.Serializable;

import rlpark.plugin.rltoys.envio.observations.Legend;
import rlpark.plugin.rltoys.math.normalization.MinMaxNormalizer;
import rlpark.plugin.rltoys.math.ranges.Range;
import zephyr.plugin.core.api.monitoring.annotations.LabelProvider;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

public class ObsNormalizers implements Serializable {
  private static final long serialVersionUID = 5380060422826193962L;
  private final MinMaxNormalizer[] normalizers;
  private final Legend legend;
  @Monitor(level = 1)
  private final double[] normalized;

  public ObsNormalizers(Legend legend) {
    normalizers = new MinMaxNormalizer[legend.nbLabels()];
    for (int i = 0; i < normalizers.length; i++)
      normalizers[i] = new MinMaxNormalizer();
    normalized = new double[normalizers.length];
    this.legend = legend;
  }

  public Legend legend() {
    return legend;
  }

  @LabelProvider(ids = { "normalized" })
  protected String label(int index) {
    return legend.label(index);
  }

  public Range[] getRanges() {
    Range[] ranges = new Range[normalizers.length];
    for (int i = 0; i < ranges.length; i++)
      ranges[i] = normalizers[i].targetRange();
    return ranges;
  }

  public double[] update(double[] o) {
    if (o == null)
      return null;
    for (int i = 0; i < o.length; i++) {
      normalizers[i].update(o[i]);
      normalized[i] = normalizers[i].normalize(o[i]);
    }
    return normalized;
  }
}
