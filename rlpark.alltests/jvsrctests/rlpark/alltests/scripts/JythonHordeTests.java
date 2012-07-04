package rlpark.alltests.scripts;

import org.junit.Assert;
import org.junit.Test;

import zephyr.plugin.core.ZephyrCore;
import zephyr.plugin.junittesting.support.RunnableTests;
import zephyr.plugin.junittesting.support.checklisteners.ControlChecks;

public class JythonHordeTests {
  @Test
  public void testJythonHordeNextingTest() {
    ZephyrCore.setSynchronous(true);
    RunnableTests.testFileLoading("../rlpark.example.scripts/pysrc/DemonsPredictionOnPolicy.py", 50);
    Assert.assertEquals(0, ControlChecks.countChildren("zephyr.plugin.critterview.view.observation"));
  }

  @Test
  public void testJythonHordeOffPolicyPredictionsTest() {
    ZephyrCore.setSynchronous(true);
    RunnableTests.testFileLoading("../rlpark.example.scripts/pysrc/DemonsPredictionOffPolicy.py", 50);
    Assert.assertEquals(0, ControlChecks.countChildren("zephyr.plugin.critterview.view.observation"));
  }

  @Test
  public void testJythonHordeOffPolicyControlTest() {
    ZephyrCore.setSynchronous(true);
    RunnableTests.testFileLoading("../rlpark.example.scripts/pysrc/DemonsControlOffPolicy.py", 50);
    Assert.assertEquals(0, ControlChecks.countChildren("zephyr.plugin.critterview.view.observation"));
  }
}
