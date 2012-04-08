package zephyr.plugin.critterview.runnable;

import rlpark.plugin.critterbot.environment.CritterbotRobot;
import zephyr.plugin.core.api.Zephyr;
import zephyr.plugin.core.api.synchronization.Clock;

public class CritterbotRunnable implements Runnable {
  @Override
  public void run() {
    CritterbotRobot robot = new CritterbotRobot();
    Clock clock = new Clock("Critterbot");
    Zephyr.advertise(clock, robot);
    while (!robot.isClosed() && clock.tick())
      robot.waitNewObs();
    robot.close();
  }
}