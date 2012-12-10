package rlpark.plugin.rltoysview.tests.internal;

import java.util.Random;

import org.eclipse.swt.widgets.Display;

import rlpark.plugin.rltoys.math.vector.implementations.BVector;
import zephyr.plugin.core.api.Zephyr;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;
import zephyr.plugin.core.api.monitoring.annotations.Popup;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.junittesting.support.checklisteners.ControlChecks;

@Monitor
public class TestVectorViewRunnable implements Runnable {
  private final Random random = new Random(0);
  @Popup
  private final BVector v = new BVector(100);
  private final Clock clock = new Clock("v");

  public TestVectorViewRunnable() {
    Zephyr.advertise(clock, this);
  }

  @Override
  public void run() {
    while (clock.tick()) {
      v.clear();
      for (int i = 0; i < 20; i++)
        v.setOn(random.nextInt(v.getDimension()));
      if (clock.timeStep() == 20)
        showMapView();
    }
  }

  private void showMapView() {
    Display.getDefault().asyncExec(new Runnable() {
      @Override
      public void run() {
        ControlChecks.showView("zephyr.rlpark.plugin.rltoysview.vectormapview");
      }
    });
  }
}
