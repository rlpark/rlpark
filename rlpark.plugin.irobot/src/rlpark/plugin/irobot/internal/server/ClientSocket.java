package rlpark.plugin.irobot.internal.server;

import java.io.IOException;

import rlpark.plugin.irobot.internal.descriptors.DropDescriptors;
import rlpark.plugin.irobot.internal.irobot.IRobotSerialConnection;
import rlpark.plugin.robot.internal.disco.drops.Drop;
import rlpark.plugin.robot.internal.disco.drops.DropByteArray;
import rlpark.plugin.robot.internal.disco.io.DiscoPacket;
import rlpark.plugin.robot.internal.disco.io.DiscoSocket;
import zephyr.plugin.core.api.signals.Signal;

public class ClientSocket implements Runnable {
  public final Signal<ClientSocket> onTerminating = new Signal<ClientSocket>();
  private final Drop sensorDrop;
  private final DropByteArray sensorData;
  private final DiscoSocket socket;
  private final IRobotSerialConnection serialConnection;
  private boolean wasSendingAction = false;

  public ClientSocket(IRobotSerialConnection serialConnection, DiscoSocket socket) {
    this.socket = socket;
    this.serialConnection = serialConnection;
    Drop robotSensorDrop = serialConnection.descriptor().createSensorDrop();
    sensorDrop = DropDescriptors.newSensorSerialDrop(robotSensorDrop.name(), robotSensorDrop.dataSize());
    sensorData = (DropByteArray) sensorDrop.dropDatas()[0];
  }

  @Override
  public void run() {
    while (!serialConnection.isClosed() && !socket.isSocketClosed()) {
      byte[] data = serialConnection.waitForRawData();
      sensorData.setValue(data);
      try {
        socket.send(sensorDrop);
      } catch (IOException e) {
        e.printStackTrace();
        socket.close();
      }
      checkCommands();
    }
    onTerminating.fire(this);
  }

  private void checkCommands() {
    if (socket.isSocketClosed())
      return;
    try {
      while (socket.dataAvailable() != 0) {
        DiscoPacket packet = socket.recv();
        serialConnection.sendMessage(packet.buffer);
        wasSendingAction = true;
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public boolean wasSendingAction() {
    return wasSendingAction;
  }
}
