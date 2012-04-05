package rlpark.plugin.rltoysview.internal.policystructure;

import org.eclipse.swt.graphics.GC;

import rlpark.plugin.rltoys.algorithms.functions.policydistributions.structures.AbstractNormalDistribution;
import rlpark.plugin.rltoys.envio.policy.BoundedPolicy;
import rlpark.plugin.rltoys.math.normalization.MinMaxNormalizer;
import rlpark.plugin.rltoys.math.ranges.Range;
import rlpark.plugin.rltoys.utils.Utils;
import zephyr.plugin.plotting.internal.data.Data2D;
import zephyr.plugin.plotting.internal.plot2d.Plot2D;

@SuppressWarnings("restriction")
public class NormalDistributionDrawer {
  static private final int NbDrawnPoints = 100;
  static private final double Width = 10;

  private final Data2D datas = new Data2D("", NbDrawnPoints);
  private final AbstractNormalDistribution policy;
  private final Plot2D plot;
  private double stddev;
  private double mean;
  private final MinMaxNormalizer normalizer;

  public NormalDistributionDrawer(Plot2D plot, AbstractNormalDistribution policy) {
    this(plot, policy, null);
  }

  public NormalDistributionDrawer(Plot2D plot, AbstractNormalDistribution policy, MinMaxNormalizer normalizer) {
    this.policy = policy;
    this.plot = plot;
    this.normalizer = normalizer;
  }

  public void draw(GC gc) {
    if (!Utils.checkValue((float) (stddev * stddev)) || !Utils.checkValue(mean))
      return;
    plot.draw(gc, datas);
  }

  public void synchronize() {
    stddev = policy.stddev();
    mean = policy.mean();
    if (!Utils.checkValue((float) (stddev * stddev)) || !Utils.checkValue(mean))
      return;
    Range range = findRange();
    double step = range.length() / datas.nbPoints;
    for (int i = 0; i < datas.nbPoints; i++) {
      double a = range.min() + step * i;
      datas.xdata[i] = (float) a;
      datas.ydata[i] = (float) policy.pi_s(a);
      if (normalizer != null)
        normalizer.update(datas.ydata[i]);
    }
    if (normalizer != null)
      for (int i = 0; i < datas.nbPoints; i++)
        datas.ydata[i] = normalizer.normalize(datas.ydata[i]);
  }

  private Range findRange() {
    double min = mean - (stddev * Width / 2);
    double max = mean + (stddev * Width / 2);
    if (policy instanceof BoundedPolicy) {
      Range policyRange = ((BoundedPolicy) policy).range();
      min = policyRange.min() - (.1 * policyRange.length());
      max = policyRange.max() + (.1 * policyRange.length());
    }
    return new Range(min, max);
  }
}
