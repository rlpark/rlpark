package rlpark.plugin.critterbot.examples;

import rlpark.plugin.critterbot.actions.CritterbotAction;
import rlpark.plugin.critterbot.actions.VoltageSpaceAction;
import rlpark.plugin.critterbot.actions.XYThetaAction;
import rlpark.plugin.critterbot.data.CritterbotLabels;
import rlpark.plugin.critterbot.environment.CritterbotEnvironment;
import rlpark.plugin.critterbot.environment.CritterbotSimulator;
import rlpark.plugin.critterbot.environment.CritterbotSimulator.SimulatorCommand;
import rlpark.plugin.rltoys.envio.observations.Legend;

public class CritterbotExample {
  public static void main(String[] args) {
    SimulatorCommand command = CritterbotSimulator.startSimulator();
    CritterbotEnvironment environment = new CritterbotSimulator(command);
    Legend legend = environment.legend();
    while (!environment.isClosed()) {
      double[] obs = environment.waitNewObs();
      CritterbotAction action;
      if (obs[legend.indexOf(CritterbotLabels.IRDistance + "0")] > 128)
        action = new XYThetaAction(10, -10, 10);
      else
        action = new VoltageSpaceAction(10, -10, 10);
      environment.sendAction(action);
    }
  }
}
