package rlpark.plugin.robot.observations;

import java.util.List;

import rlpark.plugin.rltoys.envio.observations.ObsAsDoubles;
import rlpark.plugin.rltoys.math.GrayCode;
import rlpark.plugin.rltoys.math.vector.BinaryVector;
import rlpark.plugin.rltoys.math.vector.implementations.BVector;


public class ObservationVersatileArray implements ObsAsDoubles {
  private final ObservationVersatile[] observations;

  public ObservationVersatileArray(List<ObservationVersatile> observations) {
    this.observations = new ObservationVersatile[observations.size()];
    observations.toArray(this.observations);
  }

  public ObservationVersatile last() {
    if (observations == null || observations.length == 0)
      return null;
    return observations[observations.length - 1];
  }

  public BinaryVector toRawBinary() {
    ObservationVersatile last = last();
    if (last == null)
      return null;
    return BVector.toBinary(last.rawData());
  }

  public BinaryVector toGrayCodeBinary() {
    ObservationVersatile last = last();
    if (last == null)
      return null;
    return BVector.toBinary(GrayCode.toGrayCode(last.rawData()));
  }

  @Override
  public double[] doubleValues() {
    ObservationVersatile last = last();
    if (last == null)
      return null;
    return last.doubleValues();
  }

  public ObservationVersatile[] array() {
    return observations;
  }
}
