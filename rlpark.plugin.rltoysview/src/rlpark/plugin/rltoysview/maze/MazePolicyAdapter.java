package rlpark.plugin.rltoysview.maze;

import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.policy.Policy;
import rlpark.plugin.rltoys.problems.mazes.MazeProjector;
import zephyr.plugin.plotting.internal.heatmap.MapData;

@SuppressWarnings("restriction")
public class MazePolicyAdapter extends MazeAdapter<Policy> {
  private PolicyData policyData;

  public MazePolicyAdapter() {
    super("MazePolicyAdapter");
  }

  @Override
  protected void synchronize(Policy policy) {
    Action[] actions = mazeProjector.maze().actions();
    double[] probs = new double[actions.length];
    for (int i = 0; i < policyData.resolutionX; i++)
      for (int j = 0; j < policyData.resolutionY; j++) {
        if (isMasked(i, j))
          continue;
        policy.update(mazeProjector.toState(i, j));
        for (int a = 0; a < actions.length; a++)
          probs[a] = policy.pi(actions[a]);
        policyData.set(i, j, probs);
      }
  }

  @Override
  public void setMazeLayout(MapData maskData, MazeProjector mazeProjector) {
    super.setMazeLayout(maskData, mazeProjector);
    policyData = new PolicyData(maskData.resolutionX, maskData.resolutionY, mazeProjector.maze().actions());
  }

  public PolicyData policyData() {
    return policyData;
  }
}
