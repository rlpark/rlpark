package rlpark.alltests.rlparkview;

import org.junit.Assert;
import org.junit.Test;

import zephyr.ZephyrCore;
import zephyr.plugin.junittesting.RunnableFilesTests;
import zephyr.plugin.junittesting.support.RunnableTests;
import zephyr.plugin.junittesting.support.checklisteners.ControlChecks;
import zephyr.plugin.junittesting.support.conditions.NumberTickCondition;

public class RLParkViewsTesting {
  @Test(timeout = RunnableFilesTests.TimeOut)
  public void testCritterbotFileLoading() {
    ZephyrCore.setSynchronous(true);
    RunnableTests.testFileLoading("../../rlpark/rlpark.plugin.critterbot/data/wandering.crtrlog", 100);
    Assert.assertEquals(0, ControlChecks.countChildren("zephyr.plugin.critterview.view.observation"));
  }

  @Test(timeout = RunnableFilesTests.TimeOut)
  public void testContinuousGridworldView() {
    ZephyrCore.setSynchronous(true);
    RunnableTests.startRunnable("rlpark.alltests.testcontinuousgridworld.runnable", new NumberTickCondition(50));
  }
}
