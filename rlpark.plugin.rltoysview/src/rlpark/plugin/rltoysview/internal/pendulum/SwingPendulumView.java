package rlpark.plugin.rltoysview.internal.pendulum;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;

import rlpark.plugin.rltoys.problems.pendulum.SwingPendulum;
import zephyr.plugin.core.internal.helpers.ClassViewProvider;
import zephyr.plugin.core.internal.utils.Colors;
import zephyr.plugin.core.internal.views.helpers.ForegroundCanvasView;

@SuppressWarnings("restriction")
public class SwingPendulumView extends ForegroundCanvasView<SwingPendulum> {
  public static class Provider extends ClassViewProvider {
    public Provider() {
      super(SwingPendulum.class);
    }
  }

  private static final RGB COLOR_LIGHT_GRAY = new RGB(224, 224, 224);
  private final Colors colors = new Colors();
  private double theta_t;
  private double theta_tm1;

  @Override
  public boolean synchronize(SwingPendulum problem) {
    theta_tm1 = theta_t;
    theta_t = problem.theta();
    return true;
  }

  @Override
  protected void paint(GC gc) {
    gc.setBackground(colors.color(gc, Colors.COLOR_WHITE));
    Rectangle clipping = gc.getClipping();
    gc.fillRectangle(clipping);
    gc.setAntialias(SWT.ON);
    gc.setLineWidth(2);
    int length = (int) (Math.min(clipping.width * 0.8, clipping.height * 0.8) / 2);
    int x1 = clipping.x + clipping.width / 2;
    int y1 = clipping.y + clipping.height / 2;
    drawSupport(gc, x1, y1, length);
    drawPendulum(gc, x1, y1, length, theta_tm1, COLOR_LIGHT_GRAY);
    drawPendulum(gc, x1, y1, length, theta_t, Colors.COLOR_BLACK);
  }

  private void drawPendulum(GC gc, int x1, int y1, int length, double theta, RGB color) {
    gc.setForeground(colors.color(gc, color));
    gc.setBackground(colors.color(gc, color));
    int x2 = x1 + (int) (Math.cos(theta - Math.PI / 2) * length);
    int y2 = y1 + (int) (Math.sin(theta - Math.PI / 2) * length);
    gc.drawLine(x1, y1, x2, y2);
    int weightRadius = Math.max(2, length / 12) / 2;
    gc.fillOval(x2 - weightRadius, y2 - weightRadius, weightRadius * 2, weightRadius * 2);
  }

  private void drawSupport(GC gc, int x1, int y1, int length) {
    int supportRadius = Math.max(2, length / 8) / 2;
    gc.drawOval(x1 - supportRadius, y1 - supportRadius, supportRadius * 2, supportRadius * 2);
  }

  @Override
  protected boolean isInstanceSupported(Object instance) {
    return SwingPendulum.class.isInstance(instance);
  }
}
