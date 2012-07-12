package rlpark.plugin.irobot.internal.serial;

import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TooManyListenersException;

import rlpark.plugin.irobot.internal.serial.SerialListeners.AlwaysWakeUpThread;
import rlpark.plugin.irobot.internal.serial.SerialListeners.ReadWhenArriveAndWakeUp;
import rlpark.plugin.irobot.internal.serial.SerialListeners.SerialInputCondition;
import rlpark.plugin.irobot.internal.serial.SerialListeners.WakeUpThread;
import rlpark.plugin.robot.internal.disco.drops.DropByteArray;
import zephyr.plugin.core.api.signals.Listener;
import zephyr.plugin.core.api.signals.Signal;

public class SerialPortToRobot implements SerialPortEventListener {
  static public final boolean ExpectedIgnored = false;
  static public final boolean DebugSignals = false;
  public final Signal<SerialPortToRobot> onClosed = new Signal<SerialPortToRobot>();

  public static class SerialPortInfo {
    public int rate = 115200;
    public int databits = SerialPort.DATABITS_8;
    public int stopbits = SerialPort.STOPBITS_1;
    public int parity = SerialPort.PARITY_NONE;
    public int flowControl = SerialPort.FLOWCONTROL_NONE;

    public SerialPortInfo() {
      this(115200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE, SerialPort.FLOWCONTROL_NONE);
    }

    public SerialPortInfo(int rate, int databits, int stopbits, int parity, int flowControl) {
      this.rate = rate;
      this.databits = databits;
      this.stopbits = stopbits;
      this.parity = parity;
      this.flowControl = flowControl;
    }
  }

  private final String serialPortFileName;
  private final CommPortIdentifier identifier;
  private final SerialPort serialPort;
  private List<Byte> buffer = new ArrayList<Byte>();
  private final Map<Integer, Signal<SerialPortToRobot>> signals = new HashMap<Integer, Signal<SerialPortToRobot>>();
  final SerialStreams serialStreams;
  private boolean isClosed;

  public SerialPortToRobot(String fileName, SerialPortInfo portInfo) throws PortInUseException,
      UnsupportedCommOperationException, TooManyListenersException, IOException {
    serialPortFileName = fileName;
    identifier = SerialPorts.getPortIdentifier(serialPortFileName);
    if (identifier == null)
      throw new RuntimeException("Port identifier " + serialPortFileName + " not found");
    serialPort = (SerialPort) identifier.open("RLPark", 2000);
    serialPort.addEventListener(this);
    serialPort.setFlowControlMode(portInfo.flowControl);
    serialPort.setSerialPortParams(portInfo.rate, portInfo.databits, portInfo.stopbits, portInfo.parity);
    serialStreams = new SerialStreams(serialPort);
    setNotifiers();
  }

  public void wakeupRobot() {
    synchronized (this) {
      serialPort.setRTS(false);
      serialPort.setDTR(false);
    }
    try {
      Thread.sleep(500);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    synchronized (this) {
      serialPort.setRTS(true);
      serialPort.setDTR(true);
    }
  }

  private void setNotifiers() {
    serialPort.notifyOnDataAvailable(true);
    serialPort.notifyOnOutputEmpty(true);
    serialPort.notifyOnBreakInterrupt(true);
    serialPort.notifyOnCarrierDetect(true);
    serialPort.notifyOnCTS(true);
    serialPort.notifyOnDSR(true);
    serialPort.notifyOnFramingError(true);
    serialPort.notifyOnOverrunError(true);
    serialPort.notifyOnParityError(true);
    serialPort.notifyOnRingIndicator(true);
  }

  public void register(int event, Listener<SerialPortToRobot> listener) {
    Signal<SerialPortToRobot> signal = signals.get(event);
    if (signal == null) {
      signal = new Signal<SerialPortToRobot>();
      signals.put(event, signal);
    }
    signal.connect(listener);
  }

  public void unregister(int event, Listener<SerialPortToRobot> listener) {
    signals.get(event).disconnect(listener);
  }

  @Override
  synchronized public void serialEvent(SerialPortEvent event) {
    if (event.getEventType() == SerialPortEvent.DATA_AVAILABLE)
      updateAvailable();
    Signal<SerialPortToRobot> signal = signals.get(event.getEventType());
    if (signal != null)
      signal.fire(this);
    if (!DebugSignals)
      return;
    switch (event.getEventType()) {
    case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
      System.out.println("Event received: outputBufferEmpty");
      break;

    case SerialPortEvent.DATA_AVAILABLE:
      System.out.println("Event received: dataAvailable");
      break;

    case SerialPortEvent.BI:
      System.out.println("Event received: breakInterrupt");
      break;

    case SerialPortEvent.CD:
      System.out.println("Event received: carrierDetect");
      break;

    case SerialPortEvent.CTS:
      System.out.println("Event received: clearToSend");
      break;

    case SerialPortEvent.DSR:
      System.out.println("Event received: dataSetReady");
      break;

    case SerialPortEvent.FE:
      System.out.println("Event received: framingError");
      break;

    case SerialPortEvent.OE:
      System.out.println("Event received: overrunError");
      break;

    case SerialPortEvent.PE:
      System.out.println("Event received: parityError");
      break;
    case SerialPortEvent.RI:
      System.out.println("Event received: ringIndicator");
      break;
    default:
      System.out.println("Event received: unknown");
    }
  }

  public void send(char[] chars) throws IOException {
    send(DropByteArray.toBytes(chars));
  }

  synchronized public void send(byte[] bytes) throws IOException {
    for (byte b : bytes)
      serialStreams.write(b);
  }

  public void send(String command) throws IOException {
    send(command.getBytes());
  }

  synchronized public void sendAndExpect(String command, final String returnExpected) throws IOException {
    ReadWhenArriveAndWakeUp listener = new ReadWhenArriveAndWakeUp();
    register(SerialPortEvent.DATA_AVAILABLE, listener);
    send(command);
    waitForSignal();
    unregister(SerialPortEvent.DATA_AVAILABLE, listener);
    if (!ExpectedIgnored && !returnExpected.equals(listener.message()))
      throw new IOException(String.format("Return incorrect: expected <%s> was <%s>", returnExpected,
                                          listener.message()));
  }

  private void updateAvailable() {
    buffer = new ArrayList<Byte>();
    try {
      while (serialStreams.available() > 0)
        buffer.add((byte) serialStreams.read());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void close() {
    // Produce a SEG FAULT
    // serialStreams.close();
    if (isClosed)
      return;
    isClosed = true;
    onClosed.fire(this);
  }

  public void sendAndWait(char[] chars) throws IOException {
    sendAndWait(DropByteArray.toBytes(chars));
  }

  synchronized public void sendAndWait(byte[] chars) throws IOException {
    AlwaysWakeUpThread listener = new AlwaysWakeUpThread();
    register(SerialPortEvent.OUTPUT_BUFFER_EMPTY, listener);
    send(chars);
    waitForSignal();
    unregister(SerialPortEvent.OUTPUT_BUFFER_EMPTY, listener);
  }

  private void waitForSignal() {
    try {
      wait(10000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public int sendAndWaitForData(char[] chars, final int dataSizeToWaitFor) throws IOException {
    return sendAndWaitForData(DropByteArray.toBytes(chars), dataSizeToWaitFor);
  }

  synchronized public int sendAndWaitForData(byte[] bytes, final int dataSizeToWaitFor) throws IOException {
    WakeUpThread listener = new WakeUpThread(new SerialInputCondition() {
      private int remainingData = dataSizeToWaitFor;

      @Override
      public boolean isSatisfied(SerialPortToRobot serialPort) {
        remainingData -= available();
        return remainingData <= 0;
      }
    });
    register(SerialPortEvent.DATA_AVAILABLE, listener);
    send(bytes);
    waitForSignal();
    unregister(SerialPortEvent.DATA_AVAILABLE, listener);
    return listener.nbDataAvailable();
  }

  public String getAvailableAsString() {
    StringBuilder result = new StringBuilder();
    for (Byte b : buffer)
      result.append((char) (byte) b);
    return result.toString();
  }

  public byte[] getAvailable() {
    byte[] result = new byte[buffer.size()];
    for (int i = 0; i < result.length; i++)
      result[i] = buffer.get(i);
    return result;
  }

  public int available() {
    return buffer.size();
  }

  public boolean isClosed() {
    return isClosed;
  }

  static public SerialPortToRobot openPort(String serialPortFile, SerialPortInfo serialPortInfo) {
    SerialPorts.refreshPortIdentifiers();
    SerialPortToRobot serialPort = null;
    try {
      serialPort = new SerialPortToRobot(serialPortFile, serialPortInfo);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return serialPort;
  }

  static public void fatalError(String message) {
    System.err.println(message);
    System.exit(1);
  }
}
