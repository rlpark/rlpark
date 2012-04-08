package rlpark.plugin.irobot.internal.descriptors;

import gnu.io.SerialPort;

import java.io.IOException;

import rlpark.plugin.irobot.data.IRobotLabels;
import rlpark.plugin.irobot.internal.serial.SerialPortToRobot;
import rlpark.plugin.irobot.internal.serial.SerialPortToRobot.SerialPortInfo;
import rlpark.plugin.irobot.internal.statemachine.ChecksumNode;
import rlpark.plugin.irobot.internal.statemachine.DataNode;
import rlpark.plugin.irobot.internal.statemachine.HeaderNode;
import rlpark.plugin.irobot.internal.statemachine.SerialLinkStateMachine;
import rlpark.plugin.rltoys.utils.Utils;
import rlpark.plugin.robot.internal.disco.drops.Drop;
import zephyr.plugin.core.api.signals.Listener;

public class CreateSerialDescriptor implements IRobotSerialDescriptor {
  class StartCommunicationRunnable implements Runnable {

    private final SerialPortToRobot serialPort;

    StartCommunicationRunnable(SerialPortToRobot serialPort) {
      this.serialPort = serialPort;
    }

    @Override
    public void run() {
      try {
        Thread.sleep(2500);
        System.out.println("Setting communication");
        initializeRobotCommunication(serialPort);
      } catch (IOException e) {
        e.printStackTrace();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

  }

  Thread startCommunicationThread = null;

  @Override
  public boolean initializeRobotCommunication(SerialPortToRobot serialPort) throws IOException {
    serialPort.sendAndWait(new byte[] { (byte) 128 });
    serialPort.sendAndWait(new byte[] { (byte) 148, 1, 6 });
    return true;
  }

  @Override
  public SerialLinkStateMachine createStateMachine(final SerialPortToRobot serialPort) {
    HeaderNode headerNode = new HeaderNode(19, 53);
    headerNode.onMisalignedPackets.connect(new Listener<Integer>() {
      @Override
      public void listen(Integer misalignedPacket) {
        if (misalignedPacket % 200 == 0 && (startCommunicationThread == null || !startCommunicationThread.isAlive())) {
          startCommunicationThread = new Thread(new StartCommunicationRunnable(serialPort), "ReinitializeCommunication");
          startCommunicationThread.start();
        }
      }
    });
    DataNode dataNode = new DataNode(IRobotLabels.CreateSensorsPacketSize);
    ChecksumNode checksumNode = new ChecksumNode(Utils.asList(dataNode, headerNode));
    return new SerialLinkStateMachine(serialPort, checksumNode, headerNode, dataNode);
  }

  @Override
  public Drop createSensorDrop() {
    return DropDescriptors.newCreateSensorDrop();
  }

  @Override
  public byte[] messageOnNoClient() {
    return new byte[] { (byte) 145, 0, 0, 0, 0 };
  }

  @Override
  public SerialPortInfo portInfo() {
    return new SerialPortInfo(57600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE,
                              SerialPort.FLOWCONTROL_NONE);
  }
}