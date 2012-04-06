package rlpark.plugin.irobot.internal.statemachine;

import gnu.io.SerialPortEvent;

import java.util.ArrayList;
import java.util.List;

import rlpark.plugin.irobot.internal.serial.SerialPortToRobot;
import rlpark.plugin.rltoys.utils.Utils;
import rlpark.plugin.robot.internal.statemachine.StateMachine;
import zephyr.plugin.core.api.signals.Listener;
import zephyr.plugin.core.api.signals.Signal;

public class SerialLinkStateMachine {
  public final SerialPortToRobot serialPort;
  private final StateMachine<Byte> stateMachine;
  public Signal<byte[]> onDataPacket = new Signal<byte[]>();
  private final Listener<SerialPortToRobot> serialListener = new Listener<SerialPortToRobot>() {
    @Override
    public void listen(SerialPortToRobot eventInfo) {
      readAvailableData();
    }
  };
  private final Listener<StateMachine<Byte>> stateMachineListener = new Listener<StateMachine<Byte>>() {
    @Override
    public void listen(StateMachine<Byte> stateMachine) {
      dispatchReceivedDataIfCorrect();
    }
  };
  private final List<DataNode> dataNodes;
  private final byte[] data;
  private final ChecksumNode checksumNode;

  public SerialLinkStateMachine(SerialPortToRobot serialPort, List<SerialLinkNode> nodes) {
    this(serialPort, null, nodes);
  }

  public SerialLinkStateMachine(SerialPortToRobot serialPort, ChecksumNode checksumNode, SerialLinkNode... nodes) {
    this(serialPort, checksumNode, Utils.asList(nodes));
  }

  public SerialLinkStateMachine(SerialPortToRobot serialPort, ChecksumNode checksumNode, List<SerialLinkNode> nodes) {
    this.serialPort = serialPort;
    dataNodes = createDataNodeList(nodes);
    data = createData(dataNodes);
    List<SerialLinkNode> stateMachineNodes = new ArrayList<SerialLinkNode>(nodes);
    if (checksumNode != null)
      stateMachineNodes.add(checksumNode);
    stateMachine = new StateMachine<Byte>(stateMachineNodes);
    stateMachine.onEnd.connect(stateMachineListener);
    this.checksumNode = checksumNode;
    serialPort.register(SerialPortEvent.DATA_AVAILABLE, serialListener);
  }

  private byte[] createData(List<DataNode> dataNodes) {
    int dataLength = 0;
    for (DataNode node : dataNodes)
      dataLength += node.length();
    return new byte[dataLength];
  }

  private List<DataNode> createDataNodeList(List<SerialLinkNode> nodes) {
    List<DataNode> dataNodes = new ArrayList<DataNode>();
    for (SerialLinkNode node : nodes)
      if (node instanceof DataNode)
        dataNodes.add((DataNode) node);
    return dataNodes;
  }

  protected void dispatchReceivedDataIfCorrect() {
    if (checksumNode == null || checksumNode.isPacketValid())
      onDataPacket.fire(fillDataPacket());
  }

  private byte[] fillDataPacket() {
    int index = 0;
    for (DataNode node : dataNodes) {
      System.arraycopy(node.data, 0, data, index, node.data.length);
      index += node.data.length;
    }
    return data;
  }

  public void readAvailableData() {
    byte[] available = serialPort.getAvailable();
    for (byte b : available)
      stateMachine.step(b);
  }
}
