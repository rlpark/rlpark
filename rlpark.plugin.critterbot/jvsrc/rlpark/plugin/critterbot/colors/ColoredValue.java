package rlpark.plugin.critterbot.colors;

import java.awt.Color;

public class ColoredValue {
  private float min = Float.MAX_VALUE;
  private float max = -Float.MAX_VALUE;
  private final float baseColorRed;
  private final float baseColorGreen;
  private final float baseColorBlue;

  public ColoredValue(Color baseColor) {
    baseColorRed = baseColor.getRed() / 255f;
    baseColorGreen = baseColor.getGreen() / 255f;
    baseColorBlue = baseColor.getBlue() / 255f;
  }

  public Color getColor(double value) {
    min = (float) Math.min(min, value);
    max = (float) Math.max(max, value);
    float scaledValue = 0.0f;
    if (max - min > 0)
      scaledValue = ((float) value - min) / (max - min);
    return new Color(scaledValue * baseColorRed, scaledValue * baseColorGreen, scaledValue * baseColorBlue);
  }
}
