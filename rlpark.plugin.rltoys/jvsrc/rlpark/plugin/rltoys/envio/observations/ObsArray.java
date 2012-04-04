package rlpark.plugin.rltoys.envio.observations;

public class ObsArray implements ObsAsDoubles {
  private final double[] values;

  public ObsArray(double[] values) {
    this.values = values;
  }

  @Override
  public double[] doubleValues() {
    return values;
  }
}
