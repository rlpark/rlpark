package rlpark.plugin.rltoysview.internal;

import org.eclipse.swt.graphics.RGB;

public class ColorScale {
  private double min;
  private double max;
  private final RGB strictPositiveColor;
  private final RGB color = new RGB(0, 0, 0);
  private double maxAbs;

  public ColorScale(RGB strictPositiveColor) {
    this.strictPositiveColor = strictPositiveColor;
  }

  public void init() {
    min = Double.MAX_VALUE;
    max = -Double.MAX_VALUE;
    maxAbs = -Double.MAX_VALUE;
    color.red = 0;
    color.green = 0;
    color.blue = 0;
  }

  public void update(double value) {
    min = Math.min(value, min);
    max = Math.max(value, max);
    maxAbs = Math.max(Math.abs(value), maxAbs);
  }

  public RGB color(double value) {
    if (min - max == 0)
      return color;
    double scaledValue = Math.min(Math.abs(value) / maxAbs, 1.0);
    if (min < 0) {
      color.green = 20;
      color.blue = (int) (value < 0 ? scaledValue * 255 : 0);
      color.red = (int) (value > 0 ? scaledValue * 255 : 0);
    } else {
      color.green = (int) (strictPositiveColor.green * scaledValue);
      color.blue = (int) (strictPositiveColor.blue * scaledValue);
      color.red = (int) (strictPositiveColor.red * scaledValue);
    }
    return color;
  }

  public void discount(double discountValue) {
    min *= discountValue;
    max *= discountValue;
    maxAbs *= discountValue;
  }
}
