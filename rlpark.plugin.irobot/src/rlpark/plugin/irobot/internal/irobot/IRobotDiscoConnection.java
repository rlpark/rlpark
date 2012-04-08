package rlpark.plugin.irobot.internal.irobot;

import java.io.IOException;

import rlpark.plugin.irobot.internal.descriptors.DropDescriptors;
import rlpark.plugin.irobot.internal.descriptors.IRobotObservationReceiver;
import rlpark.plugin.robot.internal.disco.DiscoConnection;
import rlpark.plugin.robot.internal.disco.drops.Drop;
import rlpark.plugin.robot.internal.disco.drops.DropByteArray;

public class IRobotDiscoConnection extends DiscoConnection implements IRobotObservationReceiver {
  private final Drop commandDrop = DropDescriptors.newCommandSerialDrop();
  private final DropByteArray commandData = (DropByteArray) commandDrop.dropDatas()[0];

  public IRobotDiscoConnection(String hostname, int port, Drop sensorDrop) {
    super(hostname, port, sensorDrop);
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
}
