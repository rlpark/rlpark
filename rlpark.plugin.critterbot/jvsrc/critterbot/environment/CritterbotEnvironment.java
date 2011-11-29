package critterbot.environment;

import java.awt.Color;

import rlpark.plugin.robot.RobotEnvironment;
import rlpark.plugin.robot.sync.ObservationReceiver;
import rltoys.algorithms.representations.actions.Action;
import rltoys.environments.envio.Agent;
import rltoys.environments.envio.observations.Legend;
import rltoys.environments.envio.observations.ObsFilter;
import zephyr.plugin.core.api.monitoring.abstracts.DataMonitor;
import zephyr.plugin.core.api.monitoring.abstracts.MonitorContainer;
import zephyr.plugin.core.api.synchronization.Clock;
import critterbot.CritterbotObservation;
import critterbot.CritterbotProblem;
import critterbot.actions.CritterbotAction;
import critterbot.environment.CritterbotDrops.LedMode;

public class CritterbotEnvironment extends RobotEnvironment implements CritterbotProblem, MonitorContainer {
  protected CritterbotAction agentAction;
  private LedMode ledMode = LedMode.BUSY;
  private final Color[] ledValues = new Color[CritterbotDrops.NbLeds];
  protected final CritterbotConnection critterbotConnection;

  protected CritterbotEnvironment(ObservationReceiver receiver) {
    super(receiver, false);
    critterbotConnection = (CritterbotConnection) receiver();
  }

  @Override
  public Legend legend() {
    return critterbotConnection.legend();
  }

  /**
   * Do not use this method, use your own main loop instead using sendAction(),
   * setLed() waitNewObs() and lastReceivedObs()
   * 
   * @see rlpark.plugin.robot.RobotEnvironment#run(zephyr.plugin.core.api.synchronization
   *      .Clock, rltoys.environments.envio.Agent)
   */
  @Deprecated
  public void run(Agent agent) {
    run(new Clock("CritterbotEnvironment"), agent);
  }

  /**
   * Do not use this method, use your own main loop instead using sendAction(),
   * setLed() waitNewObs() and lastReceivedObs()
   * 
   * @see rlpark.plugin.robot.RobotEnvironment#run(zephyr.plugin.core.api.synchronization
   *      .Clock, rltoys.environments.envio.Agent)
   */
  @Deprecated
  @Override
  public void run(Clock clock, Agent agent) {
    double[] obsArray = waitNewObs();
    while (!isClosed() && !clock.isTerminated()) {
      clock.tick();
      CritterbotAction action = (CritterbotAction) agent.getAtp1(obsArray);
      sendAction(action);
      if (action == null)
        break;
      obsArray = waitNewObs();
    }
  }

  public void sendAction(CritterbotAction action) {
    agentAction = action;
    if (action != null)
      critterbotConnection.sendActionDrop(action, ledMode, ledValues);
    ledMode = LedMode.CLEAR;
  }

  public void setLed(Color[] colors) {
    setLedMode(LedMode.CUSTOM);
    System.arraycopy(colors, 0, ledValues, 0, ledValues.length);
  }

  public void setLedMode(LedMode ledMode) {
    this.ledMode = ledMode;
  }

  public CritterbotObservation getCritterbotObservation(double[] obs) {
    return getCritterbotObservation(System.currentTimeMillis(), obs);
  }

  public CritterbotObservation getCritterbotObservation(long time, double[] obs) {
    return new CritterbotObservation(legend(), time, obs);
  }

  public ObsFilter getDefaultFilter() {
    return CritterbotDrops.newDefaultFilter(legend());
  }

  @Override
  public void addToMonitor(DataMonitor monitor) {
    CritterbotEnvironments.addObservationsLogged(this, monitor);
    CritterbotEnvironments.addActionsLogged(this, monitor);
  }

  @Override
  public void sendAction(Action a) {
    sendAction((CritterbotAction) a);
  }

  @Override
  public CritterbotAction lastAction() {
    return agentAction;
  }
}
