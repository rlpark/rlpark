package rlpark.plugin.rltoys.junit.experiments.reinforcementlearning;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import rlpark.plugin.rltoys.experiments.helpers.ExperimentCounter;
import rlpark.plugin.rltoys.experiments.parametersweep.SweepAll;
import rlpark.plugin.rltoys.experiments.parametersweep.interfaces.Context;
import rlpark.plugin.rltoys.experiments.parametersweep.interfaces.SweepDescriptor;
import rlpark.plugin.rltoys.experiments.parametersweep.internal.ParametersLogFileReader;
import rlpark.plugin.rltoys.experiments.parametersweep.parameters.FrozenParameters;
import rlpark.plugin.rltoys.experiments.parametersweep.parameters.Parameters;
import rlpark.plugin.rltoys.experiments.parametersweep.parameters.RunInfo;
import rlpark.plugin.rltoys.experiments.scheduling.schedulers.LocalScheduler;
import rlpark.plugin.rltoys.junit.experiments.scheduling.SchedulerTest;

public abstract class RLSweepTest {
  protected static final String JUnitFolder = ".junittests_rlparametersweep";
  protected static final int NbRun = 4;
  protected static final int NbRewardCheckPoint = 10;
  protected static final int NbTimeSteps = 100;
  protected static final int NbEpisode = 100;
  protected SweepAll sweep = null;

  @Before
  public void before() throws IOException {
    FileUtils.deleteDirectory(new File(JUnitFolder));
    SchedulerTest.junitMode();
    // Sweep.disableVerbose();
  }

  @After
  public void after() throws IOException {
    FileUtils.deleteDirectory(new File(JUnitFolder));
  }

  protected void testSweep(SweepDescriptor provider) {
    ExperimentCounter counter = new ExperimentCounter(NbRun, JUnitFolder);
    sweep = new SweepAll(new LocalScheduler(2));
    sweep.runSweep(provider, counter);
  }

  protected void assertValue(boolean diverged, double expected, double value) {
    if (diverged)
      Assert.assertEquals(-Float.MAX_VALUE, value, 0.0);
    else
      Assert.assertEquals(expected, value, 1.0);
  }

  protected List<RunInfo> checkFile(SweepDescriptor descriptor, int divergedOnSlice) {
    List<? extends Context> contexts = descriptor.provideContexts();
    List<RunInfo> infos = new ArrayList<RunInfo>();
    for (Context context : contexts) {
      String testFolder = context.folderPath();
      List<Parameters> parameters = descriptor.provideParameters(context);
      String[] parameterLabels = parameters.get(0).labels();
      infos.add(checkFile(testFolder, divergedOnSlice, parameterLabels));
    }
    return infos;
  }

  protected RunInfo checkFile(String testFolder, int divergedOnSlice, String[] parameterLabels) {
    ParametersLogFileReader logFile = null;
    for (int i = 0; i < NbRun; i++) {
      String filename = String.format("data%02d.logtxt", i);
      File dataFile = new File(String.format("%s/%s/%s", JUnitFolder, testFolder, filename));
      if (!dataFile.canRead())
        Assert.fail("Cannot read " + dataFile.getAbsolutePath());
      logFile = new ParametersLogFileReader(dataFile.getAbsolutePath());
      List<FrozenParameters> parametersList = logFile.extractParameters(parameterLabels);
      for (FrozenParameters parameters : parametersList)
        checkParameters(testFolder, filename, divergedOnSlice, parameters);
    }
    return logFile.infos();
  }

  abstract protected void checkParameters(String testFolder, String filename, int divergedOnSlice,
      FrozenParameters parameters);
}
