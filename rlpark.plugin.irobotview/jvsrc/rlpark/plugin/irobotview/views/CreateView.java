package rlpark.plugin.irobotview.views;

import static rlpark.plugin.irobot.data.IRobotDrops.BatteryCapacity;
import static rlpark.plugin.irobot.data.IRobotDrops.BatteryCharge;
import static rlpark.plugin.irobot.data.IRobotDrops.BatteryCurrent;
import static rlpark.plugin.irobot.data.IRobotDrops.BatteryTemperature;
import static rlpark.plugin.irobot.data.IRobotDrops.Bump;
import static rlpark.plugin.irobot.data.IRobotDrops.Button;
import static rlpark.plugin.irobot.data.IRobotDrops.ChargingState;
import static rlpark.plugin.irobot.data.IRobotDrops.CliffSensor;
import static rlpark.plugin.irobot.data.IRobotDrops.CliffSignal;
import static rlpark.plugin.irobot.data.IRobotDrops.ConnectedHomeBase;
import static rlpark.plugin.irobot.data.IRobotDrops.ConnectedInternalCharger;
import static rlpark.plugin.irobot.data.IRobotDrops.DriveAngle;
import static rlpark.plugin.irobot.data.IRobotDrops.DriveDistance;
import static rlpark.plugin.irobot.data.IRobotDrops.ICOmni;
import static rlpark.plugin.irobot.data.IRobotDrops.NumberStreamPackets;
import static rlpark.plugin.irobot.data.IRobotDrops.OIMode;
import static rlpark.plugin.irobot.data.IRobotDrops.SongNumber;
import static rlpark.plugin.irobot.data.IRobotDrops.SongPlaying;
import static rlpark.plugin.irobot.data.IRobotDrops.WallSensor;
import static rlpark.plugin.irobot.data.IRobotDrops.WallSignal;
import static rlpark.plugin.irobot.data.IRobotDrops.WallVirtual;
import static rlpark.plugin.irobot.data.IRobotDrops.WheelDrop;
import static rlpark.plugin.irobot.data.IRobotDrops.WheelOverCurrent;
import static rlpark.plugin.irobot.data.IRobotDrops.WheelRequested;
import rlpark.plugin.irobot.data.IRobotDrops;
import rlpark.plugin.irobot.robots.CreateRobot;
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
public class CreateView extends IRobotView {
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
    return problem.legend().hasLabel(IRobotDrops.CargoBayAnalogSignal);
  }

  @Override
  protected ObsLayout getObservationLayout() {
    SensorTextGroup infoGroup = createInfoGroup();
    SensorCollection wallCollection = new SensorCollection("Walls", createSensorGroup("Virtual", WallVirtual),
                                                           createSensorGroup("Sensor", WallSensor),
                                                           createSensorGroup("Signal", WallSignal));
    SensorCollection wheelCollection = new SensorCollection("Wheels", createSensorGroup("Dropped", WheelDrop),
                                                            createSensorGroup("Requested", WheelRequested),
                                                            createSensorGroup("Over Current", WheelOverCurrent));
    SensorCollection cliffCollection = new SensorCollection("Cliffs", createSensorGroup("Sensors", CliffSensor),
                                                            createSensorGroup("Signal", CliffSignal));
    SensorCollection powerCollection = new SensorCollection("Battery", createSensorGroup("Current", BatteryCurrent),
                                                            createSensorGroup("Temperature", BatteryTemperature),
                                                            createSensorGroup("Charge", BatteryCharge),
                                                            createSensorGroup("Capacity", BatteryCapacity));
    SensorCollection odoCollection = new SensorCollection("Odometry", createSensorGroup("Distance", DriveDistance),
                                                          createSensorGroup("Angle", DriveAngle));
    return new ObsLayout(new ObsWidget[][] {
        { infoGroup, createSensorGroup("Bumper", Bump), wallCollection, cliffCollection,
            createSensorGroup("Buttons", Button) }, { wheelCollection, odoCollection, powerCollection } });
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
    return new SensorTextGroup("Info", loopTimeTextClient, new IntegerTextClient(ICOmni, "IR:"),
                               new IntegerTextClient(OIMode, "OI Mode: "), new IntegerTextClient(ChargingState,
                                                                                                 "Charging State:"),
                               new IntegerTextClient(ConnectedHomeBase, "Home base: "),
                               new IntegerTextClient(ConnectedInternalCharger, "Internal charger: "),
                               new IntegerTextClient(SongNumber, "Song: "), new IntegerTextClient(SongPlaying,
                                                                                                  "Playing: "),
                               new IntegerTextClient(NumberStreamPackets, "Packets: "));
  }

  @Override
  protected Range[] ranges() {
    return CreateRobot.getRanges();
  }

  @Override
  public boolean isSupported(CodeNode codeNode) {
    return Provider.instance.canViewDraw(codeNode);
  }

  @Override
  protected boolean isInstanceSupported(Object instance) {
    return canViewDrawInstance(instance);
  }
}
