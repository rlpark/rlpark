package rlpark.plugin.irobot.data;


public class IRobotLabels {
  static public class ICOmniValues {
    static public final int SpinRight = 129;
    static public final int Forward = 130;
    static public final int SpinLeft = 131;
    static public final int Spot = 132;
    static public final int Max = 133;
    static public final int Clean = 136;
    static public final int Pause = 137;
    static public final int Power = 138;
    static public final int TurnLeft = 139;
    static public final int TurnRight = 140;
    static public final int RedBuoy = 248;
    static public final int GreenBuoy = 244;
    static public final int ForceField = 242;
    static public final int RedBuoyGreenBuoy = 252;
    static public final int RedBuoyForceField = 250;
    static public final int GreenBuoyForceField = 246;
    static public final int RedBuoyGreenBuoyForceField = 254;
  };

  static public enum ChargingState {
    NotCharging, ReconditioningCharging, FullCharging, TrickleCharging, Waiting, ChargingFaultCondition
  }

  static public enum OIModeEnum {
    Off, Passive, Safe, Full
  };

  static public final int DiscoDefaultPort = 3000;
  static public final String IRobotCommandDropName = "IRobotCommandByteStringDrop";
  static public final String RoombaSensorDropName = "RoombaSensorDrop";
  static public final int RoombaSensorsPacketSize = 80;
  static public final String CreateSensorDropName = "CreateSensorDrop";
  static public final int CreateSensorsPacketSize = 53;

  public static final String Center = "Center";
  public static final String Front = "Front";
  public static final String Left = "Left";
  public static final String Requested = "Requested";
  public static final String Right = "Right";
  public static final String Sensor = "Sensor";
  public static final String Signal = "Signal";
  public static final String OverCurrent = "OverCurrent";

  public static final String Battery = "Battery";
  public static final String BatteryVoltage = Battery + "Voltage";
  public static final String BatteryCurrent = Battery + "Current";
  public static final String BatteryTemperature = Battery + "Temperature";
  public static final String BatteryCharge = Battery + "Charge";
  public static final String BatteryCapacity = Battery + "Capacity";
  public static final String Button = "Button";
  public static final String ButtonAdvance = Button + "Advance";
  public static final String ButtonClock = Button + "Clock";
  public static final String ButtonDay = Button + "Day";
  public static final String ButtonDock = Button + "Dock";
  public static final String ButtonHour = Button + "Hour";
  public static final String ButtonMinute = Button + "Minute";
  public static final String ButtonPlay = Button + "Play";
  public static final String ButtonSchedule = Button + "Schedule";
  public static final String ButtonSpot = Button + "Spot";
  public static final String ButtonClean = Button + "Clean";
  public static final String Bump = "Bump";
  public static final String BumpLeft = Bump + Left;
  public static final String BumpRight = Bump + Right;
  public static final String CargoBay = "CargoBay";
  public static final String CargoBayDigitalInputs = CargoBay + "DigitalInputs";
  public static final String CargoBayAnalogSignal = CargoBay + "AnalogSignal";
  public static final String ChargingState = "ChargingState";
  public static final String Cliff = "Cliff";
  public static final String CliffSensor = Cliff + Sensor;
  public static final String CliffSensorLeft = CliffSensor + Left;
  public static final String CliffSensorFrontLeft = CliffSensor + Front + Left;
  public static final String CliffSensorFrontRight = CliffSensor + Front + Right;
  public static final String CliffSensorRight = CliffSensor + Right;
  public static final String CliffSignal = Cliff + Signal;
  public static final String CliffSignalLeft = CliffSignal + Left;
  public static final String CliffSignalFrontLeft = CliffSignal + Front + Left;
  public static final String CliffSignalFrontRight = CliffSignal + Front + Right;
  public static final String CliffSignalRight = CliffSignal + Right;
  public static final String Connected = "Connected";
  public static final String ConnectedHomeBase = Connected + "HomeBase";
  public static final String ConnectedInternalCharger = Connected + "InternalCharger";
  public static final String DirtDetect = "DirtDetect";
  public static final String Drive = "Drive";
  public static final String DriveAngle = Drive + "Angle";
  public static final String DriveDistance = Drive + "Distance";
  public static final String DriveRequested = Drive + Requested;
  public static final String DriverRequestedVelocity = DriveRequested + "Velocity";
  public static final String DriverRequestedRadius = DriveRequested + "Radius";
  public static final String InfraredChar = "IC";
  public static final String ICLeft = InfraredChar + Left;
  public static final String ICRight = InfraredChar + Right;
  public static final String ICOmni = InfraredChar + "Omni";
  public static final String LightBump = "LightBump";
  public static final String LightBumpSensor = "LightBump" + Sensor;
  public static final String LightBumpSensorLeft = LightBumpSensor + Left;
  public static final String LightBumpSensorFrontLeft = LightBumpSensor + Front + Left;
  public static final String LightBumpSensorCenterLeft = LightBumpSensor + Center + Left;
  public static final String LightBumpSensorCenterRight = LightBumpSensor + Center + Right;
  public static final String LightBumpSensorFrontRight = LightBumpSensor + Front + Right;
  public static final String LightBumpSensorRight = LightBumpSensor + Right;
  public static final String LightBumpSignal = "LightBump" + Signal;
  public static final String LightBumpSignalLeft = LightBumpSignal + Left;
  public static final String LightBumpSignalFrontLeft = LightBumpSignal + Front + Left;
  public static final String LightBumpSignalCenterLeft = LightBumpSignal + Center + Left;
  public static final String LightBumpSignalCenterRight = LightBumpSignal + Center + Right;
  public static final String LightBumpSignalFrontRight = LightBumpSignal + Front + Right;
  public static final String LightBumpSignalRight = LightBumpSignal + Right;
  public static final String LowSideDriver = "LowSideDriver";
  public static final String LowSideDriverOverCurrent = LowSideDriver + OverCurrent;
  public static final String MotorCurrent = "MotorCurrent";
  public static final String MotorCurrentMainBrush = MotorCurrent + "MainBrush";
  public static final String MotorCurrentSideBrush = MotorCurrent + "SideBrush";
  public static final String NumberStreamPackets = "NumberStreamPackets";
  public static final String OIMode = "OIMode";
  public static final String Song = "Song";
  public static final String SongNumber = Song + "Number";
  public static final String SongPlaying = Song + "Playing";
  public static final String Stasis = "Statis";
  public static final String Wall = "Wall";
  public static final String WallSignal = Wall + Signal;
  public static final String WallVirtual = Wall + "Virtual";
  public static final String WallSensor = Wall + Sensor;
  public static final String Wheel = "Wheel";
  public static final String WheelDrop = Wheel + "Drop";
  public static final String WheelDropLeft = WheelDrop + Left;
  public static final String WheelDropRight = WheelDrop + Right;
  public static final String WheelDropCaster = WheelDrop + "Caster";
  public static final String WheelEncoder = Wheel + "Encoder";
  public static final String WheelEncoderLeft = WheelEncoder + Left;
  public static final String WheelEncoderRight = WheelEncoder + Right;
  public static final String WheelMotorCurrent = Wheel + MotorCurrent;
  public static final String WheelMotorCurrentLeft = Wheel + MotorCurrent + Left;
  public static final String WheelMotorCurrentRight = Wheel + MotorCurrent + Right;
  public static final String WheelRequested = Wheel + Requested;
  public static final String WheelRequestedVelocityRight = WheelRequested + "Velocity" + Right;
  public static final String WheelRequestedVelocityLeft = WheelRequested + "Velocity" + Left;
  public static final String WheelOverCurrent = Wheel + OverCurrent;
  public static final String WheelOverCurrentLeft = WheelOverCurrent + Left;
  public static final String WheelOverCurrentRight = WheelOverCurrent + Right;
}
