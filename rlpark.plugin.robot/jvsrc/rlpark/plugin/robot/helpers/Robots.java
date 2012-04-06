package rlpark.plugin.robot.helpers;

import java.util.Arrays;

import rlpark.plugin.rltoys.envio.observations.Observation;
import rlpark.plugin.robot.interfaces.RobotLive;
import rlpark.plugin.robot.observations.ObservationVersatile;
import rlpark.plugin.robot.observations.ObservationVersatileArray;
import zephyr.plugin.core.api.monitoring.abstracts.DataMonitor;
import zephyr.plugin.core.api.monitoring.abstracts.Monitored;

public class Robots {
  static public void addToMonitor(DataMonitor monitor, final RobotLive problem) {
    for (String label : problem.legend().getLabels()) {
      final int obsIndex = problem.legend().indexOf(label);
      monitor.add(label, new Monitored() {
        @Override
        public double monitoredValue() {
          double[] obs = Robots.toDoubles(problem.lastReceivedRawObs());
          if (obs == null)
            return -1;
          return obs[obsIndex];
        }
      });
    }
  }

  public static byte[] doubleArrayToByteArray(double[] current) {
    byte[] result = new byte[current.length << 2];
    int j = 0;
    for (int i = 0; i < current.length; i++) {
      int x = (int) current[i];
      byte[] b = new byte[] { (byte) (x >>> 24), (byte) (x >>> 16), (byte) (x >>> 8), (byte) x };
      System.arraycopy(b, 0, result, j, 4);
      j += 4;
    }
    return result;
  }

  public static double[] byteArrayToDoubleArray(byte[] current) {
    double[] result = new double[current.length / 4];
    int j = 0;
    for (int i = 0; i < result.length; i++) {
      result[i] = byteArrayToInt(Arrays.copyOfRange(current, j, j + 4));
      j += 4;
    }
    return result;
  }

  public static final int byteArrayToInt(byte[] current) {
    return (current[0] << 24) + ((current[1] & 0xFF) << 16) + ((current[2] & 0xFF) << 8) + (current[3] & 0xFF);
  }

  public static double[] toDoubles(ObservationVersatileArray observation) {
    return observation != null ? observation.doubleValues() : null;
  }

  public static double[] toDoubles(Observation obs) {
    return toDoubles((ObservationVersatileArray) obs);
  }

  public static double[] toDoubles(ObservationVersatile obs) {
    return obs != null ? obs.doubleValues() : null;
  }
}
