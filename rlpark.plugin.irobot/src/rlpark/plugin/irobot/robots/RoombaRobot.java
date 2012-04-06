package rlpark.plugin.irobot.robots;

import rlpark.plugin.irobot.data.IRobots;
import rlpark.plugin.irobot.data.RoombaLeds;
import rlpark.plugin.irobot.internal.descriptors.DropDescriptors;
import rlpark.plugin.irobot.internal.descriptors.RoombaSerialDescriptor;
import rlpark.plugin.irobot.internal.irobot.IRobotDiscoConnection;
import rlpark.plugin.irobot.internal.irobot.IRobotSerialConnection;
import rlpark.plugin.irobot.internal.server.IRobotDiscoServer;
import rlpark.plugin.rltoys.envio.observations.Legend;
import rlpark.plugin.robot.observations.ObservationReceiver;

public class RoombaRobot extends IRobotEnvironment {
  static public final double MaxAction = 200;

  public RoombaRobot(String serialPortPath) {
    this(new IRobotSerialConnection(serialPortPath, new RoombaSerialDescriptor()));
  }

  public RoombaRobot(String localhost, int port) {
    this(new IRobotDiscoConnection(localhost, port, DropDescriptors.newRoombaSensorDrop()));
  }

  public RoombaRobot() {
    this("localhost", IRobots.DiscoDefaultPort);
  }

  private RoombaRobot(ObservationReceiver receiver) {
    super(receiver, false);
  }

  public void sendLeds(RoombaLeds leds) {
    sendLeds(leds.dirt, (byte) leds.cleanColor, (byte) leds.intensity);
  }

  public void sendLeds(boolean dirt, byte cleanColor, byte intensity) {
    sendMessage(new byte[] { (byte) 139, (byte) (dirt ? 1 : 0), cleanColor, intensity });
  }

  @Override
  public void sendLeds(int powerColor, int powerIntensity, boolean play, boolean advance) {
    sendLeds(new RoombaLeds(true, advance, play, powerColor, powerIntensity));
  }

  @Override
  protected void sendActionToRobot(double left, double right) {
    short shortLeft = toActionValue(MaxAction, left);
    short shortRight = toActionValue(MaxAction, right);
    sendMessage(new byte[] { (byte) 146, (byte) (shortRight >> 8), (byte) shortRight, (byte) (shortLeft >> 8),
        (byte) shortLeft });
  }

  public static void main(String[] args) {
    IRobotDiscoServer.runServer("/dev/cu.FireFly-155A-SPP", new RoombaSerialDescriptor());
  }

  public void filterLastReceivedObs() {
    if (lastReceivedObsBuffer == null)
      return;
    Legend legend = legend();
    final double[] lastObs = lastReceivedObsBuffer.doubleValues();
    double icRight = lastObs[legend.indexOf(IRobots.ICRight)];
    lastObs[legend.indexOf(IRobots.ICOmni)] = icRight;
    if (icRight == 88 || icRight == 89)
      lastObs[legend.indexOf(IRobots.WallVirtual)] = 1.0;
  }

  @Override
  public void resetForCharging() {
    System.out.println("Resetted");
  }
}
