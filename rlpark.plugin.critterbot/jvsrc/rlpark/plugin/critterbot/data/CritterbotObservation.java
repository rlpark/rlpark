package rlpark.plugin.critterbot.data;

import java.io.Serializable;

import rlpark.plugin.rltoys.envio.observations.Legend;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

@Monitor
public class CritterbotObservation implements Serializable {
  private static final long serialVersionUID = -2034233010499765404L;
  public final int[] motorTemperature = new int[CritterbotLabels.NbMotors];
  public final int[] motorCurrent = new int[CritterbotLabels.NbMotors];
  public final int[] motorSpeed = new int[CritterbotLabels.NbMotors];
  public final int[] motorCommand = new int[CritterbotLabels.NbMotors];
  public final int[] thermal = new int[CritterbotLabels.NbThermal];
  public final int[] light = new int[CritterbotLabels.NbLight];
  public final int[] irLight = new int[CritterbotLabels.NbIRLight];
  public final int[] irDistance = new int[CritterbotLabels.NbIRDistance];
  public final int[] bat = new int[CritterbotLabels.NbBatteries];
  public final int rotationVel;
  public final int magX;
  public final int magY;
  public final int magZ;
  public final int accelX;
  public final int accelY;
  public final int accelZ;
  public final int busVoltage;
  public final int powerSource;
  public final int chargeState;
  public final int dataSource;
  public final long time;

  public CritterbotObservation(Legend legend, long time, double[] o) {
    this.time = time;
    rotationVel = valueOf(legend, o, CritterbotLabels.RotationVel);
    magX = valueOf(legend, o, CritterbotLabels.Mag + "X");
    magY = valueOf(legend, o, CritterbotLabels.Mag + "Y");
    magZ = valueOf(legend, o, CritterbotLabels.Mag + "Z");
    accelX = valueOf(legend, o, CritterbotLabels.Accel + "X");
    accelY = valueOf(legend, o, CritterbotLabels.Accel + "Y");
    accelZ = valueOf(legend, o, CritterbotLabels.Accel + "Z");
    busVoltage = valueOf(legend, o, CritterbotLabels.BusVoltage);
    powerSource = valueOf(legend, o, CritterbotLabels.PowerSource);
    chargeState = valueOf(legend, o, CritterbotLabels.ChargeState);
    dataSource = valueOf(legend, o, CritterbotLabels.DataSource);

    fillMotor(legend, o, CritterbotLabels.Temperature, motorTemperature);
    fillMotor(legend, o, CritterbotLabels.Current, motorCurrent);
    fillMotor(legend, o, CritterbotLabels.Speed, motorSpeed);
    fillMotor(legend, o, CritterbotLabels.Command, motorCommand);
    fill(legend, o, CritterbotLabels.Thermal, thermal);
    fill(legend, o, CritterbotLabels.Light, light);
    fill(legend, o, CritterbotLabels.IRLight, irLight);
    fill(legend, o, CritterbotLabels.IRDistance, irDistance);
    fill(legend, o, CritterbotLabels.Bat, bat);
  }

  private int valueOf(Legend legend, double[] o, String label) {
    int index = legend.indexOf(label);
    return index >= 0 ? (int) o[index] : 0;
  }

  private void fill(Legend legend, double[] o, String prefix, int[] result) {
    for (int i = 0; i < result.length; i++)
      result[i] = (int) o[legend.indexOf(prefix + i)];
  }

  private void fillMotor(Legend legend, double[] o, String prefix, int[] result) {
    for (int i = 0; i < result.length; i++)
      result[i] = (int) o[legend.indexOf(CritterbotLabels.Motor + prefix + i)];
  }
}
