package rlpark.plugin.rltoysview.internal.continuousgridworld;

import rlpark.plugin.rltoys.envio.rl.TRStep;
import rlpark.plugin.rltoys.problems.continuousgridworld.ContinuousGridworld;
import zephyr.plugin.core.api.signals.Listener;
import zephyr.plugin.core.api.synchronization.Clock;

public class EpisodeTrajectories implements Listener<Clock> {
  static final private int NbEpisode = 2;
  private static final int MaximumTrajectoryLength = 10000;
  private final Trajectory[] trajectories = new Trajectory[NbEpisode];
  private int nbTrajectory = 0;
  private ContinuousGridworld current;
  private Clock clock;

  @Override
  public void listen(Clock eventInfo) {
    if (current == null)
      return;
    TRStep step = current.lastStep();
    if (step == null)
      return;
    if (trajectories[0] == null || step.o_tp1 == null) {
      System.arraycopy(trajectories, 0, trajectories, 1, trajectories.length - 1);
      trajectories[0] = new Trajectory(MaximumTrajectoryLength);
      nbTrajectory += 1;
    }
    trajectories[0].append(step.o_t, step.o_tp1);
  }

  public float[][][] copyTrajectories() {
    float[][][] result = new float[Math.min(nbTrajectory, trajectories.length)][][];
    for (int i = 0; i < result.length; i++)
      result[i] = trajectories[i].getData();
    return result;
  }

  public void connect(ContinuousGridworld current, Clock clock) {
    this.current = current;
    this.clock = clock;
    clock.onTick.connect(this);
  }

  public void disconnect() {
    clock.onTick.disconnect(this);
    this.current = null;
    this.clock = null;
  }

}
