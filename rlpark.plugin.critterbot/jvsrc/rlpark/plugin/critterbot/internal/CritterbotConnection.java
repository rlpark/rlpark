package rlpark.plugin.critterbot.internal;

import java.awt.Color;
import java.io.IOException;
import java.nio.ByteOrder;

import rlpark.plugin.critterbot.actions.CritterbotAction;
import rlpark.plugin.critterbot.data.CritterbotLabels;
import rlpark.plugin.critterbot.data.CritterbotLabels.LedMode;
import rlpark.plugin.robot.internal.disco.DiscoConnection;
import rlpark.plugin.robot.internal.disco.datagroup.DropColorGroup;
import rlpark.plugin.robot.internal.disco.datagroup.DropScalarGroup;
import rlpark.plugin.robot.internal.disco.drops.Drop;
import rlpark.plugin.robot.internal.disco.drops.DropInteger;

public class CritterbotConnection extends DiscoConnection {
  static private double ActionVoltageMax = 25;
  protected final Drop actionDrop = DropDescriptors.newActionDrop();
  protected final DropScalarGroup actions = new DropScalarGroup(CritterbotLabels.VelocityCommand, actionDrop);
  protected final DropInteger actionMode = (DropInteger) actionDrop.drop(CritterbotLabels.MotorMode);
  protected final DropInteger ledMode = (DropInteger) actionDrop.drop(CritterbotLabels.LedMode);
  private final DropColorGroup leds = new DropColorGroup(actionDrop);

  public CritterbotConnection(String hostname, int port) {
    super(hostname, port, DropDescriptors.newObservationDrop(), ByteOrder.LITTLE_ENDIAN);
  }

  public long lastObservationDropTime() {
    return sensorDrop.time();
  }

  public void sendActionDrop(CritterbotAction action, LedMode ledModeValue, Color[] ledValues) {
    if (action.actions == null || isClosed())
      return;
    actionMode.setDouble(action.motorMode.ordinal());
    if (action.motorMode != CritterbotAction.MotorMode.WHEEL_SPACE)
      actions.set(action.actions);
    else {
      double[] actionValues = action.actions.clone();
      for (int i = 0; i < actionValues.length; i++)
        actionValues[i] = Math.signum(actionValues[i]) * Math.min(ActionVoltageMax, Math.abs(actionValues[i]));
      actions.set(actionValues);
    }
    actionMode.setDouble(action.motorMode.ordinal());
    ledMode.setDouble(ledModeValue.ordinal());
    leds.set(ledValues);
    try {
      socket.send(actionDrop);
    } catch (IOException e) {
      e.printStackTrace();
      close();
    }
  }
}
