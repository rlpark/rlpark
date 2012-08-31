package rlpark.plugin.rltoys.junit.experiments.parametersweep;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import rlpark.plugin.rltoys.experiments.helpers.ExperimentCounter;
import rlpark.plugin.rltoys.experiments.parametersweep.SweepAll;
import rlpark.plugin.rltoys.experiments.parametersweep.internal.ParametersLogFileReader;
import rlpark.plugin.rltoys.experiments.parametersweep.parameters.FrozenParameters;
import rlpark.plugin.rltoys.experiments.parametersweep.parameters.Parameters;
import rlpark.plugin.rltoys.experiments.parametersweep.parameters.RunInfo;
import rlpark.plugin.rltoys.experiments.scheduling.interfaces.Scheduler;
import rlpark.plugin.rltoys.experiments.scheduling.network.ServerScheduler;
import rlpark.plugin.rltoys.experiments.scheduling.schedulers.LocalScheduler;
import rlpark.plugin.rltoys.junit.experiments.scheduling.SchedulerTest;
import rlpark.plugin.rltoys.junit.experiments.scheduling.SchedulerTestsUtils;
import rlpark.plugin.rltoys.junit.experiments.scheduling.UnreliableNetworkClientTest;

public class SweepTest {
  private static final String JUnitFolder = ".junittests_parametersweep";
  private static final int NbRun = 3;

  interface SchedulerManager {
    Scheduler newScheduler();

    void startClients();
  }

  @BeforeClass
  static public void setup() {
    SchedulerTest.junitMode();
    SweepAll.disableVerbose();
    try {
      FileUtils.deleteDirectory(new File(JUnitFolder));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testSweepLocalScheduler() throws IOException {
    testSweep(new SchedulerManager() {

      @Override
      public Scheduler newScheduler() {
        return new LocalScheduler();
      }

      @Override
      public void startClients() {
      }
    });
  }

  @Test(timeout = SchedulerTest.Timeout)
  public void testSweepNetworkScheduler() throws IOException {
    testSweep(new SchedulerManager() {

      @Override
      public Scheduler newScheduler() {
        ServerScheduler scheduler = null;
        try {
          scheduler = new ServerScheduler(SchedulerTestsUtils.Port, 0);
          // Need to fix this
        } catch (IOException e) {
          e.printStackTrace();
        }
        return scheduler;
      }

      @Override
      public void startClients() {
        UnreliableNetworkClientTest.startUnreliableClients(4, true);
      }
    });
  }

  private void testSweep(SchedulerManager schedulerManager) throws IOException {
    int nbParameters = 4;
    int nbValuesFirstSweep = 5;
    int nbValuesSecondSweep = 6;
    FileUtils.deleteDirectory(new File(JUnitFolder));
    Assert.assertFalse(checkFile(nbValuesSecondSweep, nbParameters));
    int nbJobs = runFullSweep(schedulerManager, nbValuesFirstSweep, nbParameters);
    Assert.assertEquals((int) Math.pow(nbValuesFirstSweep, nbParameters) * NbRun, nbJobs);
    nbJobs = runFullSweep(schedulerManager, nbValuesSecondSweep, nbParameters);
    final int nbJobsPerRun = (int) (Math.pow(nbValuesSecondSweep, nbParameters) - Math.pow(nbValuesFirstSweep,
                                                                                           nbParameters));
    Assert.assertEquals(nbJobsPerRun * NbRun, nbJobs);
    Assert.assertTrue(checkFile(nbValuesSecondSweep, nbParameters));
    nbJobs = runFullSweep(schedulerManager, nbValuesSecondSweep, nbParameters);
    Assert.assertEquals(0, nbJobs);
    Assert.assertTrue(checkFile(nbValuesSecondSweep, nbParameters));
  }

  private boolean checkFile(int nbValues, int nbParameters) {
    for (int runIndex = 0; runIndex < NbRun; runIndex++) {
      File dataFile = new File(String.format(JUnitFolder + "/" + ProviderTest.ContextPath + "/data%02d.logtxt",
                                             runIndex));
      if (!dataFile.canRead())
        return false;
      RunInfo infos = ProviderTest.createRunInfo();
      ParametersLogFileReader logFile = new ParametersLogFileReader(dataFile.getAbsolutePath());
      List<FrozenParameters> doneParameters = logFile
          .extractParameters(ProviderTest.getParameterLabels(nbParameters));
      List<Parameters> todoParameters = ProviderTest.createParameters(nbValues, nbParameters);
      if (doneParameters.size() != todoParameters.size())
        return false;
      for (int i = 0; i < todoParameters.size(); i++) {
        FrozenParameters doneParameter = doneParameters.get(i);
        Assert.assertEquals(infos, doneParameter.infos());
        Parameters todoParameter = todoParameters.get(i);
        Assert.assertEquals(todoParameter.infos(), doneParameter.infos());
        Assert.assertTrue(todoParameter.compareTo(doneParameter) == 0);
        if (!ProviderTest.parametersHasBeenDone(doneParameter))
          return false;
      }
    }
    return true;
  }

  private int runFullSweep(SchedulerManager schedulerManager, int nbValues, int nbParameters) {
    ProviderTest provider = new ProviderTest(nbValues, nbParameters);
    ExperimentCounter counter = new ExperimentCounter(NbRun, JUnitFolder);
    Scheduler scheduler = schedulerManager.newScheduler();
    SweepAll sweep = new SweepAll(scheduler);
    sweep.submitSweep(provider, counter);
    sweep.startScheduler();
    schedulerManager.startClients();
    sweep.waitAll();
    sweep.dispose();
    return sweep.nbJobs();
  }
}
