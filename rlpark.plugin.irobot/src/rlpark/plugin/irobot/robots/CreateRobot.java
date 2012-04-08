package rlpark.plugin.irobot.robots;

import rlpark.plugin.irobot.data.IRobotLabels;
import rlpark.plugin.irobot.internal.descriptors.CreateSerialDescriptor;
import rlpark.plugin.irobot.internal.descriptors.DropDescriptors;
import rlpark.plugin.irobot.internal.irobot.IRobotDiscoConnection;
import rlpark.plugin.irobot.internal.irobot.IRobotSerialConnection;
import rlpark.plugin.irobot.internal.server.IRobotDiscoServer;
import rlpark.plugin.robot.observations.ObservationReceiver;

public class CreateRobot extends IRobotEnvironment {
  static public final double MaxAction = 500;
  static public final String SerialLinkFile = "/dev/cu.ElementSerial-ElementSe";

  public CreateRobot() {
    this(false);
  }

  public CreateRobot(boolean persistent) {
    this("localhost", IRobotLabels.DiscoDefaultPort, persistent);
  }

  public CreateRobot(String serialPortPath) {
    this(new IRobotSerialConnection(serialPortPath, new CreateSerialDescriptor()), false);
  }

  public CreateRobot(String localhost, int port, boolean persitent) {
    this(new IRobotDiscoConnection(localhost, port, DropDescriptors.newCreateSensorDrop()), persitent);
  }

  private CreateRobot(ObservationReceiver receiver, boolean persistent) {
    super(receiver, persistent);
  }

  @Override
  public void sendLeds(int powerColor, int powerIntensity, boolean play, boolean advance) {
    byte ledBits = 0;
    if (play)
      ledBits |= 2;
    if (advance)
      ledBits |= 8;
    sendMessage(new byte[] { (byte) 139, ledBits, (byte) powerColor, (byte) powerIntensity });
  }

  @Override
  protected void sendActionToRobot(double left, double right) {
    short shortLeft = toActionValue(MaxAction, left);
    short shortRight = toActionValue(MaxAction, right);
    sendMessage(new byte[] { (byte) 145, (byte) (shortRight >> 8), (byte) shortRight, (byte) (shortLeft >> 8),
        (byte) shortLeft });
  }

  @Override
  public void resetForCharging() {
    sendMessage(new byte[] { 7 });
  }

  public static void main(String[] args) {
    String serialLinkFile = args.length == 0 ? SerialLinkFile : args[0];
    System.out.println("Opening " + serialLinkFile + "...");
    IRobotDiscoServer.runServer(serialLinkFile, new CreateSerialDescriptor());
  }
}
