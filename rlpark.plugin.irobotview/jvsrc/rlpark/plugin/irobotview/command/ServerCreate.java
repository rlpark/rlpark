package rlpark.plugin.irobotview.command;

import rlpark.plugin.irobot.internal.server.CreateServer;
import rlpark.plugin.irobot.internal.server.IRobotServer;


public class ServerCreate extends StartServerCommand {
  @Override
  protected IRobotServer newServer(int port, String serialPortPath) {
    return new CreateServer(port, serialPortPath);
  }
}
