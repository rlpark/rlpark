package rlpark.plugin.irobot.logfiles;

import java.io.IOException;

import rlpark.plugin.irobot.data.IRobotDrops;
import rlpark.plugin.irobot.robots.IRobotEnvironment;
import rlpark.plugin.irobot.robots.IRobotProblem;
import rlpark.plugin.robot.disco.datagroup.DropScalarGroup;
import rlpark.plugin.robot.disco.drops.Drop;
import rlpark.plugin.robot.disco.io.DiscoLogfile;
import rlpark.plugin.robot.disco.io.DiscoPacket;
import rlpark.plugin.robot.sync.ObservationVersatile;
import rltoys.environments.envio.observations.Legend;
import zephyr.plugin.core.api.monitoring.abstracts.DataMonitor;
import zephyr.plugin.core.api.monitoring.abstracts.MonitorContainer;

public class CreateBinaryLogfile implements MonitorContainer, IRobotProblem {
  private final static Drop sensorDrop = IRobotDrops.newCreateSensorDrop();
  private final static DropScalarGroup sensorGroup = new DropScalarGroup(sensorDrop);
  private final DiscoLogfile discoLogFile;
  private ObservationVersatile nextObservation;
  private ObservationVersatile currentObservation;

  public CreateBinaryLogfile(String logfilename) throws IOException {
    discoLogFile = new DiscoLogfile(logfilename);
    nextObservation = readNextObservation();
  }

  private ObservationVersatile readNextObservation() {
    while (discoLogFile.hasNext()) {
      DiscoPacket packet = discoLogFile.next();
      if (sensorDrop.name().equals(packet.name))
        return new ObservationVersatile(packet.byteBuffer(), sensorGroup);
    }
    return null;
  }

  @Override
  public Legend legend() {
    return sensorGroup.legend();
  }

  public boolean hasNextStep() {
    return nextObservation != null;
  }

  public void step() {
    currentObservation = nextObservation;
    nextObservation = readNextObservation();
  }

  @Override
  public double[] lastReceivedObs() {
    if (currentObservation == null)
      return null;
    return currentObservation.doubleValues();
  }

  public ObservationVersatile[] lastReceivedRawObs() {
    if (currentObservation == null)
      return null;
    return new ObservationVersatile[] { currentObservation };
  }

  @Override
  public void close() {
    discoLogFile.close();
  }

  @Override
  public void addToMonitor(DataMonitor monitor) {
    IRobotEnvironment.addToMonitor(monitor, this);
  }

  @Override
  public int observationPacketSize() {
    return sensorDrop.dataSize();
  }
}