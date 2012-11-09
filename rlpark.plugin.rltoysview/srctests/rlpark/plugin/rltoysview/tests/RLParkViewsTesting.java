package rlpark.plugin.rltoysview.tests;

import org.junit.Before;
import org.junit.Test;

import rlpark.plugin.rltoys.problems.mountaincar.MountainCar;
import rlpark.plugin.rltoys.problems.pendulum.SwingPendulum;
import rlpark.plugin.rltoysview.tests.internal.TestHelicopterRunnable;
import rlpark.plugin.rltoysview.tests.internal.TestMazeRunnable;
import rlpark.plugin.rltoysview.tests.internal.TestProblem;
import rlpark.plugin.rltoysview.tests.internal.TestVectorViewRunnable;
import rlpark.plugin.rltoysview.tests.internal.puddleworld.TestPuddleWorldRunnable;
import zephyr.plugin.core.RunnableFactory;
import zephyr.plugin.core.ZephyrCore;
import zephyr.plugin.junittesting.RunnableFilesTests;
import zephyr.plugin.junittesting.support.RunnableTests;
import zephyr.plugin.junittesting.support.conditions.NumberTickCondition;

public class RLParkViewsTesting {
  @Before
  public void before() {
    ZephyrCore.setSynchronous(true);
  }

  @Test(timeout = RunnableFilesTests.TimeOut)
  public void testVectorView() {
    RunnableTests.startRunnable(TestVectorViewRunnable.class, new NumberTickCondition(40));
  }

  @Test(timeout = RunnableFilesTests.TimeOut)
  public void testMazeView() {
    RunnableTests.startRunnable(TestMazeRunnable.class, new NumberTickCondition(20));
  }

  @Test(timeout = RunnableFilesTests.TimeOut)
  public void testPuddleWorldView() {
    RunnableTests.startRunnable(TestPuddleWorldRunnable.class, new NumberTickCondition(20));
  }

  @Test(timeout = RunnableFilesTests.TimeOut)
  public void testHelicopterView() {
    RunnableTests.startRunnable(TestHelicopterRunnable.class, new NumberTickCondition(50));
  }

  @Test(timeout = RunnableFilesTests.TimeOut)
  public void testMountainCarView() {
    RunnableTests.startRunnable(new RunnableFactory() {
      @Override
      public Runnable createRunnable() {
        return new TestProblem(new MountainCar(null));
      }
    }, new NumberTickCondition(50));
  }

  @Test(timeout = RunnableFilesTests.TimeOut)
  public void testPendulumView() {
    RunnableTests.startRunnable(new RunnableFactory() {
      @Override
      public Runnable createRunnable() {
        return new TestProblem(new SwingPendulum(null));
      }
    }, new NumberTickCondition(50));
  }
}
