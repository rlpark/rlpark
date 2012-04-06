package critterbot.environment;

import org.rlcommunity.critterbot.javadrops.drops.CritterControlDrop;
import org.rlcommunity.critterbot.javadrops.drops.CritterStateDrop;

import rlpark.plugin.rltoys.envio.observations.Legend;
import rlpark.plugin.rltoys.envio.observations.ObsFilter;
import rlpark.plugin.robot.internal.disco.drops.Drop;
import rlpark.plugin.robot.internal.disco.drops.DropArray;
import rlpark.plugin.robot.internal.disco.drops.DropColor;
import rlpark.plugin.robot.internal.disco.drops.DropData;
import rlpark.plugin.robot.internal.disco.drops.DropInteger;
import rlpark.plugin.robot.internal.disco.drops.DropTime;

public class CritterbotDrops {
  public static final int NbMotors = 3;
  public static final int NbThermal = 8;
  public static final int NbLight = 4;
  public static final int NbIRLight = 8;
  public static final int NbBump = 32;
  public static final int NbIRDistance = 10;
  public static final int NbBatteries = 3;

  public static final String Temperature = "Temperature";
  public static final String Current = "Current";
  public static final String Speed = "Speed";
  public static final String Command = "Command";
  public static final String Bump = "Bump";
  public static final String Thermal = "Thermal";
  public static final String Light = "Light";
  public static final String IRLight = "IRLight";
  public static final String IRDistance = "IRDistance";
  public static final String RotationVel = "RotationVel";
  public static final String Mag = "Mag";
  public static final String Accel = "Accel";
  public static final String Motor = "Motor";
  public static final String Bat = "Bat";
  public static final String BusVoltage = "BusVoltage";
  public static final String PowerSource = "PowerSource";
  public static final String ChargeState = "ChargeState";
  public static final String DataSource = "DataSource";
  public static final String MonitorState = "MonitorState";
  public static final String CycleTime = "CycleTime";
  public static final String ErrorFlags = "ErrorFlags";
  public static final String Microphone = "Microphone";
  public static final String MicrophoneFFT = Microphone + "FFT";

  public static final String MotorSpeed = Motor + Speed;
  public static final String MotorCurrent = Motor + Current;
  public static final String MotorCommand = Motor + Command;

  final public static int VoltageMax = 25;
  final public static int NbLeds = 16;

  public enum LedMode {
    NONE, CLEAR, BATTERY, BALL, ERROR, EMERGENCY, BUSY, CUSTOM
  };


  final static private DropData[] observationDescriptor = { new DropInteger(DataSource), new DropTime(),
      new DropInteger(PowerSource), new DropInteger(ChargeState), new DropInteger(BusVoltage),
      new DropArray(Bat, NbBatteries),
      new DropArray(Motor, Command + "0", Speed + "0", Current + "0", Temperature + "0"),
      new DropArray(Motor, Command + "1", Speed + "1", Current + "1", Temperature + "1"),
      new DropArray(Motor, Command + "2", Speed + "2", Current + "2", Temperature + "2"),
      new DropArray(Accel, "X", "Y", "Z"),
      new DropArray(Mag, "X", "Y", "Z"), new DropInteger(RotationVel), new DropArray(IRDistance, NbIRDistance),
      new DropArray(IRLight, NbIRLight), new DropArray(Light, NbLight), new DropArray(Thermal, NbThermal),
      new DropArray(Bump, NbBump),
      new DropInteger(ErrorFlags), new DropInteger(CycleTime), new DropInteger(MonitorState) };

  protected static final String MotorMode = "MotorMode";
  protected static final String LedMode = "LedMode";
  protected static final String VelocityCommand = "VelocityCommand";
  final static private DropData[] actionDescriptor = { new DropInteger(MotorMode),
      new DropArray(VelocityCommand, "M100", "M220", "M340"),
      new DropInteger(LedMode), new DropArray(new DropColor(""), "Led", NbLeds) };

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

  public static ObsFilter newDefaultFilter(Legend legend) {
    return new ObsFilter(legend, CritterbotDrops.PowerSource, CritterbotDrops.ChargeState,
                         CritterbotDrops.BusVoltage, CritterbotDrops.Bat, CritterbotDrops.Motor,
                         CritterbotDrops.Accel, CritterbotDrops.Mag, CritterbotDrops.RotationVel,
                         CritterbotDrops.IRDistance, CritterbotDrops.IRLight, CritterbotDrops.Light,
                         CritterbotDrops.Thermal, CritterbotDrops.ErrorFlags, CritterbotDrops.CycleTime,
                         CritterbotDrops.MonitorState, CritterbotDrops.Microphone);
  }
}
