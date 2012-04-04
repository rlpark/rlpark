package rlpark.example.robots.critterbot;

import rlpark.plugin.rltoys.envio.observations.Legend;
import critterbot.actions.CritterbotAction;
import critterbot.actions.VoltageSpaceAction;
import critterbot.actions.XYThetaAction;
import critterbot.environment.CritterbotDrops;
import critterbot.environment.CritterbotEnvironment;
import critterbot.environment.CritterbotSimulator;
import critterbot.environment.CritterbotSimulator.SimulatorCommand;

public class CritterbotExample {
  public static void main(String[] args) {
    SimulatorCommand command = CritterbotSimulator.startSimulator();
    CritterbotEnvironment environment = new CritterbotSimulator(command);
    Legend legend = environment.legend();
    while (!environment.isClosed()) {
      double[] obs = environment.waitNewObs();
      CritterbotAction action;
      if (obs[legend.indexOf(CritterbotDrops.IRDistance + "0")] > 128)
        action = new XYThetaAction(10, -10, 10);
      else
        action = new VoltageSpaceAction(10, -10, 10);
      environment.sendAction(action);
    }
  }
}
