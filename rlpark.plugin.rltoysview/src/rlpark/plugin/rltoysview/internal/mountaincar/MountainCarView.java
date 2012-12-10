package rlpark.plugin.rltoysview.internal.mountaincar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

import rlpark.plugin.rltoys.envio.rl.TRStep;
import rlpark.plugin.rltoys.math.ranges.Range;
import rlpark.plugin.rltoys.problems.mountaincar.MountainCar;
import zephyr.ZephyrPlotting;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.internal.canvas.BackgroundImage;
import zephyr.plugin.core.internal.helpers.ClassViewProvider;
import zephyr.plugin.core.internal.utils.Colors;
import zephyr.plugin.core.internal.views.helpers.ForegroundCanvasView;
import zephyr.plugin.plotting.internal.axes.Axes;

@SuppressWarnings("restriction")
public class MountainCarView extends ForegroundCanvasView<MountainCar> {
  public static class Provider extends ClassViewProvider {
    public Provider() {
      super(MountainCar.class);
    }
  }

  private static final int CarSize = 10;

  private final Colors colors = new Colors();
  private final Axes axes = new Axes();
  private final BackgroundImage backgroundImage = new BackgroundImage();
  private int backgroundLineSize = -1;
  private double position;

  @Override
  public boolean synchronize(MountainCar current) {
    TRStep step = current.lastStep();
    if (step == null || step.o_tp1 == null)
      return false;
    position = step.o_tp1[0];
    return true;
  }

  @Override
  protected void paint(GC gc) {
    Rectangle clipping = gc.getClipping();
    axes.updateScaling(clipping);
    updateBackground(gc.getDevice(), clipping);
    gc.drawImage(backgroundImage.image(), 0, 0);
    gc.setAntialias(ZephyrPlotting.preferredAntiAliasing() ? SWT.ON : SWT.OFF);
    int lineWidth = ZephyrPlotting.preferredLineWidth();
    int carSize = CarSize * lineWidth;
    gc.setBackground(colors.color(gc, Colors.COLOR_BLACK));
    gc.fillOval(axes.toGX(position) - (carSize / 2), axes.toGY(MountainCar.height(position)) - (carSize / 2), carSize,
                carSize);
  }

  private void updateBackground(Device device, Rectangle region) {
    if (!backgroundImage.needUpdate(region) && backgroundLineSize == ZephyrPlotting.preferredLineWidth())
      return;
    backgroundLineSize = ZephyrPlotting.preferredLineWidth();
    GC gc = backgroundImage.acquireGC(device, region);
    gc.setBackground(colors.color(gc, Colors.COLOR_WHITE));
    gc.fillRectangle(region);
    final int nbPoint = 100;
    double resolution = (double) region.width / 100;
    for (int i = 0; i < nbPoint - 1; i++) {
      int x1 = (int) (i * resolution);
      double position01 = axes.toDX(x1);
      int x2 = (int) ((i + 1) * resolution);
      double position02 = axes.toDX(x2);
      gc.drawLine(x1, axes.toGY(MountainCar.height(position01)), x2, axes.toGY(MountainCar.height(position02)));
    }
    backgroundImage.releaseGC();
  }

  @Override
  public void onInstanceSet(Clock clock, MountainCar problem) {
    super.onInstanceSet(clock, problem);
    Range[] obsRanges = problem.getObservationRanges();
    axes.x.update(obsRanges[0].min());
    axes.x.update(obsRanges[0].max());
    axes.y.update(-1);
    axes.y.update(1);
  }

  @Override
  public void onInstanceUnset(Clock clock) {
    super.onInstanceUnset(clock);
    axes.x.reset();
    axes.y.reset();
  }

  @Override
  protected boolean isInstanceSupported(Object instance) {
    return MountainCar.class.isInstance(instance);
  }
}
