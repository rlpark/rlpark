package rlpark.plugin.irobotview.command;

import rlpark.plugin.irobot.robots.CreateRobot;
import rlpark.plugin.irobotview.runnable.CreateRunnable;
import zephyr.plugin.core.RunnableFactory;
import zephyr.plugin.core.ZephyrCore;

public class ConnectCreate extends EnvironmentSerialPortCommand {
  @Override
  protected void startRunnable(final String serialPortPath) {
    ZephyrCore.start(new RunnableFactory() {
      @Override
      public Runnable createRunnable() {
        return new CreateRunnable(new CreateRobot(serialPortPath));
      }
    });
  }
}
