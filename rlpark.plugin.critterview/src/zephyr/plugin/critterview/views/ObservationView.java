package zephyr.plugin.critterview.views;

import static critterbot.environment.CritterbotDrops.Accel;
import static critterbot.environment.CritterbotDrops.BusVoltage;
import static critterbot.environment.CritterbotDrops.Current;
import static critterbot.environment.CritterbotDrops.IRDistance;
import static critterbot.environment.CritterbotDrops.IRLight;
import static critterbot.environment.CritterbotDrops.Light;
import static critterbot.environment.CritterbotDrops.Mag;
import static critterbot.environment.CritterbotDrops.MicrophoneFFT;
import static critterbot.environment.CritterbotDrops.Motor;
import static critterbot.environment.CritterbotDrops.RotationVel;
import static critterbot.environment.CritterbotDrops.Speed;
import static critterbot.environment.CritterbotDrops.Temperature;
import static critterbot.environment.CritterbotDrops.Thermal;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.action.IToolBarManager;

import rltoys.environments.envio.observations.Legend;
import rltoys.utils.Utils;
import zephyr.ZephyrCore;
import zephyr.plugin.core.actions.RestartAction;
import zephyr.plugin.core.actions.TerminateAction;
import zephyr.plugin.core.api.synchronization.Chrono;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.api.synchronization.Closeable;
import zephyr.plugin.core.helpers.ClassViewProvider;
import zephyr.plugin.core.observations.EnvironmentView;
import zephyr.plugin.core.observations.ObsLayout;
import zephyr.plugin.core.observations.ObsWidget;
import zephyr.plugin.core.observations.SensorCollection;
import zephyr.plugin.core.observations.SensorGroup;
import zephyr.plugin.core.observations.SensorTextGroup;
import zephyr.plugin.core.observations.SensorTextGroup.TextClient;
import zephyr.plugin.core.views.Restartable;
import zephyr.plugin.critterview.FileHandler;
import critterbot.CritterbotProblem;
import critterbot.crtrlog.CrtrLogFile;
import critterbot.environment.CritterbotDrops;

@SuppressWarnings("synthetic-access")
public class ObservationView extends EnvironmentView<CritterbotProblem> implements Closeable, Restartable {
  static public class Provider extends ClassViewProvider {
    public Provider() {
      super(CritterbotProblem.class);
    }
  }

  protected class IntegerTextClient extends TextClient {
    private final int labelIndex;

    public IntegerTextClient(String obsLabel, String textLabel) {
      super(textLabel);
      labelIndex = legend().indexOf(obsLabel);
    }

    @Override
    public String currentText() {
      if (labelIndex < 0 || currentObservation == null)
        return "0000";
      return String.valueOf((int) currentObservation[labelIndex]);
    }
  }

  double[] currentObservation;
  private final TerminateAction terminateAction;
  private final RestartAction restartAction;

  public ObservationView() {
    terminateAction = new TerminateAction(this);
    terminateAction.setEnabled(false);
    restartAction = new RestartAction(this);
    restartAction.setEnabled(false);
  }

  public Legend legend() {
    return instance.current().legend();
  }

  @Override
  protected ObsLayout getObservationLayout() {
    SensorGroup irDistanceGroup = new SensorGroup("IR Distance Sensors", startsWith(IRDistance), 0, 255);
    SensorGroup lightGroup = new SensorGroup("Light Sensors", startsWith(Light), 0, 800);
    SensorGroup motorSpeedGroup = new SensorGroup("Speed", startsWith(Motor + Speed), -35, 35);
    SensorGroup motorCurrentGroup = new SensorGroup("Current", startsWith(Motor + Current), 0, 90);
    SensorGroup motorTemperatureGroup = new SensorGroup("Temperature", startsWith(Motor + Temperature), 40, 175);
    SensorCollection motorCollection = new SensorCollection("Motors", motorSpeedGroup, motorCurrentGroup,
                                                            motorTemperatureGroup);
    SensorGroup rotVelGroup = new SensorGroup("Gyroscope", startsWith(RotationVel), 0, 255);
    SensorGroup accelGroup = new SensorGroup("Accelerometers", startsWith(Accel), -2048, 2048);
    SensorGroup magGroup = new SensorGroup("Magnetometers", startsWith(Mag), -2048, 2048);
    SensorCollection inertialCollection = new SensorCollection("Inertial Sensors", rotVelGroup, accelGroup);
    SensorGroup irLightGroup = new SensorGroup("IR Light Sensors", startsWith(IRLight), 0, 255);
    SensorGroup thermalGroup = new SensorGroup("Thermal Sensors", startsWith(Thermal), 14600, 15100);
    SensorGroup leftMicrophoneGroup = new SensorGroup("Microphone Left", startsWith(MicrophoneFFT + "Left"), 0, 80);
    SensorGroup rightMicrophoneGroup = new SensorGroup("Microphone Right", startsWith(MicrophoneFFT + "Right"), 0, 80);
    SensorTextGroup infoGroup = createInfoGroup();
    return new ObsLayout(new ObsWidget[][] { { infoGroup, irDistanceGroup, lightGroup },
        { magGroup, motorCollection, inertialCollection }, { irLightGroup, thermalGroup },
        { leftMicrophoneGroup, rightMicrophoneGroup } });
  }

  @Override
  protected void setToolbar(IToolBarManager toolBarManager) {
    toolBarManager.add(restartAction);
    toolBarManager.add(terminateAction);
  }

  private SensorTextGroup createInfoGroup() {
    TextClient busVoltageTextClient = new TextClient("Voltage:") {
      int busVoltageIndex = legend().indexOf(BusVoltage);

      @Override
      public String currentText() {
        if (currentObservation == null)
          return "00.0V";
        return String.format("%.1fV", currentObservation[busVoltageIndex] / 10.0);
      }
    };
    TextClient loopTimeTextClient = new TextClient("Loop Time:") {
      @Override
      public String currentText() {
        Clock clock = instance.clock();
        if (clock == null)
          return "00ms";
        return Chrono.toPeriodString(clock.lastPeriodNano());
      }
    };
    TextClient cycleTimeTextClient = new TextClient("Cycle Time:") {
      int cycleTimeIndex = instance.current().legend().indexOf(CritterbotDrops.CycleTime);

      @Override
      public String currentText() {
        if (currentObservation == null)
          return "00%";
        return String.valueOf((int) currentObservation[cycleTimeIndex]) + "%";
      }
    };
    new IntegerTextClient(CritterbotDrops.CycleTime, "");
    return new SensorTextGroup("Info", busVoltageTextClient, loopTimeTextClient, cycleTimeTextClient,
                               new IntegerTextClient(CritterbotDrops.PowerSource, "Power Source"),
                               new IntegerTextClient(CritterbotDrops.ChargeState, "Charge State"),
                               new IntegerTextClient(CritterbotDrops.MonitorState, "Monitor State"),
                               new IntegerTextClient(CritterbotDrops.ErrorFlags, "Error Flag"));
  }

  private int[] startsWith(String prefix) {
    List<Integer> indexes = new ArrayList<Integer>();
    for (Map.Entry<String, Integer> entry : legend().legend().entrySet())
      if (entry.getKey().startsWith(prefix))
        indexes.add(entry.getValue());
    Collections.sort(indexes);
    return Utils.asIntArray(indexes);
  }

  private void setViewTitle() {
    CritterbotProblem problem = instance.current();
    if (problem == null)
      setViewName("Observation", "");
    CrtrLogFile logFile = problem instanceof CrtrLogFile ? (CrtrLogFile) problem : null;
    String viewTitle = logFile == null ? problem.getClass().getSimpleName() : new File(logFile.filepath).getName();
    String tooltip = logFile == null ? "" : logFile.filepath;
    setViewName(viewTitle, tooltip);
  }

  @Override
  public void restart() {
    CritterbotProblem problem = instance.current();
    if (!(problem instanceof CrtrLogFile))
      return;
    final String filepath = ((CrtrLogFile) problem).filepath;
    close();
    ZephyrCore.start(new Runnable() {
      @Override
      public void run() {
        FileHandler.handle(filepath);
      }
    });
  }

  @Override
  public void dispose() {
    close();
    super.dispose();
  }

  @Override
  protected boolean isInstanceSupported(Object instance) {
    return CritterbotProblem.class.isInstance(instance);
  }

  @Override
  protected void setLayout() {
    super.setLayout();
    restartAction.setEnabled(instance.current() instanceof CrtrLogFile);
    terminateAction.setEnabled(true);
    setViewTitle();
  }

  @Override
  protected boolean synchronize() {
    currentObservation = instance.current().lastReceivedObs();
    synchronize(currentObservation);
    return true;
  }

  @Override
  public void close() {
    instance.unset();
  }

  @Override
  protected void unsetLayout() {
    super.unsetLayout();
    restartAction.setEnabled(false);
    terminateAction.setEnabled(false);
  }
}
