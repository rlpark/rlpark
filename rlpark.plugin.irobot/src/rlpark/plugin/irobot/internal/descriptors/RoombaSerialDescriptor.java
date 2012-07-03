package rlpark.plugin.irobot.internal.descriptors;

import gnu.io.SerialPort;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import rlpark.plugin.irobot.data.IRobotLabels;
import rlpark.plugin.irobot.internal.serial.SerialPortToRobot;
import rlpark.plugin.irobot.internal.serial.SerialPortToRobot.SerialPortInfo;
import rlpark.plugin.irobot.internal.statemachine.DataNode;
import rlpark.plugin.irobot.internal.statemachine.SerialLinkNode;
import rlpark.plugin.irobot.internal.statemachine.SerialLinkStateMachine;
import rlpark.plugin.robot.internal.disco.drops.Drop;
import zephyr.plugin.core.api.signals.Listener;

public class RoombaSerialDescriptor implements IRobotSerialDescriptor {
  static class PacketRequester implements Runnable, Listener<byte[]> {
    private final SerialPortToRobot serialPort;
    private final Semaphore semaphore = new Semaphore(2);

    PacketRequester(SerialPortToRobot serialPort) {
      this.serialPort = serialPort;
    }

    @Override
    public void run() {
      while (sendRequest()) {
        avoidOverloading();
        acquireSemaphore();
      }
    }

    private void avoidOverloading() {
      try {
        Thread.sleep(50);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    private boolean sendRequest() {
      if (serialPort.isClosed())
        return false;
      try {
        serialPort.send(new byte[] { (byte) 142, 100 });
      } catch (IOException e) {
        e.printStackTrace();
        serialPort.close();
        return false;
      }
      return true;
    }

    private void acquireSemaphore() {
      try {
        semaphore.acquire();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    @Override
    public void listen(byte[] eventInfo) {
      semaphore.release();
    }
  }

  static public final boolean SetupFireflyMandatory = true;
  static public final long Latency = 10;
  private PacketRequester packetRequester;

  private boolean setupFirefly(SerialPortToRobot serialPort) {
    System.out.println("Setting up Roomba's firefly...");
    try {
      serialPort.sendAndExpect("$$$", "CMD\r\n");
      serialPort.sendAndExpect("U,115k,N\r", "AOK\r\n");
    } catch (IOException e) {
      System.out.println("Setting up Firefly has failed...");
      if (SetupFireflyMandatory) {
        e.printStackTrace();
        return false;
      }
    }
    return true;
  }

  private boolean setupRoomba(final SerialPortToRobot serialPort) {
    System.out.println("Setting up Roomba...");
    serialPort.wakeupRobot();
    try {
      serialPort.sendAndWait(new char[] { 128 });
      serialPort.sendAndWait(new char[] { 131 });
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
    packetRequester = new PacketRequester(serialPort);
    new Thread(packetRequester).start();
    return true;
  }

  @Override
  public boolean initializeRobotCommunication(SerialPortToRobot serialPort) throws IOException {
    boolean fireflySet = setupFirefly(serialPort);
    if (!fireflySet)
      return false;
    return setupRoomba(serialPort);
  }

  @Override
  public SerialLinkStateMachine createStateMachine(SerialPortToRobot serialPort) {
    List<SerialLinkNode> serialLinkNodes = new ArrayList<SerialLinkNode>();
    serialLinkNodes.add(new DataNode(IRobotLabels.RoombaSensorsPacketSize));
    SerialLinkStateMachine serialLinkStateMachine = new SerialLinkStateMachine(serialPort, serialLinkNodes);
    serialLinkStateMachine.onDataPacket.connect(packetRequester);
    return serialLinkStateMachine;
  }

  @Override
  public Drop createSensorDrop() {
    return DropDescriptors.newRoombaSensorDrop();
  }

  @Override
  public byte[] messageOnNoClient() {
    return new byte[] { (byte) 146, 0, 0, 0, 0 };
  }

  @Override
  public SerialPortInfo portInfo() {
    return new SerialPortInfo(115200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE,
                              SerialPort.FLOWCONTROL_NONE);
  }
}