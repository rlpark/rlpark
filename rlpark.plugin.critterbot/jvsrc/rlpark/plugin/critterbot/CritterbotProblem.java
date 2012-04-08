package rlpark.plugin.critterbot;

import rlpark.plugin.critterbot.actions.CritterbotAction;
import rlpark.plugin.rltoys.envio.observations.Legend;
import zephyr.plugin.core.api.labels.Labeled;
import zephyr.plugin.core.api.synchronization.Closeable;

public interface CritterbotProblem extends Closeable, Labeled {
  CritterbotAction lastAction();

  double[] lastReceivedObs();

  Legend legend();
}
