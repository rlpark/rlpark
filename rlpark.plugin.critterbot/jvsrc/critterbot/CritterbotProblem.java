package critterbot;

import rlpark.plugin.rltoys.envio.observations.Legend;
import zephyr.plugin.core.api.labels.Labeled;
import zephyr.plugin.core.api.synchronization.Closeable;
import critterbot.actions.CritterbotAction;

public interface CritterbotProblem extends Closeable, Labeled {
  CritterbotAction lastAction();

  double[] lastReceivedObs();

  Legend legend();
}
