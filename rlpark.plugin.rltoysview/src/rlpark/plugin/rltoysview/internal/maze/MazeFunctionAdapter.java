package rlpark.plugin.rltoysview.internal.maze;

import rlpark.plugin.rltoys.math.ranges.Range;
import rlpark.plugin.rltoys.problems.mazes.MazeFunction;
import rlpark.plugin.rltoysview.internal.adapters.FunctionAdapter;
import zephyr.plugin.plotting.internal.heatmap.Interval;
import zephyr.plugin.plotting.internal.heatmap.MapData;
import zephyr.plugin.plotting.internal.heatmap.Mask2D;

@SuppressWarnings("restriction")
public class MazeFunctionAdapter extends FunctionAdapter<MazeFunction> implements Mask2D {
  private MapData layoutData;
  private MapData functionData;

  public MazeFunctionAdapter() {
    super("MazeFunctionAdapter");
  }

  @Override
  public boolean isMasked(int x, int y) {
    return layoutData.imageData()[x][y] != 0;
  }

  public void synchronize() {
    MazeFunction function = lockLayoutFunction();
    if (function != null && layoutData != null)
      synchronize(function);
    unlockLayoutFunction();
  }

  private void synchronize(MazeFunction function) {
    Range range = new Range();
    for (int i = 0; i < functionData.resolutionX; i++)
      for (int j = 0; j < functionData.resolutionY; j++)
        if (!isMasked(i, j)) {
          float value = function.value(i, j);
          range.update(value);
          functionData.imageData()[i][j] = value;
        }
    functionData.setRangeValue(new Interval(range.min(), range.max()));
  }

  public MapData functionData() {
    return functionData;
  }

  @Override
  public boolean layoutFunctionIsSet() {
    return layoutData != null && super.layoutFunctionIsSet();
  }

  public void setMazeLayout(MapData layoutData) {
    this.layoutData = layoutData;
    functionData = new MapData(layoutData.resolutionX, layoutData.resolutionY);
    findLayoutFunctionNode();
  }
}
