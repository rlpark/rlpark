package rlpark.plugin.irobot.logfiles;

import java.util.ArrayList;
import java.util.List;

import rlpark.plugin.rltoys.envio.observations.Legend;
import rlpark.plugin.rltoys.utils.Utils;
import rlpark.plugin.robot.interfaces.RobotLog;
import rlpark.plugin.robot.observations.ObservationVersatile;
import rlpark.plugin.robot.observations.ObservationVersatileArray;
import zephyr.plugin.core.api.internal.logfiles.LogFile;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

@SuppressWarnings("restriction")
public class IRobotLogFile implements RobotLog {
  public static final String Extension = "irobotlog";
  private ObservationVersatile lastReceived = null;

  @Monitor(emptyLabel = true)
  private final LogFile logfile;

  public IRobotLogFile(String filepath) {
    logfile = LogFile.load(filepath);
  }

  public double[] lastReceivedObs() {
    return logfile.currentLine();
  }

  @Override
  public Legend legend() {
    List<String> labels = new ArrayList<String>();
    for (String label : logfile.labels())
      labels.add(label);
    return new Legend(labels);
  }

  public void step() {
    if (hasNextStep()) {
      logfile.step();
      lastReceived = new ObservationVersatile(-1, null, logfile.currentLine());
    } else
      lastReceived = null;
  }

  @Override
  public boolean hasNextStep() {
    return !logfile.eof();
  }

  public void close() {
    logfile.close();
  }

  public String filepath() {
    return logfile.filepath;
  }

  @Override
  public int observationPacketSize() {
    return 0;
  }

  @Override
  public ObservationVersatileArray nextStep() {
    step();
    return new ObservationVersatileArray(Utils.asList(lastReceived));
  }
}
