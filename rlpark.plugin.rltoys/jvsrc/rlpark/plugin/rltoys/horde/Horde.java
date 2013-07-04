package rlpark.plugin.rltoys.horde;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.observations.Observation;
import rlpark.plugin.rltoys.horde.HordeScheduler.Context;
import rlpark.plugin.rltoys.horde.demons.Demon;
import rlpark.plugin.rltoys.horde.functions.HordeUpdatable;
import rlpark.plugin.rltoys.math.vector.RealVector;
import zephyr.plugin.core.api.labels.Labels;
import zephyr.plugin.core.api.monitoring.annotations.LabelProvider;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

@Monitor
public class Horde implements Serializable {
  private static final long serialVersionUID = -437180206156496271L;
  final List<HordeUpdatable> beforeFunctions = new ArrayList<HordeUpdatable>();
  final List<Demon> demons = new ArrayList<Demon>();
  final List<HordeUpdatable> afterFunctions = new ArrayList<HordeUpdatable>();
  private final HordeScheduler scheduler;

  public Horde() {
    this(new HordeScheduler());
  }

  public Horde(HordeScheduler scheduler) {
    this.scheduler = scheduler;
  }

  @LabelProvider(ids = { "demons" })
  public String demonLabel(int i) {
    return Labels.label(demons.get(i));
  }

  @LabelProvider(ids = { "beforeFunctions" })
  public String beforeFunctionLabel(int i) {
    return Labels.label(beforeFunctions.get(i));
  }

  @LabelProvider(ids = { "afterFunctions" })
  public String afterFunctionLabel(int i) {
    return Labels.label(afterFunctions.get(i));
  }

  public void update(final Observation o_tp1, final RealVector x_t, final Action a_t, final RealVector x_tp1) {
    scheduler.update(new Context() {
      @Override
      public void updateElement(int index) {
        beforeFunctions.get(index).update(o_tp1, x_t, a_t, x_tp1);
      }

      @Override
      public int nbElements() {
        return beforeFunctions.size();
      }
    });
    scheduler.update(new Context() {
      @Override
      public void updateElement(int index) {
        demons.get(index).update(x_t, a_t, x_tp1);
      }

      @Override
      public int nbElements() {
        return demons.size();
      }
    });
    scheduler.update(new Context() {
      @Override
      public void updateElement(int index) {
        afterFunctions.get(index).update(o_tp1, x_t, a_t, x_tp1);
      }

      @Override
      public int nbElements() {
        return afterFunctions.size();
      }
    });
  }

  public List<HordeUpdatable> beforeFunctions() {
    return beforeFunctions;
  }

  public List<HordeUpdatable> afterFunctions() {
    return afterFunctions;
  }

  public List<Demon> demons() {
    return demons;
  }

  public boolean addBeforeFunction(HordeUpdatable function) {
    return beforeFunctions.add(function);
  }

  public boolean addAfterFunction(HordeUpdatable function) {
    return afterFunctions.add(function);
  }

  public boolean addDemon(Demon demon) {
    return demons.add(demon);
  }
}
