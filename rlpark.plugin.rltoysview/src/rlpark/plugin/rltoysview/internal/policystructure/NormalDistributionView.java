package rlpark.plugin.rltoysview.internal.policystructure;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;

import rlpark.plugin.rltoys.algorithms.LinearLearner;
import rlpark.plugin.rltoys.algorithms.control.actorcritic.onpolicy.AbstractActorCritic;
import rlpark.plugin.rltoys.algorithms.control.actorcritic.onpolicy.ActorCritic;
import rlpark.plugin.rltoys.algorithms.functions.policydistributions.structures.AbstractNormalDistribution;
import rlpark.plugin.rltoys.math.History;
import rlpark.plugin.rltoys.math.normalization.MinMaxNormalizer;
import rlpark.plugin.rltoys.math.ranges.Range;
import zephyr.ZephyrPlotting;
import zephyr.plugin.core.api.internal.codeparser.codetree.ClassNode;
import zephyr.plugin.core.api.internal.codeparser.codetree.CodeTrees;
import zephyr.plugin.core.api.internal.codeparser.interfaces.CodeNode;
import zephyr.plugin.core.api.signals.Listener;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.internal.helpers.ClassViewProvider;
import zephyr.plugin.core.internal.utils.Colors;
import zephyr.plugin.plotting.internal.data.Data2D;
import zephyr.plugin.plotting.internal.plot2d.Plot2DView;
import zephyr.plugin.plotting.internal.plot2d.drawer2d.Drawers;

@SuppressWarnings("restriction")
public class NormalDistributionView extends Plot2DView<AbstractNormalDistribution> {
  static final Class<AbstractNormalDistribution> SupportedClass = AbstractNormalDistribution.class;

  public static class Provider extends ClassViewProvider {
    public Provider() {
      super(SupportedClass);
    }
  }

  class ClockListener implements Listener<Clock> {
    private final AbstractNormalDistribution distribution;

    ClockListener(AbstractNormalDistribution distribution) {
      this.distribution = distribution;
    }

    @Override
    public void listen(Clock eventInfo) {
      updateData(distribution);
    }

  }

  public static final int HistoryLength = 1000;
  private final static String ActionFlagKey = "ActionFlagKey";

  private NormalDistributionDrawer normalDistributionDrawer = null;
  private MinMaxNormalizer tdErrorNormalized = null;
  private AbstractActorCritic actorCritic = null;
  private Listener<Clock> clockListener;
  protected boolean displayActionFlag;
  private final History actionHistory = new History(HistoryLength);
  private final History tdErrorHistory = new History(HistoryLength);
  private final Data2D data = new Data2D(HistoryLength);

  synchronized protected void updateData(AbstractNormalDistribution distribution) {
    actionHistory.append(distribution.a_t);
    if (actorCritic != null) {
      double delta_t = ((LinearLearner) actorCritic.critic).error();
      tdErrorNormalized.update(delta_t);
      tdErrorNormalized.update(-delta_t);
      tdErrorHistory.append(delta_t);
    }
  }

  @Override
  protected void setSettingBar(Composite settingBar) {
    Button displayAction = new Button(settingBar, SWT.CHECK);
    displayAction.setText("Actions");
    displayAction.setSelection(displayActionFlag);
    displayAction.addSelectionListener(new SelectionListener() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        displayActionFlag = !displayActionFlag;
      }

      @Override
      public void widgetDefaultSelected(SelectionEvent e) {
        displayActionFlag = !displayActionFlag;
      }
    });
    super.setSettingBar(settingBar);
  }

  @Override
  synchronized public boolean synchronize(AbstractNormalDistribution current) {
    if (centerAction.scaleEnabled())
      plot.resetAxes();
    if (plot.axes().y.transformationValid) {
      actionHistory.toArray(data.xdata);
      tdErrorHistory.toArray(data.ydata);
      float scale = plot.axes().y.max();
      for (int i = 0; i < data.ydata.length; i++)
        data.ydata[i] = tdErrorNormalized.normalize(data.ydata[i]) * scale;
    }
    normalDistributionDrawer.synchronize();
    return true;
  }

  @Override
  public void paint(PainterMonitor painterListener, GC gc) {
    plot.clear(gc);
    gc.setAntialias(ZephyrPlotting.preferredAntiAliasing() ? SWT.ON : SWT.OFF);
    gc.setLineWidth(ZephyrPlotting.preferredLineWidth());
    gc.setForeground(plot.colors.color(gc, Colors.COLOR_GRAY));
    gc.setForeground(plot.colors.color(gc, Colors.COLOR_BLACK));
    normalDistributionDrawer.draw(gc);
    if (displayActionFlag && plot.axes().y.transformationValid) {
      gc.setForeground(plot.colors.color(gc, Colors.COLOR_DARK_BLUE));
      plot.draw(gc, Drawers.Dots, data);
    }
  }

  @Override
  public void init(IViewSite site, IMemento memento) throws PartInitException {
    super.init(site, memento);
    if (memento == null)
      return;
    Boolean savedActionFlag = memento.getBoolean(ActionFlagKey);
    displayActionFlag = savedActionFlag != null ? savedActionFlag : false;
  }

  @Override
  public void saveState(IMemento memento) {
    super.saveState(memento);
    memento.putBoolean(ActionFlagKey, displayActionFlag);
  }

  @Override
  synchronized public void onInstanceSet(Clock clock, AbstractNormalDistribution distribution) {
    tdErrorNormalized = new MinMaxNormalizer(new Range(0, 1));
    ClassNode actorCriticParentNode = CodeTrees.findParent(codeNode(), ActorCritic.class);
    actorCritic = actorCriticParentNode != null ? (ActorCritic) actorCriticParentNode.instance() : null;
    clockListener = new ClockListener(distribution);
    clock.onTick.connect(clockListener);
    normalDistributionDrawer = new NormalDistributionDrawer(plot, distribution);
    super.onInstanceSet(clock, distribution);
  }

  @Override
  protected void setLayout(Clock clock, AbstractNormalDistribution current) {
    CodeNode codeNode = codeNode();
    setViewName(String.format("%s[%s]", current.getClass().getSimpleName(), codeNode.label()), "");
  }

  @Override
  synchronized public void onInstanceUnset(Clock clock) {
    clock.onTick.disconnect(clockListener);
    clockListener = null;
    tdErrorNormalized = null;
    actorCritic = null;
    actionHistory.reset();
    tdErrorHistory.reset();
    normalDistributionDrawer = null;
    super.onInstanceUnset(clock);
  }

  @Override
  protected boolean isInstanceSupported(Object instance) {
    return SupportedClass.isInstance(instance);
  }
}
