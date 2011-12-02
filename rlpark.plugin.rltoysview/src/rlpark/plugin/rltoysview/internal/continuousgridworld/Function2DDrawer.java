package rlpark.plugin.rltoysview.internal.continuousgridworld;

import java.awt.image.BufferedImage;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;

import rltoys.environments.continuousgridworld.ContinuousFunction;
import zephyr.plugin.core.utils.Colors;
import zephyr.plugin.core.utils.ImageAdapter;
import zephyr.plugin.plotting.axes.Axes;

public class Function2DDrawer {
  private int resolution;
  private int[][] imageData;
  private final Colors colors;
  private final Axes axes;
  private final ImageAdapter imageAdapter = new ImageAdapter();
  private BufferedImage bufferedImage = null;
  private Rectangle drawingRegion = null;
  private boolean dirty = false;

  public Function2DDrawer(Colors colors, Axes axes) {
    this.colors = colors;
    this.axes = axes;
  }

  public void paint(GC gc, Canvas canvas, int resolution) {
    adjustDataSize(gc.getClipping(), resolution);
    if (imageData == null) {
      gc.setBackground(colors.color(gc, Colors.COLOR_WHITE));
      gc.fillRectangle(gc.getClipping());
      return;
    }
    updateBufferedImage();
    imageAdapter.paint(gc, canvas);
  }

  private void updateBufferedImage() {
    if (bufferedImage == null) {
      bufferedImage = new BufferedImage(drawingRegion.width, drawingRegion.height, BufferedImage.TYPE_INT_ARGB);
      dirty = true;
    }
    if (!dirty)
      return;
    for (int ax = 0; ax < imageData.length; ax++) {
      int gx = ax * resolution;
      for (int ay = 0; ay < imageData[ax].length; ay++) {
        int gy = ay * resolution;
        updatePixel(gx, gy, imageData[ax][ay]);
      }
    }
    imageAdapter.update(bufferedImage);
    dirty = false;
  }

  private void updatePixel(int gx, int gy, int rgb) {
    int width = bufferedImage.getWidth();
    int height = bufferedImage.getHeight();
    for (int dx = 0; dx < resolution; dx++) {
      final int x = gx + dx;
      if (x >= width)
        break;
      for (int dy = 0; dy < resolution; dy++) {
        final int y = gy + dy;
        if (y >= height)
          break;
        bufferedImage.setRGB(x, y, rgb);
      }
    }
  }

  private void adjustDataSize(Rectangle clipping, int resolution) {
    if (drawingRegion != null && clipping.equals(drawingRegion) && this.resolution == resolution)
      return;
    reset();
    drawingRegion = new Rectangle(clipping.x, clipping.y, clipping.width, clipping.height);
    this.resolution = resolution;
  }

  public void reset() {
    bufferedImage = null;
    imageData = null;
    drawingRegion = null;
    resolution = -1;
  }

  public void synchronize(ContinuousFunction rewardFunction) {
    if (imageData != null || drawingRegion == null)
      return;
    initializeImageData();
    updateImageData(rewardFunction);
  }

  private void updateImageData(ContinuousFunction rewardFunction) {
    double rangeMin = rewardFunction.range().min();
    double rangeLength = rewardFunction.range().length();
    for (int ax = 0; ax < imageData.length; ax++) {
      int gx = ax * resolution;
      for (int ay = 0; ay < imageData[ax].length; ay++) {
        int gy = ay * resolution;
        double[] position = new double[] { axes.toDX(gx), axes.toDY(gy) };
        double value = rewardFunction.fun(position);
        double scaledValue = (value - rangeMin) / rangeLength;
        imageData[ax][ay] = valueToColor(scaledValue);
      }
    }
    dirty = true;
  }

  private int valueToColor(double value) {
    return 0xFF000000 | (int) (255 * value);
  }

  private void initializeImageData() {
    imageData = new int[drawingRegion.width / resolution][];
    for (int i = 0; i < imageData.length; i++)
      imageData[i] = new int[drawingRegion.height];
  }
}
