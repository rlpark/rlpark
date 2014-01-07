package rlpark.plugin.rltoysview.maze;

import rlpark.plugin.rltoys.problems.mazes.MazeProjector;
import rlpark.plugin.rltoysview.internal.adapters.FunctionAdapter;
import zephyr.plugin.plotting.internal.heatmap.MapData;
import zephyr.plugin.plotting.internal.heatmap.Mask2D;

@SuppressWarnings("restriction")
public abstract class MazeAdapter<T> extends FunctionAdapter<T> implements Mask2D {
  protected MapData maskData;
  protected MazeProjector mazeProjector;

  public MazeAdapter(String mementoLabel) {
    super(mementoLabel);
  }

  @Override
  public boolean isMasked(int x, int y) {
    return maskData.imageData()[x][y] != 0;
  }

  public void synchronize() {
    T function = lockLayoutFunction();
    if (function != null && maskData != null)
      synchronize(function);
    unlockLayoutFunction();
  }

  abstract protected void synchronize(T function);

  @Override
  public boolean layoutFunctionIsSet() {
    return maskData != null && super.layoutFunctionIsSet();
  }

  protected void setMazeLayout(MapData maskData, MazeProjector mazeProjector) {
    this.maskData = maskData;
    this.mazeProjector = mazeProjector;
    findLayoutFunctionNode();
  }
}
