package rlpark.plugin.irobot.robots;

import rlpark.plugin.irobot.data.CreateAction;
import rlpark.plugin.irobot.data.CreateLeds;
import rlpark.plugin.irobot.internal.descriptors.IRobotObservationReceiver;
import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.observations.Legend;
import rlpark.plugin.robot.helpers.RobotEnvironment;
import rlpark.plugin.robot.helpers.Robots;
import rlpark.plugin.robot.observations.ObservationReceiver;
import zephyr.plugin.core.api.monitoring.abstracts.DataMonitor;
import zephyr.plugin.core.api.monitoring.abstracts.MonitorContainer;
import zephyr.plugin.core.api.monitoring.abstracts.Monitored;

abstract public class IRobotEnvironment extends RobotEnvironment implements MonitorContainer {
  protected final CreateAction lastSent = new CreateAction(0, 0);
  private final IRobotObservationReceiver connection;

  protected IRobotEnvironment(ObservationReceiver receiver, boolean persistent) {
    super(receiver, persistent);
    connection = (IRobotObservationReceiver) receiver();
  }

  @Override
  public Legend legend() {
    return connection.legend();
  }

  public void sendMessage(byte[] bs) {
    connection.sendMessage(bs);
  }

  public void passiveMode() {
    sendMessage(new byte[] { (byte) 128 });
  }

  public void safeMode() {
    sendMessage(new byte[] { (byte) 131 });
  }

  public void fullMode() {
    sendMessage(new byte[] { (byte) 132 });
  }

  public void clean() {
    sendMessage(new byte[] { (byte) 135 });
  }

  public void dock() {
    sendMessage(new byte[] { (byte) 143 });
  }

  public void registerSong(int songNumber, int[] song) {
    byte songLength = (byte) Math.min(song.length / 2, 16);
    byte[] message = new byte[3 + songLength * 2];
    message[0] = (byte) 140;
    message[1] = (byte) songNumber;
    message[2] = songLength;
    for (int i = 0; i < songLength * 2; i++)
      message[3 + i] = (byte) song[i];
    sendMessage(message);
  }

  public void playSong(int songNumber) {
    sendMessage(new byte[] { (byte) 141, (byte) songNumber });
  }

  public void playSong(int[] song) {
    registerSong(0, song);
    playSong(0);
  }

  @Override
  public void addToMonitor(DataMonitor monitor) {
    monitor.add("ActionWheelLeft", new Monitored() {
      @Override
      public double monitoredValue() {
        return lastSent.left();
      }
    });
    monitor.add("ActionWheelRight", new Monitored() {
      @Override
      public double monitoredValue() {
        return lastSent.right();
      }
    });
    Robots.addToMonitor(monitor, this);
  }

  public void sendAction(CreateAction agentAction) {
    sendAction(agentAction.left(), agentAction.right());
  }

  public void sendAction(double left, double right) {
    lastSent.set(left, right);
    sendActionToRobot(left, right);
  }

  abstract protected void sendActionToRobot(double left, double right);

  protected short toActionValue(double maxAction, double value) {
    return (short) Math.min(maxAction, Math.max(-maxAction, value));
  }

  @Override
  public void sendAction(Action a) {
    sendAction((CreateAction) a);
  }

  public void sendLeds(CreateLeds leds) {
    sendLeds((byte) leds.powerColor, (byte) leds.powerIntensity, leds.play, leds.advance);
  }

  abstract public void resetForCharging();

  abstract public void sendLeds(int powerColor, int powerIntensity, boolean play, boolean advance);
}
