package rlpark.plugin.critterbot.examples;

import java.awt.Color;
import java.util.Arrays;

import rlpark.plugin.critterbot.actions.XYThetaAction;
import rlpark.plugin.critterbot.colors.ColoredValue;
import rlpark.plugin.critterbot.environment.CritterbotEnvironment;
import rlpark.plugin.critterbot.environment.CritterbotRobot;

import zephyr.plugin.core.api.synchronization.Clock;

public class HLightSeeker {
  static final double ActionMax = 20.0;
  static final int lightGoal = 2;
  static final double alpha = 0.7;

  private final ColoredValue coloredValue = new ColoredValue(Color.CYAN);
  private double lightValue;
  private final CritterbotEnvironment environment;

  public HLightSeeker(CritterbotEnvironment environment) {
    this.environment = environment;
  }

  private int lightIndex(int index) {
    return (index + 8) % 4;
  }

  private int light(int[] lights, int index) {
    return lights[lightIndex(index)];
  }

  public XYThetaAction getAtp1(double[] obs) {
    int[] lights = environment.getCritterbotObservation(obs).light;
    lightValue = light(lights, lightGoal);
    double rotation = Math.signum(light(lights, lightGoal - 1) - light(lights, lightGoal + 1));
    double x = -Math.signum(light(lights, lightGoal) - light(lights, lightGoal + 2));
    return new XYThetaAction(x * 10, 0, rotation * 10.0);
  }

  public void setLeds(Color[] colors) {
    Arrays.fill(colors, coloredValue.getColor(lightValue));
  }

  public static void main(String[] args) {
    CritterbotEnvironment environment = new CritterbotRobot();
    HLightSeeker agent = new HLightSeeker(environment);
    Clock clock = new Clock();
    while (clock.tick() && !environment.isClosed())
      environment.sendAction(agent.getAtp1(environment.waitNewObs()));
  }
}
