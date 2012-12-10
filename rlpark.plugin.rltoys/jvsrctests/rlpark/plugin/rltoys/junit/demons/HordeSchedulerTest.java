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

    @Override
    public void update(RealVector x_t, Action a_t, RealVector x_tp1) {
      this.x_t = x_t;
      this.a_t = a_t;
      this.x_tp1 = x_tp1;
    }

    @Override
    public LinearLearner learner() {
      return null;
    }
  }

  static class FakeFunction implements HordeUpdatable {
    RealVector x_tp1;
    Action a_t;
    RealVector x_t;
    Observation o_tp1;

    @Override
    public void update(Observation o_tp1, RealVector x_t, Action a_t, RealVector x_tp1) {
      this.o_tp1 = o_tp1;
      this.x_t = x_t;
      this.a_t = a_t;
      this.x_tp1 = x_tp1;
    }
  }

  @Test
  public void testScheduler() {
    Observation o_tp1 = new Observation() {
    };
    FakeFunction f1 = new FakeFunction(), f2 = new FakeFunction();
    final List<FakeFunction> functions = Utils.asList(f1, f2);
    FakeDemon d1 = new FakeDemon(), d2 = new FakeDemon();
    final List<FakeDemon> demons = Utils.asList(d1, d2);
    Horde horde = new Horde(new HordeScheduler(3), demons, functions);
    final RealVector x0 = new BVector(1), x1 = new BVector(1);
    final Action a0 = new Action() {
    };
    horde.update(o_tp1, x0, a0, x1);
    Assert.assertEquals(f1.o_tp1, o_tp1);
    Assert.assertEquals(f1.x_t, x0);
    Assert.assertEquals(f1.a_t, a0);
    Assert.assertEquals(f1.x_tp1, x1);
    Assert.assertEquals(f2.o_tp1, o_tp1);
    Assert.assertEquals(f2.x_t, x0);
    Assert.assertEquals(f2.a_t, a0);
    Assert.assertEquals(f2.x_tp1, x1);
    Assert.assertEquals(d1.x_t, x0);
    Assert.assertEquals(d1.a_t, a0);
    Assert.assertEquals(d1.x_tp1, x1);
    Assert.assertEquals(d2.x_t, x0);
    Assert.assertEquals(d2.a_t, a0);
    Assert.assertEquals(d2.x_tp1, x1);
  }
}
