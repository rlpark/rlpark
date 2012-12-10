package rlpark.plugin.rltoysview.internal.maze;

import java.awt.Point;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;

import rlpark.plugin.rltoys.math.ranges.Range;
import rlpark.plugin.rltoys.problems.mazes.Maze;
import rlpark.plugin.rltoys.problems.mazes.MazeFunction;
import zephyr.plugin.core.api.internal.codeparser.codetree.ClassNode;
import zephyr.plugin.core.api.internal.codeparser.interfaces.CodeNode;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.internal.helpers.ClassViewProvider;
import zephyr.plugin.core.internal.utils.Colors;
import zephyr.plugin.core.internal.views.helpers.ForegroundCanvasView;
import zephyr.plugin.plotting.internal.actions.DisplayGridAction;
import zephyr.plugin.plotting.internal.heatmap.ColorMapDescriptor;
import zephyr.plugin.plotting.internal.heatmap.Function2DBufferedDrawer;
import zephyr.plugin.plotting.internal.heatmap.Function2DCanvasDrawer;
import zephyr.plugin.plotting.internal.heatmap.Interval;
import zephyr.plugin.plotting.internal.heatmap.MapData;

@SuppressWarnings("restriction")
public class MazeView extends ForegroundCanvasView<Maze> {
  static final private ColorMapDescriptor LayoutColorMap = new ColorMapDescriptor(new int[][] {
      new int[] { 255, 255, 255 }, new int[] { 0, 0, 0 } }, new int[] { 0, 0, 255 });
  static final private ColorMapDescriptor OverlayColorMap = new ColorMapDescriptor(new int[][] {
      new int[] { 255, 255, 255 }, new int[] { 255, 255, 0 } }, new int[] { 0, 0, 255 });

  public static class Provider extends ClassViewProvider {
    public Provider() {
      super(Maze.class);
    }
  }

  static class PosToPix {
    final int size;
    private final float pixelSizeX;
    private final float pixelSizeY;
    private final int height;

    PosToPix(GC gc, MapData data) {
      Rectangle clipping = gc.getClipping();
      pixelSizeX = (float) clipping.width / data.resolutionX;
      height = clipping.height;
      pixelSizeY = (float) height / data.resolutionY;
      size = (int) Math.max(1, Math.min(pixelSizeX / 2, pixelSizeY / 2));
    }

    int toX(int i) {
      return (int) (i * pixelSizeX + (pixelSizeX - size) / 2);
    }

    int toY(int j) {
      return (int) (height - ((j + 1) * pixelSizeY - (pixelSizeY - size) / 2));
    }
  }

  private final Colors colors = new Colors();
  private final Function2DBufferedDrawer mazeDrawer = new Function2DBufferedDrawer(colors);
  private final Function2DCanvasDrawer mazeFunctionDrawer = new Function2DCanvasDrawer(colors);
  private MapData layoutData = null;
  private final DisplayGridAction gridAction = new DisplayGridAction();
  private double[] position;
  private boolean[][] endEpisodeData;
  private MazeFunctionAdapter adapter = new MazeFunctionAdapter();

  public MazeView() {
    mazeDrawer.setColorMap(LayoutColorMap);
    mazeFunctionDrawer.setColorMap(OverlayColorMap);
  }

  @Override
  protected void setToolbar(IToolBarManager toolbarManager) {
    toolbarManager.add(gridAction);
  }

  @Override
  protected boolean synchronize(Maze maze) {
    position = maze.lastStep().o_tp1;
    adapter.synchronize();
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
    if (adapter.layoutFunctionIsSet())
      mazeFunctionDrawer.paint(gc, canvas, adapter.functionData(), adapter);
    if (gridAction.drawGrid())
      mazeDrawer.paintGrid(gc, canvas, layoutData);
    if (endEpisodeData != null)
      drawEndPositions(gc);
    if (position != null)
      drawPosition(gc, position);
  }

  private void drawEndPositions(GC gc) {
    gc.setBackground(colors.color(gc, Colors.COLOR_LIGHT_BLUE));
    PosToPix ptp = new PosToPix(gc, layoutData);
    for (int i = 0; i < endEpisodeData.length; i++) {
      for (int j = 0; j < endEpisodeData[i].length; j++) {
        if (!endEpisodeData[i][j])
          continue;
        gc.fillRectangle(ptp.toX(i), ptp.toY(j), ptp.size, ptp.size);
      }
    }
  }

  private void drawPosition(GC gc, double[] position) {
    gc.setBackground(colors.color(gc, mazeDrawer.spriteColor()));
    PosToPix ptp = new PosToPix(gc, layoutData);
    gc.fillOval(ptp.toX((int) position[0]), ptp.toY((int) position[1]), ptp.size, ptp.size);
  }

  @Override
  public void onInstanceSet(Clock clock, Maze maze) {
    super.onInstanceSet(clock, maze);
    layoutData = createLayoutData(maze);
    endEpisodeData = maze.endEpisode();
    adapter.setMazeLayout(layoutData);
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
    adapter = null;
  }

  @Override
  protected boolean isInstanceSupported(Object instance) {
    return (instance instanceof Maze) || (instance instanceof MazeFunction);
  }

  @Override
  public void init(IViewSite site, IMemento memento) throws PartInitException {
    super.init(site, memento);
    gridAction.init(memento);
    if (adapter != null)
      adapter.init(memento);
  }

  @Override
  public void saveState(IMemento memento) {
    super.saveState(memento);
    gridAction.saveState(memento);
    if (adapter != null)
      adapter.saveState(memento);
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
    if (instance instanceof MazeFunction)
      adapter.setLayoutFunction(classNode);
  }
}
