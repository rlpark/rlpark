package rlpark.plugin.rltoysview.internal.continuousgridworld;

import java.awt.image.BufferedImage;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;

import rltoys.environments.continuousgridworld.ContinuousFunction;
import rltoys.environments.continuousgridworld.ContinuousGridworld;
import rltoys.math.ranges.Range;
import zephyr.plugin.core.utils.Colors;
import zephyr.plugin.core.utils.ImageAdapter;

public class Function2DDrawer {
  static final int[][] Landmarks = new int[][] { new int[] { 255, 0, 255 }, new int[] { 0, 255, 255 },
      new int[] { 255, 255, 0 }, new int[] { 255, 0, 0 } };
  static final int[][] Diffs = colorPreprocessing();
  static final Range[] ColorRanges = rangePreprocessing(1.0 / Diffs.length);

  private static int[] computeDiff(int[] colorA, int[] colorB) {
    return new int[] { colorA[0] - colorB[0], colorA[1] - colorB[1], colorA[2] - colorB[2] };
  }

  private static int[][] colorPreprocessing() {
    int[][] diffs = new int[Landmarks.length - 1][];
    for (int i = 0; i < diffs.length; i++)
      diffs[i] = computeDiff(Landmarks[i + 1], Landmarks[i]);
    return diffs;
  }

  private static Range[] rangePreprocessing(double colorResolution) {
    Range[] ranges = new Range[Landmarks.length - 1];
    for (int i = 0; i < ranges.length; i++)
      ranges[i] = new Range(i * colorResolution, (i + 1) * colorResolution);
    return ranges;
  }

  private int resolution;
  private float[][] imageData;
  private final Range range = new Range();
  private final Colors colors;
  private final ImageAdapter imageAdapter = new ImageAdapter();
  private BufferedImage bufferedImage = null;
  private boolean dirty = false;

  public Function2DDrawer(Colors colors) {
    this.colors = colors;
  }

  public void set(ContinuousGridworld continuousGridworld, int resolution) {
    this.resolution = resolution;
    ContinuousFunction rewardFunction = continuousGridworld.rewardFunction();
    if (rewardFunction == null)
      return;
    imageData = new float[resolution][];
    for (int i = 0; i < imageData.length; i++)
      imageData[i] = new float[resolution];
    updateImageData(continuousGridworld.getObservationRanges(), rewardFunction);
  }

  public void paint(GC gc, Canvas canvas) {
    if (imageData == null) {
      gc.setBackground(colors.color(gc, Colors.COLOR_WHITE));
      gc.fillRectangle(gc.getClipping());
      return;
    }
    updateBufferedImage(gc.getClipping());
    imageAdapter.paint(gc, canvas);
  }

  private void updateBufferedImage(Rectangle rectangle) {
    if (bufferedImage != null
        && (bufferedImage.getWidth() != rectangle.width || bufferedImage.getHeight() != rectangle.height))
      bufferedImage = null;
    if (bufferedImage == null) {
      bufferedImage = new BufferedImage(rectangle.width, rectangle.height, BufferedImage.TYPE_INT_ARGB);
      dirty = true;
    }
    if (!dirty)
      return;
    final float pixelSizeX = (float) rectangle.width / resolution;
    final float pixelSizeY = (float) rectangle.height / resolution;
    for (int ax = 0; ax < imageData.length; ax++) {
      float gx = ax * pixelSizeX;
      for (int ay = 0; ay < imageData[ax].length; ay++) {
        float gy = ay * pixelSizeY;
        int color = valueToColor(range.scale(imageData[ax][ay]));
        updatePixel(gx, pixelSizeX, gy, pixelSizeY, color);
      }
    }
    imageAdapter.update(bufferedImage);
    dirty = false;
  }

  private void updatePixel(float gx, float pixelSizeX, float gy, float pixelSizeY, int rgb) {
    for (int dx = 0; dx < pixelSizeX; dx++) {
      final int x = (int) (gx + dx);
      for (int dy = 0; dy < pixelSizeY; dy++) {
        final int y = (int) (gy + dy);
        bufferedImage.setRGB(x, y, rgb);
      }
    }
  }

  private void updateImageData(Range[] ranges, ContinuousFunction rewardFunction) {
    range.reset();
    double rangeMinX = ranges[0].min();
    double rangeLengthX = ranges[0].length();
    double rangeMinY = ranges[1].min();
    double rangeLengthY = ranges[1].length();
    for (int ax = 0; ax < resolution; ax++) {
      double x = rangeMinX + ((double) ax / resolution) * rangeLengthX;
      for (int ay = 0; ay < resolution; ay++) {
        double y = rangeMinY + ((double) ay / resolution) * rangeLengthY;
        double value = rewardFunction.fun(new double[] { x, y });
        range.update(value);
        imageData[ax][ay] = (float) value;
      }
    }
    dirty = true;
  }


  static private int colorToInt(int r, int g, int b) {
    return 0xFF000000 | (r << 16) | (g << 8) | b;
  }

  private int valueToColor(double value) {
    double adjustedValue = Math.min(value, 1.0 - 1e-10);
    int colorIndex = (int) Math.floor(adjustedValue * (Landmarks.length - 1));
    int[] minColor = Landmarks[colorIndex];
    int[] diffColor = Diffs[colorIndex];
    Range colorRange = ColorRanges[colorIndex];
    double scaledValue = colorRange.scale(adjustedValue);
    return colorToInt(minColor[0] + (int) (scaledValue * diffColor[0]), minColor[1]
        + (int) (scaledValue * diffColor[1]), minColor[2] + (int) (scaledValue * diffColor[2]));
  }

  public void unset() {
    imageData = null;
    bufferedImage = null;
  }
}
