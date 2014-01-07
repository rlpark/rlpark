package rlpark.plugin.rltoysview.maze;

import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.math.ranges.Range;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.problems.mazes.MazeFunction;
import rlpark.plugin.rltoys.problems.mazes.MazeProjector;
import zephyr.plugin.core.api.internal.codeparser.codetree.ClassNode;
import zephyr.plugin.core.internal.helpers.CodeNodeToInstance;
import zephyr.plugin.plotting.internal.heatmap.Interval;
import zephyr.plugin.plotting.internal.heatmap.MapData;

@SuppressWarnings("restriction")
public class MazeFunctionAdapter extends MazeAdapter<MazeFunction> implements CodeNodeToInstance<MazeFunction> {
  private MapData functionData;

  public MazeFunctionAdapter() {
    super("MazeFunctionAdapter");
    setCodeNodeToInstance(this);
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
  public void setMazeLayout(MapData maskData, MazeProjector mazeProjector) {
    super.setMazeLayout(maskData, mazeProjector);
    functionData = new MapData(maskData.resolutionX, maskData.resolutionY);
  }

  @Override
  public MazeFunction toInstance(ClassNode codeNode) {
    Object o = codeNode.instance();
    if (o instanceof MazeFunction)
      return (MazeFunction) o;
    if (!(o instanceof RealVector))
      return null;
    RealVector v = (RealVector) o;
    if (v.getDimension() == mazeProjector.projector().vectorSize())
      return newStateVectorAdapter(v);
    if (v.getDimension() == mazeProjector.toStateAction().vectorSize())
      return newStateActionVectorAdapter(v);
    return null;
  }

  private MazeFunction newStateActionVectorAdapter(final RealVector v) {
    return new MazeFunction() {
      @Override
      public float value(int x, int y) {
        double sum = 0.0;
        RealVector state = mazeProjector.toState(x, y);
        for (Action action : mazeProjector.maze().actions())
          sum += v.dotProduct(mazeProjector.stateAction(state, action));
        return (float) sum;
      }
    };
  }

  private MazeFunction newStateVectorAdapter(final RealVector v) {
    return new MazeFunction() {
      @Override
      public float value(int x, int y) {
        return (float) v.dotProduct(mazeProjector.toState(x, y));
      }
    };
  }
}
