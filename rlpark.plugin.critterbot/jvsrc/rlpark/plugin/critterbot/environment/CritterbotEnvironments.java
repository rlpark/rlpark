package rlpark.plugin.critterbot.environment;

import java.util.List;

import rlpark.plugin.critterbot.CritterbotProblem;
import rlpark.plugin.critterbot.actions.CritterbotAction;
import rlpark.plugin.critterbot.data.CritterbotLabels;
import rlpark.plugin.rltoys.envio.observations.ObsFilter;
import zephyr.plugin.core.api.monitoring.abstracts.DataMonitor;
import zephyr.plugin.core.api.monitoring.abstracts.Monitored;

public class CritterbotEnvironments {
  public static void addObservationsLogged(final CritterbotProblem problem, DataMonitor loggedManager) {
    ObsFilter filter = CritterbotLabels.newDefaultFilter(problem.legend());
    List<String> labelsToLog = filter.legend().getLabels();
    for (String label : labelsToLog) {
      final int obsIndex = problem.legend().indexOf(label);
      loggedManager.add(label, new Monitored() {
        @Override
        public double monitoredValue() {
          double[] o_t = problem.lastReceivedObs();
          if (o_t == null)
            return -1;
          return o_t[obsIndex];
        }
      });
    }
  }

  public static void addActionsLogged(final CritterbotEnvironment environment, DataMonitor loggedManager) {
    for (int i = 0; i < 3; i++) {
      String label = String.format("a[%d]", i);
      final int actionIndex = i;
      loggedManager.add(label, new Monitored() {
        @Override
        public double monitoredValue() {
          CritterbotAction a_t = environment.lastAction();
          if (a_t == null || a_t.actions == null)
            return -1;
          return a_t.actions[actionIndex];
        }
      });
    }
    loggedManager.add("ActionMode", new Monitored() {
      @Override
      public double monitoredValue() {
        CritterbotAction a_t = environment.lastAction();
        if (a_t == null)
          return -1;
        return a_t.motorMode.ordinal();
      }
    });
  }
}
