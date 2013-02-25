package rlpark.plugin.rltoysview.internal.maze;

import rlpark.plugin.rltoys.math.ranges.Range;
import rlpark.plugin.rltoys.problems.mazes.MazeFunction;
import zephyr.plugin.plotting.internal.heatmap.Interval;
import zephyr.plugin.plotting.internal.heatmap.MapData;

@SuppressWarnings("restriction")
public class MazeFunctionAdapter extends MazeAdapter<MazeFunction> {
  private MapData functionData;

  public MazeFunctionAdapter() {
    super("MazeFunctionAdapter");
  }

  @Override
  protected void synchronize(MazeFunction function) {
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
  public void setMazeLayout(MapData layoutData) {
    super.setMazeLayout(layoutData);
    functionData = new MapData(layoutData.resolutionX, layoutData.resolutionY);
  }
}
