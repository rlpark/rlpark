package rlpark.plugin.irobot.internal.irobot;


import java.io.IOException;

import rlpark.plugin.irobot.internal.descriptors.IRobotObservationReceiver;
import rlpark.plugin.irobot.internal.descriptors.IRobotSerialDescriptor;
import rlpark.plugin.irobot.internal.serial.SerialPortToRobot;
import rlpark.plugin.irobot.internal.statemachine.SerialLinkStateMachine;
import rlpark.plugin.rltoys.envio.observations.Legend;
import rlpark.plugin.robot.internal.disco.datagroup.DropScalarGroup;
import rlpark.plugin.robot.internal.disco.drops.Drop;
import rlpark.plugin.robot.internal.sync.LiteByteBuffer;
import rlpark.plugin.robot.internal.sync.Syncs;
import rlpark.plugin.robot.observations.ObservationVersatile;
import zephyr.plugin.core.api.signals.Listener;
import zephyr.plugin.core.api.signals.Signal;
import zephyr.plugin.core.api.synchronization.Chrono;

public class IRobotSerialConnection implements IRobotObservationReceiver {
  public Signal<IRobotSerialConnection> onClosed = new Signal<IRobotSerialConnection>();
  private final Drop sensorDrop;
  protected final DropScalarGroup sensors;
  protected SerialPortToRobot serialPort;
  protected final String fileName;
  private SerialLinkStateMachine stateMachine;
  private final IRobotSerialDescriptor serialDescriptor;
  private final Listener<byte[]> sensorDataListener = new Listener<byte[]>() {
    @Override
    public void listen(byte[] sensorData) {
      receiveData(sensorData);
    }
  };
  private final LiteByteBuffer byteBuffer;
  private Chrono timeSinceReset = null;

  public IRobotSerialConnection(String fileName, IRobotSerialDescriptor serialDescriptor) {
    this.fileName = fileName;
    sensorDrop = serialDescriptor.createSensorDrop();
    sensors = new DropScalarGroup(sensorDrop);
    this.serialDescriptor = serialDescriptor;
    byteBuffer = new LiteByteBuffer(sensorDrop.dataSize());
  }

  synchronized protected void receiveData(byte[] sensorData) {
    byteBuffer.clear();
    byteBuffer.put(sensorData);
    notifyAll();
  }

  @Override
  public void sendMessage(byte[] bytes) {
    if (!canWriteOnSerialPort())
      return;
    try {
      serialPort.send(bytes);
    } catch (IOException e) {
      e.printStackTrace();
      close();
      SerialPortToRobot.fatalError("error while sending message");
    }
    checkResetCommand(bytes);
  }

  protected void checkResetCommand(byte[] bytes) {
    if (bytes[0] == 7)
      if (timeSinceReset == null)
        timeSinceReset = new Chrono();
      else
        timeSinceReset.start();
  }

  protected boolean canWriteOnSerialPort() {
    return timeSinceReset == null || timeSinceReset.getCurrentChrono() > 3.0;
  }

  synchronized public void close() {
    serialPort.close();
    notifyAll();
    onClosed.fire(this);
  }

  @Override
  public Legend legend() {
    return sensors.legend();
  }

  @Override
  public void initialize() {
    serialPort = SerialPortToRobot.openPort(fileName, serialDescriptor.portInfo());
    if (serialPort == null)
      return;
    serialPort.onClosed.connect(new Listener<SerialPortToRobot>() {
      @Override
      public void listen(SerialPortToRobot eventInfo) {
        close();
      }
    });
    serialPort.wakeupRobot();
    try {
      serialDescriptor.initializeRobotCommunication(serialPort);
    } catch (IOException e) {
      e.printStackTrace();
      serialPort.close();
      return;
    }
    stateMachine = serialDescriptor.createStateMachine(serialPort);
    stateMachine.onDataPacket.connect(sensorDataListener);
  }

  synchronized public byte[] waitForRawData() {
    try {
      wait();
    } catch (InterruptedException e) {
      e.printStackTrace();
      return null;
    }
    return byteBuffer.array();
  }

  @Override
  public synchronized ObservationVersatile waitForData() {
    waitForRawData();
    return Syncs.createObservation(System.currentTimeMillis(), byteBuffer, sensors);
  }

  @Override
  public boolean isClosed() {
    return serialPort == null || serialPort.isClosed();
  }

  public SerialLinkStateMachine stateMachine() {
    return stateMachine;
  }

  public IRobotSerialDescriptor descriptor() {
    return serialDescriptor;
  }

  @Override
  public int packetSize() {
    return sensorDrop.dataSize();
  }
}