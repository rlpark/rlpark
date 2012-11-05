package rlpark.plugin.rltoysview.tests.internal.puddleworld;

import java.util.Random;

import rlpark.plugin.rltoys.algorithms.functions.ContinuousFunction;
import rlpark.plugin.rltoys.envio.actions.ActionArray;
import rlpark.plugin.rltoys.envio.rl.TRStep;
import rlpark.plugin.rltoys.math.ranges.Range;
import rlpark.plugin.rltoys.problems.puddleworld.PuddleWorld;
import zephyr.plugin.core.api.Zephyr;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;
import zephyr.plugin.core.api.monitoring.annotations.Popup;
import zephyr.plugin.core.api.synchronization.Clock;

@SuppressWarnings("restriction")
@Monitor
public class TestPuddleWorldRunnable implements Runnable {
  static final Range ObservationRange = new Range(-10, 10);
  private final Clock clock = new Clock("TestPuddleWorld");
  @Popup
  private final PuddleWorld puddleWorld;
  private final Random random = new Random(0);

  public TestPuddleWorldRunnable() {
    puddleWorld = new PuddleWorld(random, 2, ObservationRange, new Range(-1, 1), .1);
    puddleWorld.setRewardFunction(new ContinuousFunction() {
      @Override
      public double value(double[] input) {
        return input[0] + input[1];
      }
    });
    Zephyr.advertise(clock, this);
  }

  @Override
  public void run() {
    TRStep step = puddleWorld.initialize();
    Range[] actionRanges = puddleWorld.actionRanges();
    while (clock.tick()) {
      if (step.isEpisodeEnding()) {
        step = puddleWorld.initialize();
        continue;
      }
      step = puddleWorld.step(new ActionArray(actionRanges[0].choose(random), actionRanges[1].choose(random)));
    }
  }
}
