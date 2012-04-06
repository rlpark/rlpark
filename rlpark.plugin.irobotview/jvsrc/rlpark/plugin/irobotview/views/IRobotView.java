package rlpark.plugin.irobotview.views;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.action.IToolBarManager;

import rlpark.plugin.irobot.logfiles.IRobotLogFile;
import rlpark.plugin.irobotview.filehandlers.IRobotLogFileHandler;
import rlpark.plugin.rltoys.envio.observations.Legend;
import rlpark.plugin.rltoys.utils.Utils;
import rlpark.plugin.robot.helpers.Robots;
import rlpark.plugin.robot.interfaces.RobotLive;
import zephyr.plugin.core.ZephyrCore;
import zephyr.plugin.core.api.synchronization.Closeable;
import zephyr.plugin.core.internal.actions.RestartAction;
import zephyr.plugin.core.internal.actions.TerminateAction;
import zephyr.plugin.core.internal.helpers.ClassViewProvider;
import zephyr.plugin.core.internal.observations.EnvironmentView;
import zephyr.plugin.core.internal.observations.SensorGroup;
import zephyr.plugin.core.internal.observations.SensorTextGroup.TextClient;
import zephyr.plugin.core.internal.views.Restartable;

@SuppressWarnings("restriction")
public abstract class IRobotView extends EnvironmentView<RobotLive> implements Closeable, Restartable {
  static abstract public class IRobotViewProvider extends ClassViewProvider {
    public IRobotViewProvider() {
      super(RobotLive.class);
    }
  }

  protected class IntegerTextClient extends TextClient {
    private final String defaultString;
    private final int labelIndex;
    private final String suffix;

    public IntegerTextClient(String obsLabel, String textLabel) {
      this(obsLabel, textLabel, "0");
    }

    public IntegerTextClient(String obsLabel, String textLabel, String defaultString) {
      this(obsLabel, textLabel, "0", "");
    }

    @SuppressWarnings("synthetic-access")
    public IntegerTextClient(String obsLabel, String textLabel, String defaultString, String suffix) {
      super(textLabel);
      labelIndex = instance.current().legend().indexOf(obsLabel);
      this.defaultString = defaultString;
      this.suffix = suffix;
      assert labelIndex >= 0;
    }

    @Override
    public String currentText() {
      if (currentObservation == null)
        return defaultString + suffix;
      return String.valueOf((int) currentObservation[labelIndex]) + suffix;
    }
  }

  protected double[] currentObservation;
  private final TerminateAction terminateAction;
  private final RestartAction restartAction;

  public IRobotView() {
    terminateAction = new TerminateAction(this);
    terminateAction.setEnabled(false);
    restartAction = new RestartAction(this);
    restartAction.setEnabled(false);
  }

  @Override
  protected void setToolbar(IToolBarManager toolBarManager) {
    toolBarManager.add(restartAction);
    toolBarManager.add(terminateAction);
  }

  protected SensorGroup createSensorGroup(String title, String prefix) {
    return new SensorGroup(title, startsWith(prefix));
  }

  private int[] startsWith(String prefix) {
    List<Integer> result = new ArrayList<Integer>();
    for (Map.Entry<String, Integer> entry : legend().legend().entrySet()) {
      String label = entry.getKey();
      if (label.startsWith(prefix))
        result.add(entry.getValue());
    }
    Collections.sort(result);
    return Utils.asIntArray(result);
  }

  protected Legend legend() {
    return instance.current().legend();
  }

  @Override
  public boolean synchronize() {
    currentObservation = Robots.toDoubles(instance.current().lastReceivedRawObs());
    synchronize(currentObservation);
    return true;
  }

  @Override
  protected void setLayout() {
    super.setLayout();
    restartAction.setEnabled(instance.current() instanceof IRobotLogFile);
    terminateAction.setEnabled(true);
    setViewTitle();
  }

  private void setViewTitle() {
    RobotLive robot = instance.current();
    if (robot == null) {
      setViewName("Observation", "");
      return;
    }
    IRobotLogFile logFile = robot instanceof IRobotLogFile ? (IRobotLogFile) robot : null;
    String viewTitle = logFile == null ? robot.getClass().getSimpleName() : new File(logFile.filepath()).getName();
    String tooltip = logFile == null ? "" : logFile.filepath();
    setViewName(viewTitle, tooltip);
  }

  @Override
  protected void unsetLayout() {
    super.unsetLayout();
    restartAction.setEnabled(false);
    terminateAction.setEnabled(false);
  }

  @Override
  public void restart() {
    if (!(instance.current() instanceof IRobotLogFile))
      return;
    final String filepath = ((IRobotLogFile) instance.current()).filepath();
    close();
    ZephyrCore.start(new Runnable() {
      @Override
      public void run() {
        IRobotLogFileHandler.handle(filepath);
      }
    });
  }

  @Override
  public void close() {
    instance.unset();
  }
}