package rlpark.plugin.irobot.logfiles;

import java.io.IOException;

import rlpark.plugin.irobot.internal.descriptors.DropDescriptors;
import rlpark.plugin.rltoys.envio.observations.Legend;
import rlpark.plugin.rltoys.utils.Utils;
import rlpark.plugin.robot.interfaces.RobotLog;
import rlpark.plugin.robot.internal.disco.datagroup.DropScalarGroup;
import rlpark.plugin.robot.internal.disco.drops.Drop;
import rlpark.plugin.robot.internal.disco.io.DiscoLogfile;
import rlpark.plugin.robot.internal.disco.io.DiscoPacket;
import rlpark.plugin.robot.internal.sync.Syncs;
import rlpark.plugin.robot.observations.ObservationVersatile;
import rlpark.plugin.robot.observations.ObservationVersatileArray;
import zephyr.plugin.core.api.monitoring.abstracts.DataMonitor;
import zephyr.plugin.core.api.monitoring.abstracts.MonitorContainer;
import zephyr.plugin.core.api.monitoring.abstracts.Monitored;

public class CreateBinaryLogfile implements MonitorContainer, RobotLog {
  public static final String Extension = "crtbin";
  private final static Drop sensorDrop = DropDescriptors.newCreateSensorDrop();
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
        return Syncs.createObservation(packet.time, packet.byteBuffer(), sensorGroup);
    }
    return null;
  }

  @Override
  public Legend legend() {
    return sensorGroup.legend();
  }

  @Override
  public boolean hasNextStep() {
    return nextObservation != null;
  }

  @Override
  public ObservationVersatileArray nextStep() {
    currentObservation = nextObservation;
    nextObservation = readNextObservation();
    return new ObservationVersatileArray(Utils.asList(currentObservation));
  }

  // @Override
  public void close() {
    discoLogFile.close();
  }

  @Override
  public void addToMonitor(DataMonitor monitor) {
    addToMonitor(monitor, this);
  }

  static public void addToMonitor(DataMonitor monitor, final RobotLog problem) {
    for (String label : problem.legend().getLabels()) {
      final int obsIndex = problem.legend().indexOf(label);
      monitor.add(label, new Monitored() {
        @Override
        public double monitoredValue() {
          double[] obs = problem.nextStep().doubleValues();
          if (obs == null)
            return -1;
          return obs[obsIndex];
        }
      });
    }
  }

  @Override
  public int observationPacketSize() {
    return sensorDrop.dataSize();
  }

  public String filepath() {
    return discoLogFile.filepath;
  }
}
