package rlpark.plugin.rltoysview.internal.maze;

import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.problems.mazes.MazePolicy;
import zephyr.plugin.plotting.internal.heatmap.MapData;

@SuppressWarnings("restriction")
public class MazePolicyAdapter extends MazeAdapter<MazePolicy> {
  private PolicyData policyData;

  public MazePolicyAdapter() {
    super("MazePolicyAdapter");
  }

  @Override
  protected void synchronize(MazePolicy function) {
    for (int i = 0; i < policyData.resolutionX; i++)
      for (int j = 0; j < policyData.resolutionY; j++) {
        if (isMasked(i, j))
          continue;
        policyData.set(i, j, function.policy(i, j));
      }
  }

  public void setMazeLayout(MapData layoutData, Action[] actions) {
    super.setMazeLayout(layoutData);
    policyData = new PolicyData(layoutData.resolutionX, layoutData.resolutionY, actions);
  }

  public PolicyData policyData() {
    return policyData;
  }
}
