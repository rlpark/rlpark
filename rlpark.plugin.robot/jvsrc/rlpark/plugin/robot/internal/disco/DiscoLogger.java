package rlpark.plugin.robot.internal.disco;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import rlpark.plugin.robot.internal.disco.io.DiscoPacket;
import rlpark.plugin.robot.internal.disco.io.DiscoSocket;
import zephyr.plugin.core.api.signals.Listener;

public class DiscoLogger {
  public static final String Extension = "discobin";
  private final Listener<DiscoPacket> packetListener = new Listener<DiscoPacket>() {
    @Override
    public void listen(DiscoPacket eventInfo) {
      writePacket(eventInfo);
    }
  };
  private ObjectOutputStream objout;

  public DiscoLogger(String name, boolean append) throws IOException {
    objout = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(name, append)));
  }

  public void connectTo(DiscoSocket socket) {
    socket.onPacket.connect(packetListener);
  }

  public void connectTo(DiscoConnection connection) {
    connection.addPacketListener(packetListener);
  }

  synchronized protected void writePacket(DiscoPacket packet) {
    if (objout == null)
      return;
    try {
      objout.writeObject(packet);
      objout.flush();
      objout.reset();
    } catch (IOException e) {
      e.printStackTrace();
      close();
    }
  }

  synchronized public void close() {
    if (objout == null)
      return;
    try {
      objout.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    objout = null;
  }
}
