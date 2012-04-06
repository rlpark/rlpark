package rlpark.plugin.irobot.data;

import rlpark.plugin.rltoys.envio.observations.Legend;
import rlpark.plugin.robot.observations.ObservationReceiver;

public interface IRobotObservationReceiver extends ObservationReceiver {
  void sendMessage(byte[] bytes);

  Legend legend();
}
