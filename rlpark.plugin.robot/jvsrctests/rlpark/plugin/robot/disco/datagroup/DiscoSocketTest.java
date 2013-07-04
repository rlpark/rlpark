package rlpark.plugin.robot.disco.datagroup;

import java.awt.Color;
import java.io.IOException;
import java.util.Arrays;

import org.junit.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import rlpark.plugin.robot.internal.disco.Server;
import rlpark.plugin.robot.internal.disco.datagroup.DropColorGroup;
import rlpark.plugin.robot.internal.disco.datagroup.DropScalarGroup;
import rlpark.plugin.robot.internal.disco.drops.Drop;
import rlpark.plugin.robot.internal.disco.drops.DropByteArray;
import rlpark.plugin.robot.internal.disco.drops.DropData;
import rlpark.plugin.robot.internal.disco.drops.DropFloat;
import rlpark.plugin.robot.internal.disco.io.DiscoPacket;
import rlpark.plugin.robot.internal.disco.io.DiscoSocket;


public class DiscoSocketTest {
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

  private void testCommunication(Drop drop, int... sent) throws IOException {
    testCommunication(drop, sent, drop, sent);
  }

  private void testCommunication(Drop dropSent, int[] sent, final Drop dropRecv, int[] expected) throws IOException {
    DropScalarGroup sendGroup = new DropScalarGroup(dropSent);
    sendGroup.set(sent);
    DropScalarGroup recvGroup = new DropScalarGroup(dropRecv);
    int[] dataReceived = new int[recvGroup.size()];
    socket01.send(dropSent);
    DiscoPacket packet = socket02.recv();
    recvGroup.interpret(packet.byteBuffer(), dataReceived);
    Assert.assertTrue(Arrays.equals(expected, dataReceived));
  }

  private void testCommunication(final Drop drop, Color... sent) throws IOException {
    final DropColorGroup sendGroup = new DropColorGroup(drop);
    sendGroup.set(sent);
    DropColorGroup recvGroup = new DropColorGroup(drop);
    socket01.send(drop);
    DiscoPacket packet = socket02.recv();
    Color[] dataReceived = new Color[recvGroup.size()];
    sendGroup.get(packet.byteBuffer(), dataReceived);
    for (int i = 0; i < dataReceived.length; i++)
      Assert.assertEquals(sent[i], dataReceived[i]);
  }

  private void testCommunication(byte... sent) throws IOException {
    Drop drop = new Drop("NoTime", new DropData[] { new DropByteArray("Bla", 3) });
    ((DropByteArray) drop.dropDatas()[0]).setValue(sent);
    socket01.send(drop);
    DiscoPacket packet = socket02.recv();
    Assert.assertTrue(Arrays.equals(sent, packet.buffer));
  }

  @Test
  public void testServerClientCommunication() throws IOException {
    Drop sendDrop = new Drop("NoTime", DropTest.dropData01);
    testCommunication(sendDrop, 45);
    testCommunication(sendDrop, 60);
  }

  @Test
  public void testServerClientCommunicationWithFloat() throws IOException {
    Drop drop = new Drop("WithFloat", new DropData[] { new DropFloat("Float01") });
    testCommunication(drop, 45);
    testCommunication(drop, 60);
  }

  @Test
  public void testServerClientCommunicationWithFinal() throws IOException {
    Drop sendDrop = new Drop("WithFinal", DropTest.dropData03);
    Drop receivedDrop = new Drop("WithFinal", DropTest.dropData04);
    testCommunication(sendDrop, new int[] { 45, 90 }, receivedDrop, new int[] { 45, 9, 90 });
  }

  @Test
  public void testServerClientCommunicationWithArray() throws IOException {
    testCommunication(new Drop("WithArray", DropTest.dropData05), 4, 5, 6, 7, 8);
  }

  @Test
  public void testServerClientCommunicationWithColor() throws IOException {
    Drop drop = new Drop("NoTime", DropTest.dropData06);
    testCommunication(drop, Color.WHITE);
    testCommunication(drop, Color.RED);
  }

  @Test
  public void testServerClientCommunicationWithByteString() throws IOException {
    testCommunication(new byte[] { 1, 2, 3 });
    testCommunication(new byte[] { 4, 5, 6 });
  }

  // Time not communicated at the moment
  // @Test
  // public void testServerClientCommunicationWithTime() throws IOException {
  // Drop sendDrop = new Drop("WithTime", DropTest.dropData02);
  // DropIntegerGroup sendGroup = new DropIntegerGroup(sendDrop);
  // Drop receivedDrop = new Drop("WithTime", DropTest.dropData02);
  //
  // sendGroup.setWithTime(20, 45);
  // Assert.assertFalse(sendDrop.equals(receivedDrop));
  // socket01.send(sendDrop);
  // socket02.recv(receivedDrop);
  // Assert.assertEquals(sendDrop, receivedDrop);
  //
  // sendGroup.setWithTime(30, 60);
  // Assert.assertFalse(sendDrop.equals(receivedDrop));
  // socket02.send(sendDrop);
  // socket01.recv(receivedDrop);
  // Assert.assertEquals(sendDrop, receivedDrop);
  // }
}
