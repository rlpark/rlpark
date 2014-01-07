package rlpark.plugin.rltoysview.maze;

import java.awt.Point;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;

import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.actions.ActionArray;
import rlpark.plugin.rltoys.envio.policy.Policy;
import rlpark.plugin.rltoys.math.ranges.Range;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.problems.mazes.Maze;
import rlpark.plugin.rltoys.problems.mazes.MazeFunction;
import rlpark.plugin.rltoys.problems.mazes.MazeProjector;
import zephyr.ZephyrPlotting;
import zephyr.plugin.core.api.internal.codeparser.codetree.ClassNode;
import zephyr.plugin.core.api.internal.codeparser.interfaces.CodeNode;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.internal.helpers.ClassViewProvider;
import zephyr.plugin.core.internal.helpers.CodeNodeToInstance;
import zephyr.plugin.core.internal.utils.Colors;
import zephyr.plugin.core.internal.views.helpers.ForegroundCanvasView;
import zephyr.plugin.plotting.internal.actions.DisplayGridAction;
import zephyr.plugin.plotting.internal.heatmap.ColorMapDescriptor;
import zephyr.plugin.plotting.internal.heatmap.Function2DBufferedDrawer;
import zephyr.plugin.plotting.internal.heatmap.Function2DCanvasDrawer;
import zephyr.plugin.plotting.internal.heatmap.Interval;
import zephyr.plugin.plotting.internal.heatmap.MapData;

@SuppressWarnings("restriction")
public class MazeView extends ForegroundCanvasView<MazeProjector> implements CodeNodeToInstance<MazeProjector> {
  static final private ColorMapDescriptor LayoutColorMap = new ColorMapDescriptor(new int[][] {
      new int[] { 255, 255, 255 }, new int[] { 0, 0, 0 } }, new int[] { 0, 0, 255 });
  static final private ColorMapDescriptor OverlayColorMap = new ColorMapDescriptor(new int[][] {
      new int[] { 255, 255, 255 }, new int[] { 255, 255, 0 } }, new int[] { 0, 0, 255 });

  public static class Provider extends ClassViewProvider {
    public Provider() {
      super(Maze.class, MazeProjector.class);
    }
  }

  static class PosToPix {
    final float pixelSizeX;
    final float pixelSizeY;
    private final int height;

    PosToPix(GC gc, MapData data) {
      Rectangle clipping = gc.getClipping();
      pixelSizeX = (float) clipping.width / data.resolutionX;
      height = clipping.height;
      pixelSizeY = (float) height / data.resolutionY;
    }

    int toX(int i) {
      return toX(i, 0);
    }

    int toX(int i, int offset) {
      return (int) (i * pixelSizeX + (pixelSizeX - offset) / 2);
    }

    int toY(int j) {
      return toY(j, 0);
    }

    int toY(int j, int offset) {
      return (int) (height - ((j + 1) * pixelSizeY - (pixelSizeY - offset) / 2));
    }

    int halfSize() {
      return Math.max(1, (int) (Math.min(pixelSizeX, pixelSizeY) / 2));
    }
  }

  private final Colors colors = new Colors();
  private final Function2DBufferedDrawer mazeDrawer = new Function2DBufferedDrawer(colors);
  private final Function2DCanvasDrawer mazeFunctionDrawer = new Function2DCanvasDrawer(colors);
  private final MazeFunctionAdapter valueAdapter = new MazeFunctionAdapter();
  private final MazePolicyAdapter policyAdapter = new MazePolicyAdapter();
  private MapData layoutData = null;
  private final DisplayGridAction gridAction = new DisplayGridAction();
  private double[] position;
  private boolean[][] endEpisodeData;

  public MazeView() {
    mazeDrawer.setColorMap(LayoutColorMap);
    mazeFunctionDrawer.setColorMap(OverlayColorMap);
    setNodeToInstance(this);
  }

  @Override
  protected void setToolbar(IToolBarManager toolbarManager) {
    toolbarManager.add(gridAction);
  }

  @Override
  protected boolean synchronize(MazeProjector mazeProjector) {
    position = mazeProjector.maze().lastStep().o_tp1;
    valueAdapter.synchronize();
    policyAdapter.synchronize();
    return true;
  }

  @Override
  protected void paint(GC gc) {
    gc.setBackground(colors.color(gc, Colors.COLOR_WHITE));
    Rectangle clipping = gc.getClipping();
    gc.fillRectangle(0, 0, clipping.width, clipping.height);
    if (layoutData == null)
      return;
    mazeDrawer.paint(gc, canvas, layoutData, false);
    if (valueAdapter.layoutFunctionIsSet())
      mazeFunctionDrawer.paint(gc, canvas, valueAdapter.functionData(), valueAdapter);
    if (gridAction.drawGrid())
      mazeDrawer.paintGrid(gc, canvas, layoutData);
    PosToPix ptp = new PosToPix(gc, layoutData);
    if (policyAdapter.layoutFunctionIsSet())
      drawPolicy(gc, ptp);
    if (endEpisodeData != null)
      drawEndPositions(gc, ptp);
    if (position != null)
      drawPosition(gc, position);
  }

  private void drawPolicy(GC gc, PosToPix ptp) {
    gc.setForeground(colors.color(gc, Colors.COLOR_DARK_RED));
    gc.setBackground(colors.color(gc, Colors.COLOR_DARK_RED));
    int halfSize = ptp.halfSize();
    gc.setLineWidth(Math.min(ZephyrPlotting.preferredLineWidth(), halfSize / 4));
    PolicyData policyData = policyAdapter.policyData();
    Action[] actions = policyData.actions();
    for (int i = 0; i < policyData.resolutionX; i++)
      for (int j = 0; j < policyData.resolutionY; j++) {
        if (policyAdapter.isMasked(i, j))
          continue;
        double[] probs = policyData.probabilities(i, j);
        for (int a = 0; a < probs.length; a++) {
          double length = halfSize * probs[a];
          drawAction(gc, ptp.toX(i, 0), ptp.toY(j, 0), length, ((ActionArray) actions[a]).actions);
        }
      }
  }

  private void drawAction(GC gc, int x, int y, double length, double[] action) {
    if (((int) length) == 0)
      return;
    if (action[0] + action[1] == 0) {
      gc.drawOval((int) (x - length / 2), (int) (y - length / 2), (int) length, (int) length);
      return;
    }
    int tx = (int) (x + length * action[0]);
    int ty = (int) (y + length * -action[1]);
    gc.drawLine(x, y, tx, ty);
    double arrowFactor = .2;
    double arrowLength = length * arrowFactor;
    gc.drawLine(tx, ty, (int) (action[1] * arrowLength + (x + tx) / 2.0),
                (int) (action[0] * arrowLength + (y + ty) / 2.0));
    gc.drawLine(tx, ty, (int) (-action[1] * arrowLength + (x + tx) / 2.0),
                (int) (-action[0] * arrowLength + (y + ty) / 2.0));
  }

  private void drawEndPositions(GC gc, PosToPix ptp) {
    gc.setBackground(colors.color(gc, Colors.COLOR_LIGHT_BLUE));
    int offset = ptp.halfSize();
    for (int i = 0; i < endEpisodeData.length; i++) {
      for (int j = 0; j < endEpisodeData[i].length; j++) {
        if (!endEpisodeData[i][j])
          continue;
        gc.fillRectangle(ptp.toX(i, offset), ptp.toY(j, offset), offset, offset);
      }
    }
  }

  private void drawPosition(GC gc, double[] position) {
    gc.setBackground(colors.color(gc, mazeDrawer.spriteColor()));
    PosToPix ptp = new PosToPix(gc, layoutData);
    int offset = ptp.halfSize();
    gc.fillOval(ptp.toX((int) position[0], offset), ptp.toY((int) position[1], offset), offset, offset);
  }

  @Override
  public void onInstanceSet(Clock clock, MazeProjector mazeProjector) {
    super.onInstanceSet(clock, mazeProjector);
    layoutData = createLayoutData(mazeProjector.maze());
    endEpisodeData = mazeProjector.maze().endEpisode();
    MapData maskData = createMaskData();
    valueAdapter.setMazeLayout(maskData, mazeProjector);
    policyAdapter.setMazeLayout(maskData, mazeProjector);
  }

  public MapData createMaskData() {
    MapData maskData = layoutData.copy();
    for (int i = 0; i < endEpisodeData.length; i++)
      for (int j = 0; j < endEpisodeData[i].length; j++)
        if (endEpisodeData[i][j])
          maskData.imageData()[i][j] = (float) 1.0;
    return maskData;
  }

  private MapData createLayoutData(Maze maze) {
    Point mazeSize = maze.size();
    MapData layoutData = new MapData(mazeSize.x, mazeSize.y);
    Range range = new Range();
    for (int i = 0; i < mazeSize.x; i++)
      for (int j = 0; j < mazeSize.y; j++) {
        byte value = maze.layout()[i][j];
        range.update(value);
        layoutData.imageData()[i][j] = value;
      }
    layoutData.setRangeValue(new Interval(range.min(), range.max()));
    return layoutData;
  }


  @Override
  public void onInstanceUnset(Clock clock) {
    super.onInstanceUnset(clock);
    layoutData = null;
    endEpisodeData = null;
  }

  @Override
  protected boolean isInstanceSupported(Object instance) {
    return (instance instanceof Maze) || (instance instanceof MazeFunction) || (instance instanceof Policy)
        || (instance instanceof RealVector);
  }

  @Override
  public void init(IViewSite site, IMemento memento) throws PartInitException {
    super.init(site, memento);
    gridAction.init(memento);
    valueAdapter.init(memento);
    policyAdapter.init(memento);
  }

  @Override
  public void saveState(IMemento memento) {
    super.saveState(memento);
    gridAction.saveState(memento);
    valueAdapter.saveState(memento);
    policyAdapter.saveState(memento);
  }

  @Override
  public void dispose() {
    super.dispose();
    colors.dispose();
  }

  @Override
  public void drop(CodeNode[] supported) {
    ClassNode classNode = (ClassNode) supported[0];
    Object instance = classNode.instance();
    if (instance instanceof Maze) {
      super.drop(supported);
      return;
    }
    if ((instance instanceof MazeFunction) || (instance instanceof RealVector))
      valueAdapter.setLayoutFunction(classNode);
    if (instance instanceof Policy)
      policyAdapter.setLayoutFunction(classNode);
  }

  @Override
  public MazeProjector toInstance(ClassNode node) {
    Object o = node.instance();
    if (o instanceof Maze)
      return new MazeProjector((Maze) o);
    return (MazeProjector) o;
  }
}
