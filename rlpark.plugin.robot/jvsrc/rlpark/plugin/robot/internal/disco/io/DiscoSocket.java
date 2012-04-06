package rlpark.plugin.robot.internal.disco.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteOrder;
import java.util.Arrays;

import rlpark.plugin.robot.internal.disco.drops.Drop;
import rlpark.plugin.robot.internal.disco.drops.DropString;
import rlpark.plugin.robot.internal.disco.io.DiscoPacket.Direction;
import rlpark.plugin.robot.internal.sync.LiteByteBuffer;
import zephyr.plugin.core.api.signals.Signal;
import zephyr.plugin.core.api.synchronization.Chrono;

public class DiscoSocket {
  static final boolean Verbose = false;
  static final float TIMEOUT = 30;
  private final ByteOrder byteOrder;
  protected Socket socket;
  private final DataInputStream in;
  private final DataOutputStream out;
  final public Signal<DiscoPacket> onPacket = new Signal<DiscoPacket>();
  public double readLatency;
  private final LiteByteBuffer sizeBuffer;

  public DiscoSocket(int port) throws UnknownHostException, IOException {
    this("localhost", port);
  }

  public DiscoSocket(String host, int serverPort) throws UnknownHostException, IOException {
    this(host, serverPort, ByteOrder.BIG_ENDIAN);
  }

  public DiscoSocket(String host, int serverPort, ByteOrder byteOrder) throws UnknownHostException, IOException {
    this(createSocket(host, serverPort), byteOrder);
  }

  static private void log(String message) {
    if (Verbose)
      System.out.print(message);
    System.out.flush();
  }

  static private Socket createSocket(String host, int serverPort) throws UnknownHostException, IOException {
    Chrono chrono = new Chrono();
    Socket socket = null;
    log(String.format("Connecting to %s:%d", host, serverPort));
    while (socket == null)
      try {
        socket = new Socket(host, serverPort);
      } catch (java.net.ConnectException e) {
        if (chrono.getCurrentChrono() > TIMEOUT)
          throw e;
        try {
          Thread.sleep(1000);
          log(".");
        } catch (InterruptedException ie) {
          throw new RuntimeException(e);
        }
      }
    log(" ok\n");
    return socket;
  }

  public DiscoSocket(Socket socket, ByteOrder byteOrder) throws IOException {
    this.socket = socket;
    in = new DataInputStream(socket.getInputStream());
    out = new DataOutputStream(socket.getOutputStream());
    this.byteOrder = byteOrder;
    sizeBuffer = allocate(4);
  }

  private LiteByteBuffer allocate(int capacity) {
    return new LiteByteBuffer(capacity, byteOrder);
  }

  synchronized public void send(Drop sendDrop) throws IOException {
    LiteByteBuffer buffer = allocate(sendDrop.packetSize());
    sendDrop.putData(buffer);
    byte[] byteArray = Arrays.copyOfRange(buffer.array(), sendDrop.headerSize(), buffer.array().length);
    onPacket.fire(new DiscoPacket(Direction.Send, sendDrop.name(), buffer.order(), byteArray));
    out.write(buffer.array(), 0, buffer.capacity());
  }

  private String readName() throws IOException {
    int stringSize = readSize();
    if (stringSize > 100 || stringSize <= 0)
      throw new RuntimeException("Name error: length is not > 0 && < 100");
    LiteByteBuffer stringBuffer = allocate(stringSize);
    in.readFully(stringBuffer.array(), 0, stringSize);
    return DropString.getData(stringBuffer, 0);
  }

  protected int readSize() throws IOException {
    sizeBuffer.clear();
    in.readFully(sizeBuffer.array());
    return sizeBuffer.getInt();
  }

  synchronized public void close() {
    try {
      socket.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public DiscoPacket recv() throws IOException {
    String name = readName();
    LiteByteBuffer buffer = allocate(readSize());
    in.readFully(buffer.array(), 0, buffer.capacity());
    DiscoPacket packet = new DiscoPacket(Direction.Recv, name, buffer);
    onPacket.fire(packet);
    return packet;
  }

  public int dataAvailable() throws IOException {
    return in.available();
  }

  public boolean isSocketClosed() {
    return socket == null || socket.isClosed() || !socket.isConnected() || !socket.isBound();
  }
}
