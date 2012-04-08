package rlpark.plugin.irobotview.views;

import static rlpark.plugin.irobot.data.IRobotLabels.BatteryCapacity;
import static rlpark.plugin.irobot.data.IRobotLabels.BatteryCharge;
import static rlpark.plugin.irobot.data.IRobotLabels.BatteryCurrent;
import static rlpark.plugin.irobot.data.IRobotLabels.BatteryTemperature;
import static rlpark.plugin.irobot.data.IRobotLabels.BatteryVoltage;
import static rlpark.plugin.irobot.data.IRobotLabels.Bump;
import static rlpark.plugin.irobot.data.IRobotLabels.Button;
import static rlpark.plugin.irobot.data.IRobotLabels.ChargingState;
import static rlpark.plugin.irobot.data.IRobotLabels.CliffSensor;
import static rlpark.plugin.irobot.data.IRobotLabels.CliffSignal;
import static rlpark.plugin.irobot.data.IRobotLabels.ConnectedHomeBase;
import static rlpark.plugin.irobot.data.IRobotLabels.ConnectedInternalCharger;
import static rlpark.plugin.irobot.data.IRobotLabels.DirtDetect;
import static rlpark.plugin.irobot.data.IRobotLabels.DriveAngle;
import static rlpark.plugin.irobot.data.IRobotLabels.DriveDistance;
import static rlpark.plugin.irobot.data.IRobotLabels.DriveRequested;
import static rlpark.plugin.irobot.data.IRobotLabels.ICLeft;
import static rlpark.plugin.irobot.data.IRobotLabels.ICOmni;
import static rlpark.plugin.irobot.data.IRobotLabels.ICRight;
import static rlpark.plugin.irobot.data.IRobotLabels.LightBumpSensor;
import static rlpark.plugin.irobot.data.IRobotLabels.LightBumpSignal;
import static rlpark.plugin.irobot.data.IRobotLabels.MotorCurrentMainBrush;
import static rlpark.plugin.irobot.data.IRobotLabels.MotorCurrentSideBrush;
import static rlpark.plugin.irobot.data.IRobotLabels.NumberStreamPackets;
import static rlpark.plugin.irobot.data.IRobotLabels.OIMode;
import static rlpark.plugin.irobot.data.IRobotLabels.SongNumber;
import static rlpark.plugin.irobot.data.IRobotLabels.SongPlaying;
import static rlpark.plugin.irobot.data.IRobotLabels.Stasis;
import static rlpark.plugin.irobot.data.IRobotLabels.WallSensor;
import static rlpark.plugin.irobot.data.IRobotLabels.WallSignal;
import static rlpark.plugin.irobot.data.IRobotLabels.WallVirtual;
import static rlpark.plugin.irobot.data.IRobotLabels.WheelDrop;
import static rlpark.plugin.irobot.data.IRobotLabels.WheelEncoder;
import static rlpark.plugin.irobot.data.IRobotLabels.WheelMotorCurrent;
import static rlpark.plugin.irobot.data.IRobotLabels.WheelRequested;
import rlpark.plugin.irobot.data.IRobotLabels;
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
    return problem.legend().hasLabel(IRobotLabels.LightBumpSensorCenterLeft);
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
