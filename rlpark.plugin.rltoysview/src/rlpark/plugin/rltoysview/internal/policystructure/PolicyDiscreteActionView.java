package rlpark.plugin.rltoysview.internal.policystructure;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;

import rltoys.algorithms.learning.control.acting.Greedy;
import rltoys.algorithms.representations.acting.DiscreteActionPolicy;
import rltoys.algorithms.representations.actions.Action;
import zephyr.plugin.core.api.labels.Labels;
import zephyr.plugin.core.internal.helpers.ClassViewProvider;
import zephyr.plugin.core.internal.utils.Colors;
import zephyr.plugin.core.internal.views.helpers.ForegroundCanvasView;
import zephyr.plugin.plotting.internal.bar2d.Bar2D;
import zephyr.plugin.plotting.internal.bar2d.BarColorMap;
import zephyr.plugin.plotting.internal.mousesearch.MouseSearch;

@SuppressWarnings("restriction")
public class PolicyDiscreteActionView extends ForegroundCanvasView<DiscreteActionPolicy> {
  public static class Provider extends ClassViewProvider {
    public Provider() {
      super(DiscreteActionPolicy.class);
    }
  }

  Action bestAction;
  Action[] actions;
  private double[] values;
  private final Bar2D bar = new Bar2D();
  private MouseSearch mouseSearch;
  private final BarColorMap barColorMap = new BarColorMap() {
    @Override
    public RGB toColor(int x, double value) {
      if (actions[x] == bestAction)
        return Colors.COLOR_MAGENTA;
      return value > 0 ? Colors.COLOR_RED : Colors.COLOR_BLUE;
    }
  };

  @Override
  public void createPartControl(final Composite parent) {
    super.createPartControl(parent);
    mouseSearch = new MouseSearch(bar.dataBuffer(), canvas());
    bar.setColorMap(barColorMap);
  }

  @Override
  public boolean synchronize() {
    DiscreteActionPolicy current = instance.current();
    System.arraycopy(current.values(), 0, values, 0, values.length);
    bestAction = (current instanceof Greedy) ? ((Greedy) current).bestAction() : null;
    return true;
  }

  @Override
  protected void paint(GC gc) {
    bar.clear(gc);
    bar.draw(gc, values);
  }

  @Override
  public void onInstanceSet() {
    super.onInstanceSet();
    DiscreteActionPolicy current = instance.current();
    actions = current.actions();
    values = new double[actions.length];
    String[] labels = new String[actions.length];
    for (int i = 0; i < labels.length; i++)
      labels[i] = Labels.label(actions[i]);
    bar.dataBuffer().setLabels(labels);
  }

  @Override
  public void onInstanceUnset() {
    super.onInstanceUnset();
    actions = null;
    values = null;
  }

  @Override
  protected boolean isInstanceSupported(Object instance) {
    return DiscreteActionPolicy.class.isInstance(instance);
  }

  @Override
  public void dispose() {
    mouseSearch.dispose();
    bar.dispose();
    super.dispose();
  }
}
