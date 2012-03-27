package rlpark.plugin.rltoysview.internal.vectors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;

import rlpark.plugin.rltoysview.internal.ColorScale;
import rltoys.math.vector.RealVector;
import rltoys.math.vector.SparseVector;
import rltoys.math.vector.VectorEntry;
import zephyr.plugin.core.internal.helpers.ClassViewProvider;
import zephyr.plugin.core.internal.utils.Colors;
import zephyr.plugin.core.internal.views.helpers.BackgroundCanvasView;

@SuppressWarnings("restriction")
public class RealVectorMapView extends BackgroundCanvasView<RealVector> {
  public static class Provider extends ClassViewProvider {
    public Provider() {
      super(RealVector.class);
    }
  }

  private final Colors colors = new Colors();
  final ColorScale colorScale = new ColorScale(new RGB(255, 255, 255));
  private RealVector copy;

  @Override
  public boolean synchronize() {
    copy = instance.current().copy();
    return true;
  }

  @Override
  public void paint(PainterMonitor painterListener, GC gc) {
    Rectangle clipping = gc.getClipping();
    gc.setBackground(colors.color(gc, Colors.COLOR_BLACK));
    gc.fillRectangle(clipping);
    updateNormalizer();
    gc.setAntialias(SWT.OFF);
    double rootSize = Math.ceil(Math.sqrt(copy.getDimension()));
    double ratio = (double) clipping.width / (double) clipping.height;
    final double xSize = Math.ceil(rootSize * ratio);
    double ySize = Math.ceil(rootSize / ratio);
    final double xPixelSize = clipping.width / xSize;
    final double yPixelSize = clipping.height / ySize;
    final int xPixelDisplaySize = (int) Math.max(xPixelSize, 1);
    final int yPixelDisplaySize = (int) Math.max(yPixelSize, 1);
    for (VectorEntry entry : copy)
      drawWeight(gc, xSize, xPixelSize, yPixelSize, xPixelDisplaySize, yPixelDisplaySize, entry.index(), entry.value());
  }

  protected void drawWeight(GC gc, double xSize, double xPixelSize, double yPixelSize, int xPixelDisplaySize,
      int yPixelDisplaySize, int index, double value) {
    int xCoord = (int) (index % xSize * xPixelSize);
    int yCoord = (int) ((int) (index / xSize) * yPixelSize);
    gc.setBackground(colors.color(gc, colorScale.color(value)));
    gc.fillRectangle(xCoord, yCoord, xPixelDisplaySize, yPixelDisplaySize);
  }

  protected void updateNormalizer() {
    colorScale.discount(0.99);
    if (copy instanceof SparseVector)
      if (((SparseVector) copy).nonZeroElements() < copy.getDimension())
        colorScale.update(0.0);
    for (VectorEntry entry : copy)
      colorScale.update(entry.value());
  }

  @Override
  public void setLayout() {
    colorScale.init();
  }

  @Override
  public void unsetLayout() {
    copy = null;
  }

  @Override
  public void dispose() {
    super.dispose();
    colors.dispose();
  }

  @Override
  protected boolean isInstanceSupported(Object instance) {
    return RealVector.class.isInstance(instance);
  }
}
