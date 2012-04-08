package rlpark.plugin.critterbot.data;


import rlpark.plugin.rltoys.envio.observations.Legend;
import rlpark.plugin.rltoys.envio.observations.ObsFilter;

public class CritterbotLabels {
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


  public static final String MotorMode = "MotorMode";
  public static final String LedMode = "LedMode";
  public static final String VelocityCommand = "VelocityCommand";
  public static ObsFilter newDefaultFilter(Legend legend) {
    return new ObsFilter(legend, CritterbotLabels.PowerSource, CritterbotLabels.ChargeState, CritterbotLabels.BusVoltage,
                         CritterbotLabels.Bat, CritterbotLabels.Motor, CritterbotLabels.Accel, CritterbotLabels.Mag,
                         CritterbotLabels.RotationVel, CritterbotLabels.IRDistance, CritterbotLabels.IRLight,
                         CritterbotLabels.Light, CritterbotLabels.Thermal, CritterbotLabels.ErrorFlags,
                         CritterbotLabels.CycleTime, CritterbotLabels.MonitorState, CritterbotLabels.Microphone);
  }
}
