package rlpark.plugin.irobotview.views;

import static rlpark.plugin.irobot.data.IRobotDrops.BatteryCapacity;
import static rlpark.plugin.irobot.data.IRobotDrops.BatteryCharge;
import static rlpark.plugin.irobot.data.IRobotDrops.BatteryCurrent;
import static rlpark.plugin.irobot.data.IRobotDrops.BatteryTemperature;
import static rlpark.plugin.irobot.data.IRobotDrops.BatteryVoltage;
import static rlpark.plugin.irobot.data.IRobotDrops.Bump;
import static rlpark.plugin.irobot.data.IRobotDrops.Button;
import static rlpark.plugin.irobot.data.IRobotDrops.ChargingState;
import static rlpark.plugin.irobot.data.IRobotDrops.CliffSensor;
import static rlpark.plugin.irobot.data.IRobotDrops.CliffSignal;
import static rlpark.plugin.irobot.data.IRobotDrops.ConnectedHomeBase;
import static rlpark.plugin.irobot.data.IRobotDrops.ConnectedInternalCharger;
import static rlpark.plugin.irobot.data.IRobotDrops.DirtDetect;
import static rlpark.plugin.irobot.data.IRobotDrops.DriveAngle;
import static rlpark.plugin.irobot.data.IRobotDrops.DriveDistance;
import static rlpark.plugin.irobot.data.IRobotDrops.DriveRequested;
import static rlpark.plugin.irobot.data.IRobotDrops.ICLeft;
import static rlpark.plugin.irobot.data.IRobotDrops.ICOmni;
import static rlpark.plugin.irobot.data.IRobotDrops.ICRight;
import static rlpark.plugin.irobot.data.IRobotDrops.LightBumpSensor;
import static rlpark.plugin.irobot.data.IRobotDrops.LightBumpSignal;
import static rlpark.plugin.irobot.data.IRobotDrops.MotorCurrentMainBrush;
import static rlpark.plugin.irobot.data.IRobotDrops.MotorCurrentSideBrush;
import static rlpark.plugin.irobot.data.IRobotDrops.NumberStreamPackets;
import static rlpark.plugin.irobot.data.IRobotDrops.OIMode;
import static rlpark.plugin.irobot.data.IRobotDrops.SongNumber;
import static rlpark.plugin.irobot.data.IRobotDrops.SongPlaying;
import static rlpark.plugin.irobot.data.IRobotDrops.Stasis;
import static rlpark.plugin.irobot.data.IRobotDrops.WallSensor;
import static rlpark.plugin.irobot.data.IRobotDrops.WallSignal;
import static rlpark.plugin.irobot.data.IRobotDrops.WallVirtual;
import static rlpark.plugin.irobot.data.IRobotDrops.WheelDrop;
import static rlpark.plugin.irobot.data.IRobotDrops.WheelEncoder;
import static rlpark.plugin.irobot.data.IRobotDrops.WheelMotorCurrent;
import static rlpark.plugin.irobot.data.IRobotDrops.WheelRequested;
import rlpark.plugin.irobot.data.IRobotDrops;
import rlpark.plugin.irobot.robots.RoombaRobot;
import rlpark.plugin.robot.RobotLive;
import rltoys.math.ranges.Range;
import zephyr.plugin.core.api.internal.codeparser.codetree.ClassNode;
import zephyr.plugin.core.api.internal.codeparser.interfaces.CodeNode;
import zephyr.plugin.core.api.synchronization.Chrono;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.internal.observations.ObsLayout;
import zephyr.plugin.core.internal.observations.ObsWidget;
import zephyr.plugin.core.internal.observations.SensorCollection;
import zephyr.plugin.core.internal.observations.SensorTextGroup;
import zephyr.plugin.core.internal.observations.SensorTextGroup.TextClient;

@SuppressWarnings("restriction")
public class RoombaView extends IRobotView {
  static public class Provider extends IRobotViewProvider {
    static public final Provider instance = new Provider();

    @Override
    public boolean canViewDraw(CodeNode codeNode) {
      if (!super.canViewDraw(codeNode))
        return false;
      return canViewDrawInstance(((ClassNode) codeNode).instance());
    }
  }

  static boolean canViewDrawInstance(Object instance) {
    if (!RobotLive.class.isInstance(instance))
      return false;
    RobotLive problem = (RobotLive) instance;
    return problem.legend().hasLabel(IRobotDrops.LightBumpSensorCenterLeft);
  }

  @Override
  protected ObsLayout getObservationLayout() {
    SensorTextGroup infoGroup = createInfoGroup();
    SensorCollection wallCollection = new SensorCollection("Walls", createSensorGroup("Virtual", WallVirtual),
                                                           createSensorGroup("Sensor", WallSensor),
                                                           createSensorGroup("Signal", WallSignal));
    SensorCollection odoCollection = new SensorCollection("Odometry", createSensorGroup("Distance", DriveDistance),
                                                          createSensorGroup("Angle", DriveAngle),
                                                          createSensorGroup("Requested", DriveRequested));
    SensorCollection icCollection = new SensorCollection("Infrared Character", createSensorGroup("Omni", ICOmni),
                                                         createSensorGroup("Left", ICLeft), createSensorGroup("Right",
                                                                                                              ICRight));
    SensorCollection powerCollection = new SensorCollection("Battery", createSensorGroup("Current", BatteryCurrent),
                                                            createSensorGroup("Temperature", BatteryTemperature),
                                                            createSensorGroup("Charge", BatteryCharge),
                                                            createSensorGroup("Capacity", BatteryCapacity));
    SensorCollection cliffCollection = new SensorCollection("Cliffs", createSensorGroup("Sensors", CliffSensor),
                                                            createSensorGroup("Signal", CliffSignal));
    SensorCollection wheelCollection = new SensorCollection("Wheels", createSensorGroup("Dropped", WheelDrop),
                                                            createSensorGroup("Requested", WheelRequested),
                                                            createSensorGroup("Encoder", WheelEncoder),
                                                            createSensorGroup("Current", WheelMotorCurrent));
    SensorCollection lightBumperCollection = new SensorCollection("Light Bumper", createSensorGroup("Sensor",
                                                                                                    LightBumpSensor),
                                                                  createSensorGroup("Signal", LightBumpSignal));
    SensorCollection motorCurrentCollection = new SensorCollection("Brushes", createSensorGroup("Main",
                                                                                                MotorCurrentMainBrush),
                                                                   createSensorGroup("Side", MotorCurrentSideBrush));
    return new ObsLayout(new ObsWidget[][] {
        { infoGroup, createSensorGroup("Bumper", Bump), wheelCollection, odoCollection,
            createSensorGroup("Dirt", DirtDetect) },
        { icCollection, cliffCollection, createSensorGroup("Buttons", Button), motorCurrentCollection,
            createSensorGroup("Statis", Stasis) }, { wallCollection, lightBumperCollection, powerCollection } });
  }

  private SensorTextGroup createInfoGroup() {
    TextClient loopTimeTextClient = new TextClient("Loop Time:") {
      @SuppressWarnings("synthetic-access")
      @Override
      public String currentText() {
        Clock clock = instance.clock();
        if (clock == null)
          return "0000ms";
        return Chrono.toPeriodString(clock.lastPeriodNano());
      }
    };
    return new SensorTextGroup("Info", loopTimeTextClient, new IntegerTextClient(ChargingState, "Charging State:"),
                               new IntegerTextClient(BatteryVoltage, "Voltage:", "00000", "mV"),
                               new IntegerTextClient(ConnectedHomeBase, "Home base: "),
                               new IntegerTextClient(ConnectedInternalCharger, "Internal charger: "),
                               new IntegerTextClient(OIMode, "OI Mode: "), new IntegerTextClient(SongNumber, "Song: "),
                               new IntegerTextClient(SongPlaying, "Playing: "),
                               new IntegerTextClient(NumberStreamPackets, "Packets: "));
  }

  @Override
  protected Range[] ranges() {
    return RoombaRobot.getRanges();
  }

  @Override
  protected boolean isInstanceSupported(Object instance) {
    return canViewDrawInstance(instance);
  }
}
