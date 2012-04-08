package rlpark.plugin.critterbot.internal;

import org.rlcommunity.critterbot.javadrops.drops.CritterControlDrop;
import org.rlcommunity.critterbot.javadrops.drops.CritterStateDrop;

import rlpark.plugin.critterbot.data.CritterbotLabels;
import rlpark.plugin.robot.internal.disco.drops.Drop;
import rlpark.plugin.robot.internal.disco.drops.DropArray;
import rlpark.plugin.robot.internal.disco.drops.DropColor;
import rlpark.plugin.robot.internal.disco.drops.DropData;
import rlpark.plugin.robot.internal.disco.drops.DropInteger;
import rlpark.plugin.robot.internal.disco.drops.DropTime;

public class DropDescriptors {

  public final static DropData[] observationDescriptor = { new DropInteger(CritterbotLabels.DataSource), new DropTime(),
  new DropInteger(CritterbotLabels.PowerSource), new DropInteger(CritterbotLabels.ChargeState), new DropInteger(CritterbotLabels.BusVoltage),
  new DropArray(CritterbotLabels.Bat, CritterbotLabels.NbBatteries),
  new DropArray(CritterbotLabels.Motor, CritterbotLabels.Command + "0", CritterbotLabels.Speed + "0", CritterbotLabels.Current + "0", CritterbotLabels.Temperature + "0"),
  new DropArray(CritterbotLabels.Motor, CritterbotLabels.Command + "1", CritterbotLabels.Speed + "1", CritterbotLabels.Current + "1", CritterbotLabels.Temperature + "1"),
  new DropArray(CritterbotLabels.Motor, CritterbotLabels.Command + "2", CritterbotLabels.Speed + "2", CritterbotLabels.Current + "2", CritterbotLabels.Temperature + "2"),
  new DropArray(CritterbotLabels.Accel, "X", "Y", "Z"), new DropArray(CritterbotLabels.Mag, "X", "Y", "Z"), new DropInteger(CritterbotLabels.RotationVel),
  new DropArray(CritterbotLabels.IRDistance, CritterbotLabels.NbIRDistance), new DropArray(CritterbotLabels.IRLight, CritterbotLabels.NbIRLight), new DropArray(CritterbotLabels.Light, CritterbotLabels.NbLight),
  new DropArray(CritterbotLabels.Thermal, CritterbotLabels.NbThermal), new DropArray(CritterbotLabels.Bump, CritterbotLabels.NbBump), new DropInteger(CritterbotLabels.ErrorFlags),
  new DropInteger(CritterbotLabels.CycleTime), new DropInteger(CritterbotLabels.MonitorState) };
  public final static DropData[] actionDescriptor = { new DropInteger(CritterbotLabels.MotorMode),
  new DropArray(CritterbotLabels.VelocityCommand, "M100", "M220", "M340"), new DropInteger(CritterbotLabels.LedMode),
  new DropArray(new DropColor(""), "Led", CritterbotLabels.NbLeds) };
  static public Drop newObservationDrop() {
    Drop drop = new Drop("CritterStateDrop", observationDescriptor);
    assert drop.dataSize() == new CritterStateDrop().getSize();
    return drop;
  }
  static public Drop newActionDrop() {
    Drop drop = new Drop("CritterControlDrop", actionDescriptor);
    assert drop.dataSize() == new CritterControlDrop().getSize();
    return drop;
  }

}
