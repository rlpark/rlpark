package rlpark.alltests.rlparkview;

import org.junit.Assert;
import org.junit.Test;

import zephyr.ZephyrCore;
import zephyr.plugin.junittesting.RunnableFilesTests;
import zephyr.plugin.junittesting.support.checklisteners.ControlChecks;

public class TestOpenLogFiles {
  @Test(timeout = RunnableFilesTests.TimeOut)
  public void testCritterbotFileLoading() {
    ZephyrCore.setSynchronous(true);
    RunnableFilesTests.testFileLoading("../../rlpark/rlpark.plugin.critterbot/data/wandering.crtrlog", 100);
    Assert.assertEquals(0, ControlChecks.countChildren("zephyr.plugin.critterview.view.observation"));
  }
}
