package rlpark.plugin.irobot.internal.irobot;

import java.io.IOException;

import rlpark.plugin.irobot.data.IRobotDrops;
import rlpark.plugin.irobot.data.IRobotObservationReceiver;
import rlpark.plugin.rltoys.math.ranges.Range;
import rlpark.plugin.robot.DiscoConnection;
import rlpark.plugin.robot.disco.drops.Drop;
import rlpark.plugin.robot.disco.drops.DropByteArray;

public class IRobotDiscoConnection extends DiscoConnection implements IRobotObservationReceiver {
  private final Drop commandDrop = IRobotDrops.newCommandSerialDrop();
  private final DropByteArray commandData = (DropByteArray) commandDrop.dropDatas()[0];
  private final Range[] ranges;

  public IRobotDiscoConnection(String hostname, int port, Drop sensorDrop) {
    super(hostname, port, sensorDrop);
    ranges = IRobotDrops.rangeProvider(sensorGroup).ranges(legend());
  }

  @Override
  public void sendMessage(byte[] bytes) {
    commandData.setPascalStringValue(bytes);
    send();
  }

  private void send() {
    try {
      socket.send(commandDrop);
    } catch (IOException e) {
      e.printStackTrace();
      close();
    }
  }

  @Override
  public Range[] ranges() {
    return ranges;
  }
}
