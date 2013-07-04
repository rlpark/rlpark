package rlpark.plugin.robot;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import rlpark.plugin.robot.internal.disco.DiscoLogger;
import rlpark.plugin.robot.internal.disco.Server;
import rlpark.plugin.robot.internal.disco.drops.Drop;
import rlpark.plugin.robot.internal.disco.drops.DropByteArray;
import rlpark.plugin.robot.internal.disco.drops.DropData;
import rlpark.plugin.robot.internal.disco.io.DiscoLogfile;
import rlpark.plugin.robot.internal.disco.io.DiscoPacket;
import rlpark.plugin.robot.internal.disco.io.DiscoSocket;
import rlpark.plugin.robot.internal.disco.io.DiscoPacket.Direction;

public class DiscoLogfileTest {
  private static final String DropName = "ThisAName";
  private static final String JUnitLogFileName = ".junitdiscofile.bin";
  private Server server;
  private DiscoSocket socket02;
  private DiscoSocket socket01 = null;

  @Before
  public void setUp() throws IOException {
    server = new Server();
    socket02 = new DiscoSocket(server.port);
    socket01 = server.accept();
  }

  @After
  public void setDown() throws IOException {
    socket01.close();
    socket02.close();
    server.close();
  }

  private void testCommunication(byte... sent) throws IOException {
    Drop drop = new Drop(DropName, new DropData[] { new DropByteArray("Bla", 3) });
    ((DropByteArray) drop.dropDatas()[0]).setValue(sent);
    socket01.send(drop);
    DiscoPacket packet = socket02.recv();
    Assert.assertTrue(Arrays.equals(sent, packet.buffer));
  }

  @Test
  public void testDiscoLogfile() throws IOException {
    DiscoLogger discoLogger = new DiscoLogger(JUnitLogFileName, false);
    discoLogger.connectTo(socket01);
    discoLogger.connectTo(socket02);
    byte[] data = new byte[] { 1, 2, 3 };
    testCommunication(data);
    discoLogger.close();
    DiscoLogfile logfile = new DiscoLogfile(JUnitLogFileName);
    Assert.assertTrue(logfile.hasNext());
    DiscoPacket packet = logfile.next();
    Assert.assertEquals(DropName, packet.name);
    Assert.assertTrue(Arrays.equals(data, packet.buffer));
    Assert.assertEquals(Direction.Send, packet.direction);
    packet = logfile.next();
    Assert.assertEquals(DropName, packet.name);
    Assert.assertTrue(Arrays.equals(data, packet.buffer));
    Assert.assertEquals(Direction.Recv, packet.direction);
    Assert.assertFalse(logfile.hasNext());
  }
}
