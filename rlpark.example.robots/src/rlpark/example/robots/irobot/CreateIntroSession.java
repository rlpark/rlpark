package rlpark.example.robots.irobot;

import rlpark.plugin.irobot.data.CreateAction;
import rlpark.plugin.irobot.data.IRobots;
import rlpark.plugin.irobot.robots.CreateRobot;
import zephyr.plugin.core.api.Zephyr;
import zephyr.plugin.core.api.synchronization.Clock;

public class CreateIntroSession {
  public static void main(String[] args) {
    Clock clock = new Clock("Create");
    CreateRobot robot = new CreateRobot("/dev/cu.ElementSerial-ElementSe");
    Zephyr.advertise(clock, robot);
    int bumpRightObsIndex = robot.legend().indexOf(IRobots.BumpRight);
    int bumpLeftObsIndex = robot.legend().indexOf(IRobots.BumpLeft);
    robot.safeMode();
    while (!robot.isClosed() && clock.tick()) {
      double obs[] = robot.waitNewObs();
      double wheelLeft = obs[bumpRightObsIndex] == 0 ? 150 : -150;
      double wheelRight = obs[bumpLeftObsIndex] == 0 ? 150 : -70;
      robot.sendAction(new CreateAction(wheelLeft, wheelRight));
    }
  }
}
