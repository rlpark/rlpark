package rlpark.plugin.irobotview.views;

import static rlpark.plugin.irobot.data.IRobots.BatteryCapacity;
import static rlpark.plugin.irobot.data.IRobots.BatteryCharge;
import static rlpark.plugin.irobot.data.IRobots.BatteryCurrent;
import static rlpark.plugin.irobot.data.IRobots.BatteryTemperature;
import static rlpark.plugin.irobot.data.IRobots.BatteryVoltage;
import static rlpark.plugin.irobot.data.IRobots.Bump;
import static rlpark.plugin.irobot.data.IRobots.Button;
import static rlpark.plugin.irobot.data.IRobots.ChargingState;
import static rlpark.plugin.irobot.data.IRobots.CliffSensor;
import static rlpark.plugin.irobot.data.IRobots.CliffSignal;
import static rlpark.plugin.irobot.data.IRobots.ConnectedHomeBase;
import static rlpark.plugin.irobot.data.IRobots.ConnectedInternalCharger;
import static rlpark.plugin.irobot.data.IRobots.DirtDetect;
import static rlpark.plugin.irobot.data.IRobots.DriveAngle;
import static rlpark.plugin.irobot.data.IRobots.DriveDistance;
import static rlpark.plugin.irobot.data.IRobots.DriveRequested;
import static rlpark.plugin.irobot.data.IRobots.ICLeft;
import static rlpark.plugin.irobot.data.IRobots.ICOmni;
import static rlpark.plugin.irobot.data.IRobots.ICRight;
import static rlpark.plugin.irobot.data.IRobots.LightBumpSensor;
import static rlpark.plugin.irobot.data.IRobots.LightBumpSignal;
import static rlpark.plugin.irobot.data.IRobots.MotorCurrentMainBrush;
import static rlpark.plugin.irobot.data.IRobots.MotorCurrentSideBrush;
import static rlpark.plugin.irobot.data.IRobots.NumberStreamPackets;
import static rlpark.plugin.irobot.data.IRobots.OIMode;
import static rlpark.plugin.irobot.data.IRobots.SongNumber;
import static rlpark.plugin.irobot.data.IRobots.SongPlaying;
import static rlpark.plugin.irobot.data.IRobots.Stasis;
import static rlpark.plugin.irobot.data.IRobots.WallSensor;
import static rlpark.plugin.irobot.data.IRobots.WallSignal;
import static rlpark.plugin.irobot.data.IRobots.WallVirtual;
import static rlpark.plugin.irobot.data.IRobots.WheelDrop;
import static rlpark.plugin.irobot.data.IRobots.WheelEncoder;
import static rlpark.plugin.irobot.data.IRobots.WheelMotorCurrent;
import static rlpark.plugin.irobot.data.IRobots.WheelRequested;
import rlpark.plugin.irobot.data.IRobots;
import rlpark.plugin.robot.interfaces.RobotLive;
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
    return problem.legend().hasLabel(IRobots.LightBumpSensorCenterLeft);
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
  protected boolean isInstanceSupported(Object instance) {
    return canViewDrawInstance(instance);
  }
}
