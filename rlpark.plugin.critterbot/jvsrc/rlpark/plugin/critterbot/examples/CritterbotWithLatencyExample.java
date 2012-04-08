package rlpark.plugin.critterbot.examples;

import rlpark.plugin.critterbot.actions.CritterbotAction;
import rlpark.plugin.critterbot.actions.VoltageSpaceAction;
import rlpark.plugin.critterbot.actions.XYThetaAction;
import rlpark.plugin.critterbot.data.CritterbotLabels;
import rlpark.plugin.critterbot.environment.CritterbotEnvironment;
import rlpark.plugin.critterbot.environment.CritterbotSimulator;
import rlpark.plugin.critterbot.environment.CritterbotSimulator.SimulatorCommand;
import rlpark.plugin.rltoys.envio.observations.Legend;
import zephyr.plugin.core.api.synchronization.Chrono;

public class CritterbotWithLatencyExample {
  final static public long Latency = 100;

  public static void main(String[] args) {
    SimulatorCommand command = CritterbotSimulator.startSimulator();
    CritterbotEnvironment environment = new CritterbotSimulator(command);
    Legend legend = environment.legend();
    Chrono chrono = new Chrono();
    while (!environment.isClosed()) {
      chrono.start();
      double[] obs = environment.waitNewObs();
      CritterbotAction action;
      if (obs[legend.indexOf(CritterbotLabels.IRDistance + "0")] > 128)
        action = new XYThetaAction(20, -20, 20);
      else
        action = new VoltageSpaceAction(20, -20, 20);
      environment.sendAction(action);
      long remainingTime = Latency - chrono.getCurrentMillis();
      if (remainingTime > 0)
        try {
          Thread.sleep(remainingTime);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
    }
  }
}
