package rlpark.plugin.irobot.internal.descriptors;

import rlpark.plugin.rltoys.envio.observations.Legend;
import rlpark.plugin.robot.observations.ObservationReceiver;

public interface IRobotObservationReceiver extends ObservationReceiver {
  void sendMessage(byte[] bytes);

  Legend legend();
}
