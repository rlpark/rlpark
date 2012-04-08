package rlpark.plugin.critterbot.crtrlog;

import java.util.ArrayList;
import java.util.List;

import rlpark.plugin.critterbot.CritterbotProblem;
import rlpark.plugin.critterbot.actions.CritterbotAction;
import rlpark.plugin.critterbot.environment.CritterbotEnvironments;
import rlpark.plugin.rltoys.envio.observations.Legend;
import rlpark.plugin.rltoys.utils.Utils;
import rlpark.plugin.robot.helpers.Robots;
import rlpark.plugin.robot.interfaces.RobotLog;
import rlpark.plugin.robot.observations.ObservationVersatile;
import rlpark.plugin.robot.observations.ObservationVersatileArray;
import zephyr.plugin.core.api.internal.logfiles.LogFile;
import zephyr.plugin.core.api.monitoring.abstracts.DataMonitor;
import zephyr.plugin.core.api.monitoring.abstracts.MonitorContainer;
import zephyr.plugin.core.api.monitoring.annotations.LabelProvider;

@SuppressWarnings("restriction")
public class CrtrLogFile implements CritterbotProblem, RobotLog, MonitorContainer {
  public final String filepath;
  private final LogFile logfile;
  private ObservationVersatile currentObservation;
  private final Legend legend;
  private final int timeIndex;

  public CrtrLogFile(String filepath) {
    logfile = LogFile.load(filepath);
    this.filepath = filepath;
    timeIndex = findTimeIndex();
    legend = createLegend();
  }

  private int findTimeIndex() {
    String[] labels = logfile.labels();
    for (int i = 0; i < labels.length; i++)
      if (labels[i].equals("LocalTime"))
        return i;
    return -1;
  }

  private Legend createLegend() {
    List<String> legendLabels = new ArrayList<String>();
    String[] labels = logfile.labels();
    for (int i = 0; i < labels.length; i++) {
      if (i == timeIndex)
        continue;
      legendLabels.add(labels[i]);
    }
    return new Legend(legendLabels);
  }

  @LabelProvider(ids = { "values" })
  String observationLabel(int index) {
    return legend.label(index);
  }

  @Override
  public Legend legend() {
    return legend;
  }

  @Override
  public ObservationVersatileArray nextStep() {
    logfile.step();
    double[] obs = logfile.currentLine();
    long localTime = 0;
    if (timeIndex >= 0) {
      localTime = (long) obs[timeIndex];
      obs = removeLocalTimeValue(obs);
    }
    currentObservation = new ObservationVersatile(localTime, Robots.doubleArrayToByteArray(obs), obs);
    return new ObservationVersatileArray(Utils.asList(currentObservation));
  }

  private double[] removeLocalTimeValue(double[] obs) {
    double[] result = new double[obs.length - 1];
    System.arraycopy(obs, 0, result, 0, timeIndex);
    System.arraycopy(obs, timeIndex + 1, result, timeIndex, obs.length - timeIndex - 1);
    return result;
  }

  public double[] step() {
    return nextStep().doubleValues();
  }

  @Override
  public boolean hasNextStep() {
    return !logfile.eof();
  }

  @Override
  public void close() {
    logfile.close();
  }

  public String filepath() {
    return logfile.filepath;
  }

  @Override
  public String label() {
    return logfile.label();
  }

  public static CrtrLogFile load(String filepath) {
    return new CrtrLogFile(filepath);
  }

  @Override
  public CritterbotAction lastAction() {
    return null;
  }

  @Override
  public double[] lastReceivedObs() {
    return currentObservation != null ? currentObservation.doubleValues() : null;
  }

  @Override
  public int observationPacketSize() {
    return logfile.labels().length * 4;
  }

  @Override
  public void addToMonitor(DataMonitor monitor) {
    CritterbotEnvironments.addObservationsLogged(this, monitor);
  }
}
