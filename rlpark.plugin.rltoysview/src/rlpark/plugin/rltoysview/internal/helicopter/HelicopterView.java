package rlpark.plugin.rltoysview.internal.helicopter;

import java.util.Arrays;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import rlpark.plugin.rltoys.envio.rl.TRStep;
import rlpark.plugin.rltoys.math.ranges.Range;
import rlpark.plugin.rltoys.problems.helicopter.Helicopter;
import rlpark.plugin.rltoys.problems.helicopter.HelicopterDynamics;
import zephyr.ZephyrPlotting;
import zephyr.plugin.core.internal.helpers.ClassViewProvider;
import zephyr.plugin.core.internal.utils.Colors;
import zephyr.plugin.core.internal.views.helpers.ForegroundCanvasView;

@SuppressWarnings("restriction")
public class HelicopterView extends ForegroundCanvasView<Helicopter> {
  public static class Provider extends ClassViewProvider {
    public Provider() {
      super(Helicopter.class);
    }
  }

  private final Colors colors = new Colors();
  private double[] heliState;

  @Override
  public boolean synchronize(Helicopter current) {
    TRStep step = current.lastStep();
    if (step == null || step.o_tp1 == null)
      return false;
    heliState = step.o_tp1.clone();
    return true;
  }

  @Override
  protected void paint(GC gc) {
    Rectangle clipping = gc.getClipping();
    gc.setBackground(colors.color(gc, Colors.COLOR_BLACK));
    if (heliState == null)
      return;
    gc.fillRectangle(clipping);
    int viewWidth = clipping.width / 3;
    drawPosition(gc, new Rectangle(viewWidth * 0, 0, viewWidth, clipping.height));
    drawVelocity(gc, new Rectangle(viewWidth * 1, 0, viewWidth, clipping.height));
    drawRotationalVelocity(gc, new Rectangle(viewWidth * 2, 0, viewWidth, clipping.height));
  }

  private void drawPosition(GC gc, Rectangle rectangle) {
    gc.setForeground(colors.color(gc, Colors.COLOR_LIGHT_RED));
    drawCenter(gc, rectangle);
    gc.setForeground(colors.color(gc, Colors.COLOR_RED));
    drawState(gc, rectangle, new Range(-HelicopterDynamics.MaxPos, HelicopterDynamics.MaxPos),
              Arrays.copyOfRange(heliState, 3, 6));
  }

  private void drawVelocity(GC gc, Rectangle rectangle) {
    gc.setForeground(colors.color(gc, Colors.COLOR_LIGHT_GREEN));
    drawCenter(gc, rectangle);
    gc.setForeground(colors.color(gc, Colors.COLOR_GREEN));
    drawState(gc, rectangle, new Range(-HelicopterDynamics.MaxVel, HelicopterDynamics.MaxVel),
              Arrays.copyOfRange(heliState, 0, 3));
  }

  private void drawRotationalVelocity(GC gc, Rectangle rectangle) {
    gc.setForeground(colors.color(gc, Colors.COLOR_LIGHT_BLUE));
    drawCenter(gc, rectangle);
    gc.setForeground(colors.color(gc, Colors.COLOR_BLUE));
    drawState(gc, rectangle, new Range(-HelicopterDynamics.MaxRate, HelicopterDynamics.MaxRate),
              Arrays.copyOfRange(heliState, 6, 9));
  }

  private int refSize(Rectangle rectangle) {
    int refSize = Math.min(rectangle.width / 2, rectangle.height / 2);
    return refSize;
  }

  private Point center(Rectangle rectangle) {
    return new Point(rectangle.x + rectangle.width / 2, rectangle.y + rectangle.height / 2);
  }

  private void drawCenter(GC gc, Rectangle rectangle) {
    int lineWidth = ZephyrPlotting.preferredLineWidth();
    gc.setLineWidth(lineWidth);
    Point center = center(rectangle);
    drawPlusOnPosition(gc, center);
    int refSize = refSize(rectangle);
    gc.drawRectangle(center.x - refSize / 2, center.y - refSize / 2, refSize, refSize);
  }

  private void drawPlusOnPosition(GC gc, Point center) {
    int lineWidth = ZephyrPlotting.preferredLineWidth();
    gc.setLineWidth(lineWidth);
    gc.drawLine(center.x - 2 * lineWidth, center.y, center.x + 2 * lineWidth, center.y);
    gc.drawLine(center.x, center.y - 2 * lineWidth, center.x, center.y + 2 * lineWidth);
  }

  private void drawState(GC gc, Rectangle rectangle, Range range, double[] data) {
    int lineWidth = ZephyrPlotting.preferredLineWidth();
    gc.setLineWidth(lineWidth);
    int positionScaling = Math.min(rectangle.width / 2, rectangle.height / 2);
    Point positionOffset = new Point((int) (scale(range, data[0]) * positionScaling),
                                     (int) (scale(range, data[1]) * positionScaling));
    Point center = center(rectangle);
    Point current = new Point(center.x + positionOffset.x, center.y + positionOffset.y);
    drawPlusOnPosition(gc, current);
    int refSize = refSize(rectangle);
    int sizeOffset = (int) (scale(range, data[2]) * refSize);
    int currentSize = refSize + sizeOffset;
    gc.drawRectangle(current.x - currentSize / 2, current.y - currentSize / 2, currentSize, currentSize);

  }

  private double scale(Range range, double value) {
    return ((value - range.min()) / range.length()) - .5;
  }

  @Override
  protected boolean isInstanceSupported(Object instance) {
    return (instance instanceof Helicopter);
  }
}
