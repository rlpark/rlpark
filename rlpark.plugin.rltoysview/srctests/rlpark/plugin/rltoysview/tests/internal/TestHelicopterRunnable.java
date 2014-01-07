package rlpark.plugin.rltoysview.tests.internal;

import java.util.Random;

import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.actions.ActionArray;
import rlpark.plugin.rltoys.envio.rl.RLAgent;
import rlpark.plugin.rltoys.envio.rl.TRStep;
import rlpark.plugin.rltoys.experiments.runners.Runner;
import rlpark.plugin.rltoys.problems.helicopter.Helicopter;
import zephyr.plugin.core.api.Zephyr;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;
import zephyr.plugin.core.api.monitoring.annotations.Popup;
import zephyr.plugin.core.api.synchronization.Clock;

@Monitor
public class TestHelicopterRunnable implements Runnable, RLAgent {
  private static final long serialVersionUID = -7618574896704964858L;
  Random random = new Random(0);
  @Popup
  private final Helicopter heli = new Helicopter(random);
  private final Clock clock = new Clock("Helicopter");

  public TestHelicopterRunnable() {
    Zephyr.advertise(clock, this);
  }

  @Override
  public void run() {
    heli.initialize();
    Runner runner = new Runner(heli, this, 6000, -1);
    while (clock.tick())
      runner.step();
  }

  @Override
  public Action getAtp1(TRStep step) {
    return new ActionArray(.0, .0, random.nextDouble() * .2, .25);
  }
}
