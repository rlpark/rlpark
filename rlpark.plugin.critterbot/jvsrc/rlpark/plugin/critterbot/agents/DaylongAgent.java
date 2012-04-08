package rlpark.plugin.critterbot.agents;

import java.awt.Color;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import rlpark.plugin.critterbot.actions.CritterbotAction;
import rlpark.plugin.critterbot.actions.XYThetaAction;
import rlpark.plugin.critterbot.data.CritterbotObservation;
import rlpark.plugin.critterbot.environment.CritterbotEnvironment;
import rlpark.plugin.critterbot.environment.CritterbotRobot;
import rlpark.plugin.rltoys.agents.Agent;
import rlpark.plugin.rltoys.agents.RandomAgent;
import zephyr.plugin.core.api.internal.monitoring.fileloggers.TimedFileLogger;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;
import zephyr.plugin.core.api.synchronization.Clock;

@SuppressWarnings("restriction")
public class DaylongAgent {
  public static interface DaylongSlaveAgent {
    void setInControl(boolean inControl);
  }

  private final double chargingVoltage;
  @Monitor(emptyLabel = true)
  private final CritterbotEnvironment environment;
  private final DockingAgent dockingAgent;
  private CritterbotObservation critterObs;
  private long timeVoltageAbove = -1;
  private long timeDocked = -1;
  @Monitor
  private final Agent agent;
  private final TimedFileLogger logFile;
  @Monitor
  private boolean daylongInControl;

  public DaylongAgent(String filepath, CritterbotEnvironment environment, Agent agent, double chargingVoltage)
      throws IOException {
    this.environment = environment;
    this.chargingVoltage = chargingVoltage;
    dockingAgent = new DockingAgent(environment);
    this.agent = agent;
    logFile = new TimedFileLogger(filepath, false);
    logFile.add(this);
    System.out.println("Critterbot will charge at " + chargingVoltage);
  }

  private void updateCharging() {
    double voltageMax = Math.max(critterObs.bat[0], Math.max(critterObs.bat[1], critterObs.bat[2]));
    if (timeVoltageAbove < 0 || voltageMax >= chargingVoltage || isDocked())
      timeVoltageAbove = critterObs.time;
    if (!isDocked())
      timeDocked = critterObs.time;
  }

  private boolean isDocked() {
    return critterObs.busVoltage > 170;
  }

  private boolean needsCharging() {
    return critterObs.time - timeVoltageAbove > 10000;
  }

  private Color pickupColor() {
    if (needsCharging())
      return Color.BLUE;
    if (isDockedAndWait())
      return Color.WHITE;
    if (isDocked())
      return Color.GREEN;
    return Color.BLACK;
  }

  public void setLeds(Color[] colors) {
    if (critterObs == null)
      return;
    Arrays.fill(colors, pickupColor());
    colors[0] = Color.RED;
    colors[colors.length / 2] = Color.MAGENTA;
  }

  public void run() {
    Clock clock = new Clock("Daylong");
    while (clock.tick() && !environment.isClosed())
      environment.sendAction(getAtp1(environment.waitNewObs()));
  }

  public CritterbotAction getAtp1(double[] envObs) {
    critterObs = environment.getCritterbotObservation(envObs);
    updateCharging();
    daylongInControl = needsCharging() || isDockedAndWait();
    if (agent instanceof DaylongSlaveAgent)
      ((DaylongSlaveAgent) agent).setInControl(!daylongInControl);
    CritterbotAction action = getAction(envObs);
    logFile.update();
    return action;
  }

  public CritterbotAction getAction(double[] envObs) {
    if (daylongInControl)
      return dockingAgent.getAtp1(envObs);
    return (CritterbotAction) agent.getAtp1(envObs);
  }

  private boolean isDockedAndWait() {
    return isDocked() && critterObs.time - timeDocked < 10000;
  }

  public static void main(String[] args) throws IOException {
    CritterbotEnvironment environment = new CritterbotRobot();
    RandomAgent funAgent = new RandomAgent(new Random(0), XYThetaAction.sevenActions());
    DaylongAgent daylongAgent = new DaylongAgent("/tmp/daylong.crtrlog", environment, funAgent, 160);
    daylongAgent.run();
  }
}
