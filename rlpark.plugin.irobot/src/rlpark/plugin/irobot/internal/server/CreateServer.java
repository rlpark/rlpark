package rlpark.plugin.irobot.internal.server;

import rlpark.plugin.irobot.internal.descriptors.CreateSerialDescriptor;

public class CreateServer extends IRobotServer {
  public CreateServer(int port, String serialPortPath) {
    super(port, serialPortPath, new CreateSerialDescriptor());
  }
}
