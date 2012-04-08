package rlpark.plugin.critterbot.agents;

import java.util.Random;

import rlpark.plugin.critterbot.actions.CritterbotAction;
import rlpark.plugin.critterbot.actions.XYThetaAction;
import rlpark.plugin.critterbot.data.CritterbotObservation;
import rlpark.plugin.critterbot.environment.CritterbotEnvironment;
import rlpark.plugin.critterbot.environment.CritterbotRobot;
import rlpark.plugin.rltoys.agents.Agent;
import zephyr.plugin.core.api.synchronization.Clock;

public class DockingAgent implements Agent {
  private static final int RIGHT_TIMEOUT = 1 * 100;
  private static final int ABANDON_TIMEOUT = 10 * 100;
  static private final double lambda = .01;
  private int[] IRLights;
  private double[] wmaIRLights;
  private final Random random = new Random(0);
  private CritterbotAction lastCommand = XYThetaAction.NoMove;
  private final CritterbotEnvironment environment;
  private int abandonTimeout = ABANDON_TIMEOUT;
  private boolean abandonMode = false;
  private int timesteps = 0;

  public DockingAgent(CritterbotEnvironment environment) {
    this.environment = environment;
  }

  @Override
  public CritterbotAction getAtp1(double[] envObs) {
    CritterbotObservation obs = environment.getCritterbotObservation(envObs);
    // System.out.println(String.format("abandonMode: %b abandonTimeout: %d",
    // abandonMode, abandonTimeout));
    if (obs.busVoltage > 170)
      return XYThetaAction.NoMove;
    else if (abandonMode) {
      abandonTimeout -= 1;
      if (abandonTimeout <= 0) {
        abandonMode = false;
        System.out.println("End Timeout");
        abandonTimeout = ABANDON_TIMEOUT;
      }
      return lastCommand;
    } else {
      abandonTimeout -= 1;
      if (abandonTimeout <= 0) {
        abandonMode = true;
        abandonTimeout = RIGHT_TIMEOUT;
        if (random.nextDouble() > .5) {
          System.out.println("Timeout: Moving right");
          lastCommand = new XYThetaAction(0, 20, 0);
        } else {
          System.out.println("Timeout: Stopping");
          lastCommand = new XYThetaAction(0, 0, 0);
        }
        return lastCommand;
      }
    }
    if (timesteps > 0) {
      timesteps--;
      return lastCommand;
    }

    IRLights = obs.irLight;
    if (wmaIRLights == null) {
      wmaIRLights = new double[IRLights.length];
      for (int i = 0; i < IRLights.length; i++)
        wmaIRLights[i] = IRLights[i];
    } else
      for (int i = 0; i < IRLights.length; i++)
        wmaIRLights[i] = lambda * IRLights[i] + (1 - lambda) * wmaIRLights[i];

    int max_ind = -1; // where is the strongest indication of the dock, in a
    // local neighborhood of the sensors
    double max_val = -1;
    for (int i = 0; i < 8; i++) {
      double val = Math.abs(IRLights[(i - 1 + 8) % 8] - wmaIRLights[(i - 1 + 8) % 8]) +
          Math.abs(IRLights[i] - wmaIRLights[i]) +
          Math.abs(IRLights[(i + 1) % 8] - wmaIRLights[(i + 1) % 8]);
      if (val > max_val) {
        max_ind = i;
        max_val = val;
      }
    }

    if (random.nextDouble() > .1 && max_ind >= 0 && max_val > 10) {// 20
      System.out.println("IR" + max_ind + " " + max_val);
      // for (double d : IRLights) {
      // System.out.print(" " + d);
      // }
      // System.out.println();

      if (max_ind == 2)
        lastCommand = new XYThetaAction(0, -20, 0);
      else if (4 < (max_ind - 2 + 4) % 8) { // in one direction, turn or
        // move back

        lastCommand = new XYThetaAction(0, 0, 5);
        if (random.nextDouble() > .3)
          lastCommand = new XYThetaAction(-10, 0, 0);
        else if (random.nextDouble() > .2)
          lastCommand = new XYThetaAction(0, -10, 0);
      } else { // if the other direction, turn or move forward.
        lastCommand = new XYThetaAction(0, 0, -5);
        if (random.nextDouble() > .3)
          lastCommand = new XYThetaAction(10, 0, 0);
        else if (random.nextDouble() > .2)
          lastCommand = new XYThetaAction(0, 10, 0);

      }

      // double radians = (max_ind * 45 + 0) * Math.PI / 180.;
      // lastCommand = new XYThetaAction(20 * Math.cos(radians), 20 *
      // Math.sin(radians), 0);
      timesteps = 50;
      System.out.println(" Command" + lastCommand);
      return lastCommand;
    }

    System.out.println("IR null");
    timesteps = 20;
    lastCommand = new XYThetaAction(0, 0, 0);
    return lastCommand;
  }

  public static void main(String[] args) {
    final CritterbotEnvironment environment = new CritterbotRobot();
    Agent agent = new DockingAgent(environment);
    Clock clock = new Clock("Docking");
    while (clock.tick() && !environment.isClosed())
      environment.sendAction((CritterbotAction) agent.getAtp1(environment.waitNewObs()));
  }
}
