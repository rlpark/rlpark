package rlpark.plugin.rltoys.envio.rl;

import java.io.Serializable;
import java.util.Arrays;

import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.observations.ObsAsDoubles;
import rlpark.plugin.rltoys.utils.Utils;

public class TRStep implements ObsAsDoubles, Serializable {
  private static final long serialVersionUID = 5694217784539677187L;
  // Time of o_tp1
  final public long time;
  final public double[] o_t;
  final public Action a_t;
  final public double[] o_tp1;
  final public double r_tp1;
  public final boolean endEpisode;

  public TRStep(double[] o_tp1, double reward) {
    this(0, null, null, o_tp1, reward, false);
  }

  public TRStep(TRStep step_t, Action a_t, double[] o_tp1, double r_tp1) {
    this(step_t.time + 1, step_t.o_tp1, a_t, o_tp1, r_tp1, false);
  }

  public TRStep(long time, double[] o_t, Action a_t, double[] o_tp1, double r_tp1, boolean endEpisode) {
    this.time = time;
    this.endEpisode = endEpisode;
    assert (o_t == null && a_t == null) || (o_t != null && a_t != null);
    this.o_t = o_t == null ? null : o_t.clone();
    this.a_t = a_t;
    assert o_tp1 != null;
    this.o_tp1 = o_tp1.clone();
    assert Utils.checkValue(r_tp1);
    this.r_tp1 = r_tp1;
  }

  public TRStep createEndingStep() {
    return new TRStep(time, o_t, a_t, o_tp1, r_tp1, true);
  }

  @Override
  public String toString() {
    return String.format("T=%d: %s,%s->%s,r=%f", time, Arrays.toString(o_t), a_t, Arrays.toString(o_tp1), r_tp1);
  }

  public boolean isEpisodeStarting() {
    return o_t == null;
  }


  public boolean isEpisodeEnding() {
    return endEpisode;
  }

  @Override
  public double[] doubleValues() {
    return o_tp1;
  }
}
