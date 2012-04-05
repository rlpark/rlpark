package rlpark.alltests.rlparkview.continuousgridworld;

import java.util.Random;

import rlpark.plugin.rltoys.envio.actions.ActionArray;
import rlpark.plugin.rltoys.envio.rl.TRStep;
import rlpark.plugin.rltoys.math.ranges.Range;
import rlpark.plugin.rltoys.problems.continuousgridworld.ContinuousGridworld;
import zephyr.plugin.core.api.Zephyr;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.api.viewable.ContinuousFunction;

@Monitor
public class TestContinuousGridworldRunnable implements Runnable {
  static final Range ObservationRange = new Range(-10, 10);
  private final Clock clock = new Clock("TestContinuousGridworld");
  private final ContinuousGridworld continuousGridworld;
  private final Random random = new Random(0);

  public TestContinuousGridworldRunnable() {
    continuousGridworld = new ContinuousGridworld(random, 2, ObservationRange, new Range(-1, 1), .1);
    continuousGridworld.setRewardFunction(new ContinuousFunction() {
      @Override
      public double value(double[] position) {
        return position[0] + position[1];
      }
    });
    Zephyr.advertise(clock, this);
  }

  @Override
  public void run() {
    TRStep step = continuousGridworld.initialize();
    Range[] actionRanges = continuousGridworld.actionRanges();
    while (clock.tick()) {
      if (step.isEpisodeEnding()) {
        step = continuousGridworld.initialize();
        continue;
      }
      step = continuousGridworld.step(new ActionArray(actionRanges[0].choose(random), actionRanges[1].choose(random)));
    }
  }
}
