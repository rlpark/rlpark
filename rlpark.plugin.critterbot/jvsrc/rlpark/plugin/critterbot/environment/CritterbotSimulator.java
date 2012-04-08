package rlpark.plugin.critterbot.environment;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

import org.rlcommunity.critterbot.simulator.SimulatorMain;

import rlpark.plugin.critterbot.data.CritterbotLabels;
import rlpark.plugin.critterbot.internal.CritterbotConnection;
import rlpark.plugin.rltoys.envio.observations.ObsFilter;
import rlpark.plugin.rltoys.utils.Command;

public class CritterbotSimulator extends CritterbotEnvironment {
  static public class SimulatorCommand {
    final public int port;
    final public Command command;

    public SimulatorCommand(Command command, int port) {
      this.port = port;
      this.command = command;
    }
  }

  static boolean remoteDebugging = false;
  private static String jarPath = null;
  static final String[] remoteDebugingArgs = { "-Xdebug",
      "-Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=1044" };
  private SimulatorCommand command;

  public CritterbotSimulator(SimulatorCommand command) {
    super(new CritterbotConnection("localhost", command.port));
    this.command = command;
  }

  static public SimulatorCommand startSimulator() {
    if (jarPath == null)
      setJarPath(getDefaultSimulatorJarPath());
    int port = findFreePort();
    String[] commandLine = buildSimulatorCommandLine(port);
    System.out.println("Running: " + toCommandLineString(commandLine));
    Command command = new Command(CritterbotSimulator.class.getSimpleName(), commandLine);
    try {
      command.start();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return new SimulatorCommand(command, port);
  }

  private static int findFreePort() {
    int port = 3284;
    ServerSocket socket;
    try {
      socket = new ServerSocket(0);
      port = socket.getLocalPort();
      socket.close();
    } catch (IOException e) {
      e.printStackTrace();
      System.err.println("Problem to find a free port: using port by default");
    }
    return port;
  }

  static protected String[] buildSimulatorCommandLine(int port) {
    String absJarPath = new File(jarPath).getAbsolutePath();
    List<String> commandLine = new ArrayList<String>();
    commandLine.add("java");
    if (remoteDebugging)
      for (String parameter : remoteDebugingArgs)
        commandLine.add(parameter);
    commandLine.add("-jar");
    commandLine.add(absJarPath);
    String[] simulatorParameters = { "-p", String.valueOf(port), "-nv" };
    for (String parameter : simulatorParameters)
      commandLine.add(parameter);
    String[] result = new String[commandLine.size()];
    commandLine.toArray(result);
    return result;
  }

  static private String toCommandLineString(String[] commandLine) {
    StringBuilder result = new StringBuilder();
    for (String arg : commandLine) {
      result.append(arg);
      result.append(" ");
    }
    return result.substring(0, result.length() - 1);
  }

  static private String getDefaultSimulatorJarPath() {
    String filePath = SimulatorMain.class.getResource(SimulatorMain.class.getSimpleName() + ".class").getFile();
    int classIndex = filePath.indexOf("/" + SimulatorMain.class.getCanonicalName().replace('.', '/'));
    String pathToFile = filePath.substring(filePath.indexOf('/'), classIndex);
    if (pathToFile.endsWith("!"))
      pathToFile = filePath.substring(0, filePath.length() - 1);
    return pathToFile;
  }

  public static void setJarPath(String jarPath) {
    CritterbotSimulator.jarPath = jarPath;
  }

  @Override
  public ObsFilter getDefaultFilter() {
    return new ObsFilter(legend(), CritterbotLabels.Motor, CritterbotLabels.RotationVel, CritterbotLabels.IRDistance,
                         CritterbotLabels.Light, CritterbotLabels.Accel);
  }

  @Override
  public void close() {
    super.close();
    if (command != null) {
      command.command.kill();
      command = null;
    }
  }
}
