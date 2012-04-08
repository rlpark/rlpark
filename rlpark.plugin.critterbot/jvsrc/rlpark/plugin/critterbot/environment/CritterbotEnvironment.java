package rlpark.plugin.critterbot.environment;

import java.awt.Color;

import rlpark.plugin.critterbot.CritterbotProblem;
import rlpark.plugin.critterbot.actions.CritterbotAction;
import rlpark.plugin.critterbot.data.CritterbotLabels;
import rlpark.plugin.critterbot.data.CritterbotObservation;
import rlpark.plugin.critterbot.data.CritterbotLabels.LedMode;
import rlpark.plugin.critterbot.internal.CritterbotConnection;
import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.observations.Legend;
import rlpark.plugin.rltoys.envio.observations.ObsFilter;
import rlpark.plugin.robot.helpers.RobotEnvironment;
import rlpark.plugin.robot.observations.ObservationReceiver;
import zephyr.plugin.core.api.monitoring.abstracts.DataMonitor;
import zephyr.plugin.core.api.monitoring.abstracts.MonitorContainer;

public class CritterbotEnvironment extends RobotEnvironment implements CritterbotProblem, MonitorContainer {
  protected CritterbotAction agentAction;
  private LedMode ledMode = LedMode.BUSY;
  private final Color[] ledValues = new Color[CritterbotLabels.NbLeds];
  protected final CritterbotConnection critterbotConnection;

  protected CritterbotEnvironment(ObservationReceiver receiver) {
    super(receiver, false);
    critterbotConnection = (CritterbotConnection) receiver();
  }

  @Override
  public Legend legend() {
    return critterbotConnection.legend();
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
    return CritterbotLabels.newDefaultFilter(legend());
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

  @Override
  public double[] waitNewObs() {
    return super.waitNewObs();
  }

  @Override
  public void close() {
    super.close();
  }

  @Override
  public boolean isClosed() {
    return super.isClosed();
  }
}
