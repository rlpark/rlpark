package rlpark.plugin.rltoys.junit.experiments.predictions.onpolicytd;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import rlpark.plugin.rltoys.experiments.helpers.ExperimentCounter;
import rlpark.plugin.rltoys.experiments.parametersweep.SweepAll;
import rlpark.plugin.rltoys.experiments.parametersweep.interfaces.Context;
import rlpark.plugin.rltoys.experiments.parametersweep.interfaces.SweepDescriptor;
import rlpark.plugin.rltoys.experiments.parametersweep.internal.ParametersLogFileReader;
import rlpark.plugin.rltoys.experiments.parametersweep.parameters.FrozenParameters;
import rlpark.plugin.rltoys.experiments.parametersweep.parameters.Parameters;
import rlpark.plugin.rltoys.experiments.parametersweep.prediction.PredictionLearnerFactory;
import rlpark.plugin.rltoys.experiments.parametersweep.prediction.PredictionParameters;
import rlpark.plugin.rltoys.experiments.parametersweep.prediction.PredictionProblemFactory;
import rlpark.plugin.rltoys.experiments.parametersweep.prediction.PredictionSweepDescriptor;
import rlpark.plugin.rltoys.experiments.parametersweep.prediction.onpolicy.OnPolicyTDErrorMonitor;
import rlpark.plugin.rltoys.experiments.parametersweep.prediction.onpolicy.OnPolicyTDSweepDescriptor;
import rlpark.plugin.rltoys.experiments.scheduling.schedulers.LocalScheduler;
import rlpark.plugin.rltoys.junit.experiments.scheduling.SchedulerTest;

public class OnPolicyTDSweepTest {
  static private final PredictionProblemFactory[] problemFactories = new PredictionProblemFactory[] {
      new OnPolicyTDProblemFactoryJUnit(.99, 10.0), new OnPolicyTDProblemFactoryJUnit(.8, 100.0) };
  protected static final String JUnitFolder = ".junittests_tdonpolicysweep";
  protected static final int NbRun = 4;
  protected SweepAll sweep = null;
  private PredictionLearnerFactory[] learnerFactories;

  @Before
  public void before() throws IOException {
    learnerFactories = new PredictionLearnerFactory[] { new OnPolicyTDLearnerFactoryJUnit() };
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
      Assert.assertEquals(expected, value, 0.0);
  }

  protected void checkFile(PredictionSweepDescriptor descriptor, boolean diverged) {
    List<? extends Context> contexts = descriptor.provideContexts();
    for (Context context : contexts) {
      String testFolder = context.folderPath();
      List<Parameters> parameters = descriptor.provideParameters(context);
      String[] parameterLabels = parameters.get(0).labels();
      checkFile(testFolder, diverged, parameterLabels);
    }
  }

  private void checkFile(String testFolder, boolean diverged, String[] parameterLabels) {
    ParametersLogFileReader logFile = null;
    for (int i = 0; i < NbRun; i++) {
      String filename = String.format("data%02d.logtxt", i);
      File dataFile = new File(String.format("%s/%s/%s", JUnitFolder, testFolder, filename));
      if (!dataFile.canRead())
        Assert.fail("Cannot read " + dataFile.getAbsolutePath());
      logFile = new ParametersLogFileReader(dataFile.getAbsolutePath());
      List<FrozenParameters> parametersList = logFile.extractParameters(parameterLabels);
      for (FrozenParameters parameters : parametersList)
        checkParameters(parameters, diverged);
    }
  }

  protected void checkParameters(FrozenParameters parameters, boolean diverged) {
    for (int i = 0; i < PredictionParameters.nbPerformanceCheckpoint(parameters); i++) {
      double mse = parameters.get(String.format("%s%s%02d", PredictionParameters.MSE,
                                                Parameters.PerformanceCumulatedMeasured, i));
      if (diverged) {
        Assert.assertEquals(Float.MAX_VALUE, mse, 0);
        return;
      }
      double gamma = parameters.get(PredictionParameters.Gamma);
      double targetValue = parameters.get(OnPolicyTDProblemFactoryJUnit.Target) / (1 - gamma);
      double predictionValue = parameters.get(OnPolicyTDLearnerFactoryJUnit.Parameter);
      double expectedMSE = (targetValue - predictionValue) * (targetValue - predictionValue);
      Assert.assertEquals(expectedMSE, mse, OnPolicyTDErrorMonitor.Precision * expectedMSE * 10);
    }
  }

  @Test
  public void testLearner() {
    PredictionSweepDescriptor descriptor = new OnPolicyTDSweepDescriptor(problemFactories, learnerFactories);
    testSweep(descriptor);
    checkFile(descriptor, false);
  }

  @Test
  public void testBadLearner01() {
    PredictionSweepDescriptor descriptor = new OnPolicyTDSweepDescriptor(problemFactories, learnerFactories);
    for (PredictionLearnerFactory learnerFactory : learnerFactories)
      ((OnPolicyTDLearnerFactoryJUnit) learnerFactory)
          .divergeAt((int) (OnPolicyTDProblemFactoryJUnit.NbLearningSteps / 2));
    testSweep(descriptor);
    checkFile(descriptor, true);
  }

  @Test
  public void testBadLearner02() {
    PredictionSweepDescriptor descriptor = new OnPolicyTDSweepDescriptor(problemFactories, learnerFactories);
    for (PredictionLearnerFactory learnerFactory : learnerFactories)
      ((OnPolicyTDLearnerFactoryJUnit) learnerFactory)
          .divergeAt((int) (OnPolicyTDProblemFactoryJUnit.NbLearningSteps + OnPolicyTDProblemFactoryJUnit.NbEvaluationSteps / 2));
    testSweep(descriptor);
    checkFile(descriptor, true);
  }
}
