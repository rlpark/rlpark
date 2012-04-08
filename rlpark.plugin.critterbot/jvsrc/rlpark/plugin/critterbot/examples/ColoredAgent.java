package rlpark.plugin.critterbot.examples;

import java.awt.Color;

import rlpark.plugin.critterbot.data.CritterbotLabels;
import rlpark.plugin.critterbot.environment.CritterbotEnvironment;
import rlpark.plugin.critterbot.environment.CritterbotRobot;

import zephyr.plugin.core.api.synchronization.Clock;

public class ColoredAgent implements Runnable {
  final Color[] colors = new Color[CritterbotLabels.NbLeds];
  static int ledOffset = 0;
  static double currentCount = 0.0;
  static int time = 0;
  private final CritterbotEnvironment environment;
  private final Clock clock = new Clock("Agent");

  public ColoredAgent(CritterbotEnvironment environment) {
    this.environment = environment;
  }

  private Color[] computeLeds() {
    return computeLeds(colors);
  }

  static public Color[] computeLeds(Color[] colors) {
    currentCount += 0.01;
    time++;
    if (time % 8 == 0)
      ledOffset += 1;
    Color[] colorsSuite = { Color.BLUE, Color.RED, Color.GREEN };
    for (int i = 0; i < colors.length; i++) {
      int ledIndex = (i + ledOffset) % colors.length;
      colors[ledIndex] = computeColor(colorsSuite, currentCount + i);
    }
    return colors;
  }

  static private Color computeColor(Color[] colorsSuite, double countColor) {
    double floor = Math.floor(countColor);
    double ceil = Math.ceil(countColor);
    double ratio = countColor - floor;
    Color floorColor = colorsSuite[(int) floor % colorsSuite.length];
    Color ceilColor = colorsSuite[(int) ceil % colorsSuite.length];
    return new Color((int) (floorColor.getRed() * (1 - ratio) + ceilColor.getRed() * ratio),
                     (int) (floorColor.getGreen() * (1 - ratio) + ceilColor.getGreen() * ratio),
                     (int) (floorColor.getBlue() * (1 - ratio) + ceilColor.getBlue() * ratio));
  }

  @Override
  public void run() {
    while (clock.tick() && !environment.isClosed()) {
      environment.setLed(computeLeds());
    }
  }

  public static void main(String[] args) {
    CritterbotEnvironment environment = new CritterbotRobot();
    new ColoredAgent(environment).run();
  }
}
