package rlpark.plugin.rltoys.junit.demons;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import rlpark.plugin.rltoys.algorithms.LinearLearner;
import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.observations.Observation;
import rlpark.plugin.rltoys.horde.Horde;
import rlpark.plugin.rltoys.horde.HordeScheduler;
import rlpark.plugin.rltoys.horde.demons.Demon;
import rlpark.plugin.rltoys.horde.functions.HordeUpdatable;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.math.vector.implementations.BVector;
import rlpark.plugin.rltoys.utils.Utils;

@SuppressWarnings("serial")
public class HordeSchedulerTest {
  static class FakeDemon implements Demon {
    RealVector x_tp1;
    Action a_t;
    RealVector x_t;
    public boolean updated = false;

    @Override
    public void update(RealVector x_t, Action a_t, RealVector x_tp1) {
      this.x_t = x_t;
      this.a_t = a_t;
      this.x_tp1 = x_tp1;
      updated = true;
    }

    @Override
    public LinearLearner learner() {
      return null;
    }
  }

  static class FakeFunction implements HordeUpdatable {
    RealVector x_tp1 = null;
    Action a_t = null;
    RealVector x_t = null;
    Observation o_tp1 = null;
    boolean demonState;
    final FakeDemon fakeDemon;

    public FakeFunction(FakeDemon fakeDemon) {
      this.fakeDemon = fakeDemon;
      demonState = fakeDemon.updated;
    }

    @Override
    public void update(Observation o_tp1, RealVector x_t, Action a_t, RealVector x_tp1) {
      this.o_tp1 = o_tp1;
      this.x_t = x_t;
      this.a_t = a_t;
      this.x_tp1 = x_tp1;
      this.demonState = fakeDemon.updated;
    }
  }

  @Test
  public void testScheduler() {
    Observation o_tp1 = new Observation() {
    };
    FakeDemon d1 = new FakeDemon(), d2 = new FakeDemon();
    final List<FakeDemon> demons = Utils.asList(d1, d2);
    final FakeFunction[] beforeFunctions = { new FakeFunction(d1), new FakeFunction(d2) };
    final FakeFunction[] afterFunctions = { new FakeFunction(d1), new FakeFunction(d2) };
    Horde horde = new Horde(new HordeScheduler(3));
    horde.beforeFunctions().addAll(Utils.asList(beforeFunctions));
    horde.demons().addAll(demons);
    horde.afterFunctions().addAll(Utils.asList(afterFunctions));
    final RealVector x0 = new BVector(1), x1 = new BVector(1);
    final Action a0 = new Action() {
    };
    checkFunction(beforeFunctions, null, null, null, null, false);
    checkFunction(afterFunctions, null, null, null, null, false);
    horde.update(o_tp1, x0, a0, x1);
    checkFunction(beforeFunctions, x0, a0, o_tp1, x1, false);
    checkFunction(afterFunctions, x0, a0, o_tp1, x1, true);
    checkDemon(d1, x0, a0, x1);
    checkDemon(d2, x0, a0, x1);
  }

  public void checkDemon(FakeDemon d, final RealVector x_t, final Action a_t, final RealVector x_tp1) {
    Assert.assertEquals(d.x_t, x_t);
    Assert.assertEquals(d.a_t, a_t);
    Assert.assertEquals(d.x_tp1, x_tp1);
  }

  public void checkFunction(FakeFunction[] fs, final RealVector x_t, final Action a_t, Observation o_tp1,
      final RealVector x_tp1, boolean state) {
    for (FakeFunction f : fs) {
      Assert.assertEquals(f.o_tp1, o_tp1);
      Assert.assertEquals(f.x_t, x_t);
      Assert.assertEquals(f.a_t, a_t);
      Assert.assertEquals(f.x_tp1, x_tp1);
      Assert.assertEquals(f.demonState, state);
    }
  }
}
