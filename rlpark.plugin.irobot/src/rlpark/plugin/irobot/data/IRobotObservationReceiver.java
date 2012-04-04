package rlpark.plugin.irobot.data;

import rlpark.plugin.rltoys.envio.observations.Legend;
import rlpark.plugin.rltoys.math.ranges.Range;
import rlpark.plugin.robot.sync.ObservationReceiver;

public interface IRobotObservationReceiver extends ObservationReceiver {
  void sendMessage(byte[] bytes);

  Legend legend();

  Range[] ranges();
}
