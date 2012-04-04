package critterbot;

import java.io.Serializable;

import rlpark.plugin.rltoys.envio.observations.Legend;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;
import critterbot.environment.CritterbotDrops;

@Monitor
public class CritterbotObservation implements Serializable {
  private static final long serialVersionUID = -2034233010499765404L;
  public final int[] motorTemperature = new int[CritterbotDrops.NbMotors];
  public final int[] motorCurrent = new int[CritterbotDrops.NbMotors];
  public final int[] motorSpeed = new int[CritterbotDrops.NbMotors];
  public final int[] motorCommand = new int[CritterbotDrops.NbMotors];
  public final int[] thermal = new int[CritterbotDrops.NbThermal];
  public final int[] light = new int[CritterbotDrops.NbLight];
  public final int[] irLight = new int[CritterbotDrops.NbIRLight];
  public final int[] irDistance = new int[CritterbotDrops.NbIRDistance];
  public final int[] bat = new int[CritterbotDrops.NbBatteries];
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
    rotationVel = valueOf(legend, o, CritterbotDrops.RotationVel);
    magX = valueOf(legend, o, CritterbotDrops.Mag + "X");
    magY = valueOf(legend, o, CritterbotDrops.Mag + "Y");
    magZ = valueOf(legend, o, CritterbotDrops.Mag + "Z");
    accelX = valueOf(legend, o, CritterbotDrops.Accel + "X");
    accelY = valueOf(legend, o, CritterbotDrops.Accel + "Y");
    accelZ = valueOf(legend, o, CritterbotDrops.Accel + "Z");
    busVoltage = valueOf(legend, o, CritterbotDrops.BusVoltage);
    powerSource = valueOf(legend, o, CritterbotDrops.PowerSource);
    chargeState = valueOf(legend, o, CritterbotDrops.ChargeState);
    dataSource = valueOf(legend, o, CritterbotDrops.DataSource);

    fillMotor(legend, o, CritterbotDrops.Temperature, motorTemperature);
    fillMotor(legend, o, CritterbotDrops.Current, motorCurrent);
    fillMotor(legend, o, CritterbotDrops.Speed, motorSpeed);
    fillMotor(legend, o, CritterbotDrops.Command, motorCommand);
    fill(legend, o, CritterbotDrops.Thermal, thermal);
    fill(legend, o, CritterbotDrops.Light, light);
    fill(legend, o, CritterbotDrops.IRLight, irLight);
    fill(legend, o, CritterbotDrops.IRDistance, irDistance);
    fill(legend, o, CritterbotDrops.Bat, bat);
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
      result[i] = (int) o[legend.indexOf(CritterbotDrops.Motor + prefix + i)];
  }
}
