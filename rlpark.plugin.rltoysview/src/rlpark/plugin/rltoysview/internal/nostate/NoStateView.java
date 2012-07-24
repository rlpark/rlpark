package rlpark.plugin.rltoysview.internal.nostate;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;

import rlpark.plugin.rltoys.envio.actions.ActionArray;
import rlpark.plugin.rltoys.envio.rl.TRStep;
import rlpark.plugin.rltoys.math.History;
import rlpark.plugin.rltoys.math.normalization.MinMaxNormalizer;
import rlpark.plugin.rltoys.math.ranges.Range;
import rlpark.plugin.rltoys.problems.nostate.NoStateProblem;
import zephyr.ZephyrPlotting;
import zephyr.plugin.core.api.signals.Listener;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.internal.helpers.ClassViewProvider;
import zephyr.plugin.core.internal.utils.Colors;
import zephyr.plugin.core.internal.views.helpers.ForegroundCanvasView;
import zephyr.plugin.plotting.internal.data.Data2D;
import zephyr.plugin.plotting.internal.plot2d.Plot2D;
import zephyr.plugin.plotting.internal.plot2d.drawer2d.Drawer2D;

@SuppressWarnings("restriction")
public class NoStateView extends ForegroundCanvasView<NoStateProblem> {
  public static class Provider extends ClassViewProvider {
    public Provider() {
      super(NoStateProblem.class);
    }
  }

  protected class ExperimentData implements Listener<Clock> {
    static final public int HistoryLength = 1000;
    public final History actionHistory = new History(HistoryLength);
    public final History rewardHistory = new History(HistoryLength);
    private final NoStateProblem problem;

    public ExperimentData(NoStateProblem problem) {
      this.problem = problem;
    }

    @Override
    public void listen(Clock eventInfo) {
      TRStep step = problem.lastStep();
      final double action = ActionArray.toDouble(step.a_t);
      actionHistory.append(action);
      rewardHistory.append(step.r_tp1);
    }

    public void reset() {
      actionHistory.reset();
      rewardHistory.reset();
    }

    public int nbAdded() {
      return actionHistory.nbAdded();
    }
  }

  ExperimentData experimentData;
  private final Plot2D plot = new Plot2D();
  private final Drawer2D rewardDrawer = new Drawer2D() {
    @Override
    public void draw(GC gc, float[] xdata, float[] ydata, int[] gx, int[] gy) {
      for (int i = 1; i <= Math.min(gy.length, experimentData.nbAdded()); i++)
        gc.drawOval(gx[gx.length - i] - radius, gy[gy.length - i] - radius, radius, radius);
    }
  };
  private Data2D data;
  private final Colors colors = new Colors();
  private final MinMaxNormalizer rewardNormalizer = new MinMaxNormalizer(new Range(0, 1));
  int radius = 1;

  @Override
  public boolean synchronize(NoStateProblem current) {
    experimentData.actionHistory.toArray(data.xdata);
    experimentData.rewardHistory.toArray(data.ydata);
    for (float reward : data.ydata)
      rewardNormalizer.update(reward);
    for (int i = 0; i < data.ydata.length; i++)
      data.ydata[i] = rewardNormalizer.normalize(data.ydata[i]);
    return true;
  }

  @Override
  protected void paint(GC gc) {
    plot.clear(gc);
    gc.setAntialias(ZephyrPlotting.preferredAntiAliasing() ? SWT.ON : SWT.OFF);
    gc.setForeground(colors.color(gc, Colors.COLOR_DARK_RED));
    radius = ZephyrPlotting.preferredLineWidth();
    gc.setLineWidth(ZephyrPlotting.preferredLineWidth());
    plot.draw(gc, rewardDrawer, data);
    gc.setForeground(colors.color(gc, Colors.COLOR_BLACK));
    gc.setLineWidth(ZephyrPlotting.preferredLineWidth());
  }

  @Override
  public void dispose() {
    super.dispose();
    colors.dispose();
  }

  @Override
  public void onInstanceSet(Clock clock, NoStateProblem current) {
    experimentData.reset();
    rewardNormalizer.reset();
    experimentData = new ExperimentData(current);
    clock.onTick.connect(experimentData);
    super.onInstanceSet(clock, current);
  }

  @Override
  protected void setLayout(Clock clock, NoStateProblem current) {
    data = new Data2D("Reward", experimentData.actionHistory.length);
  }

  @Override
  protected boolean isInstanceSupported(Object instance) {
    return NoStateProblem.class.isInstance(instance);
  }
}
