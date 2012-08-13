package rlpark.plugin.rltoys.horde;

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
public class Horde {
  final List<HordeUpdatable> functions = new ArrayList<HordeUpdatable>();
  final List<Demon> demons = new ArrayList<Demon>();
  private final HordeScheduler scheduler;

  public Horde() {
    this(new HordeScheduler());
  }

  public Horde(HordeScheduler scheduler) {
    this(scheduler, null, null);
  }

  public Horde(List<? extends Demon> demons, List<?> functions) {
    this(new HordeScheduler(), demons, functions);
  }

  public Horde(HordeScheduler scheduler, List<? extends Demon> demons, List<?> functions) {
    this.scheduler = scheduler;
    if (demons != null)
      this.demons.addAll(demons);
    addFunctions(functions);
  }

  @LabelProvider(ids = { "demons" })
  public String demonLabel(int i) {
    return Labels.label(demons.get(i));
  }

  @LabelProvider(ids = { "functions" })
  public String functionLabel(int i) {
    return Labels.label(functions.get(i));
  }

  private void addFunctions(List<?> functions) {
    if (functions == null)
      return;
    for (Object function : functions)
      this.functions.add((HordeUpdatable) function);
  }

  public void update(final Observation o_tp1, final RealVector x_t, final Action a_t, final RealVector x_tp1) {
    scheduler.update(new Context() {
      @Override
      public void updateElement(int index) {
        functions.get(index).update(o_tp1, x_t, a_t, x_tp1);
      }

      @Override
      public int nbElements() {
        return functions.size();
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
  }

  @LabelProvider(ids = { "functions" })
  String functionsLabelOf(int index) {
    return Labels.label(functions.get(index));
  }

  @LabelProvider(ids = { "demons" })
  String demonsLabelOf(int index) {
    return Labels.label(demons.get(index));
  }

  public List<HordeUpdatable> functions() {
    return functions;
  }

  public List<Demon> demons() {
    return demons;
  }

  public boolean addFunction(HordeUpdatable function) {
    return functions.add(function);
  }

  public boolean addDemon(Demon demon) {
    return demons.add(demon);
  }
}
